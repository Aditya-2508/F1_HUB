package com.aditya.f1hub.integration.service;

import com.aditya.f1hub.entity.Circuit;
import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.Season;
import com.aditya.f1hub.integration.client.OpenF1Client;
import com.aditya.f1hub.integration.dto.OpenF1RaceDto;
import com.aditya.f1hub.integration.dto.RaceSyncResponseDto;
import com.aditya.f1hub.integration.mapper.OpenF1RaceMapper;
import com.aditya.f1hub.repository.CircuitRepository;
import com.aditya.f1hub.repository.RaceRepository;
import com.aditya.f1hub.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RaceSyncService {

    private final OpenF1Client openF1Client;
    private final RaceRepository raceRepository;
    private final SeasonRepository seasonRepository;
    private final CircuitRepository circuitRepository;
    private final OpenF1RaceMapper raceMapper;

    /**
     * Synchronizes Race data from OpenF1 for a specific season year.
     *
     * <p>
     * Race round numbers are derived from the chronological order
     * of valid OpenF1 race meetings.
     * </p>
     *
     * @param year season year to synchronize
     * @return synchronization summary
     */
    @Transactional
    public RaceSyncResponseDto synchronizeRaces(Integer year) {

        if (year == null || year < 1950) {
            throw new IllegalArgumentException(
                    "A valid season year is required."
            );
        }

        /*
         * Fetch race meetings for the requested season.
         */
        List<OpenF1RaceDto> raceDtos =
                openF1Client.getRaces(year);

        int totalFetched = raceDtos.size();
        int newRaces = 0;
        int existingRaces = 0;
        int failed = 0;

        /*
         * Ensure the requested Season exists before
         * processing dependent Race entities.
         *
         * The Season is resolved only once instead of
         * querying the database for every race.
         */
        Season season = getOrCreateSeason(year);

        /*
         * Sort race meetings chronologically.
         *
         * OpenF1 date_start values contain an offset, therefore
         * OffsetDateTime is used instead of plain String sorting.
         */
        raceDtos = raceDtos.stream()
                .sorted(
                        Comparator.comparing(
                                dto -> parseDateStart(dto.getDateStart()),
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()
                                )
                        )
                )
                .toList();

        /*
         * Calculate round numbers based on chronological order.
         *
         * Example:
         *
         * Australian GP  -> Round 1
         * Chinese GP     -> Round 2
         * Japanese GP    -> Round 3
         *
         * Duplicate meeting IDs are ignored so that one meeting
         * cannot consume multiple round numbers.
         */
        Map<String, Integer> roundNumberByMeetingId =
                calculateRoundNumbers(raceDtos);

        /*
         * Prevent duplicate processing if the same
         * meeting appears more than once in the response.
         */
        Set<String> processedMeetingIds = new HashSet<>();

        for (OpenF1RaceDto dto : raceDtos) {

            try {

                /*
                 * meeting_key uniquely identifies
                 * a specific Grand Prix meeting.
                 */
                if (dto.getMeetingKey() == null) {

                    failed++;

                    log.warn(
                            "Skipping race because meeting_key is missing."
                    );

                    continue;
                }

                String externalMeetingId =
                        String.valueOf(dto.getMeetingKey());

                /*
                 * Prevent duplicate processing if the same
                 * meeting appears more than once in the response.
                 */
                if (!processedMeetingIds.add(externalMeetingId)) {

                    log.warn(
                            "Skipping duplicate race in OpenF1 response. " +
                                    "Meeting ID: {}",
                            externalMeetingId
                    );

                    continue;
                }

                /*
                 * A valid Race must contain a season year.
                 */
                if (dto.getYear() == null) {

                    failed++;

                    log.warn(
                            "Skipping race because year is missing. " +
                                    "Meeting ID: {}",
                            externalMeetingId
                    );

                    continue;
                }

                /*
                 * Ensure that the response belongs to the
                 * season that was requested.
                 */
                if (!year.equals(dto.getYear())) {

                    failed++;

                    log.warn(
                            "Skipping race because meeting year {} " +
                                    "does not match requested year {}. " +
                                    "Meeting ID: {}",
                            dto.getYear(),
                            year,
                            externalMeetingId
                    );

                    continue;
                }

                /*
                 * circuit_key identifies the actual circuit.
                 *
                 * OpenF1 circuit_key
                 *        ↓
                 * Circuit.externalCircuitId
                 */
                if (dto.getCircuitKey() == null) {

                    failed++;

                    log.warn(
                            "Skipping race because circuit_key is missing. " +
                                    "Meeting ID: {}",
                            externalMeetingId
                    );

                    continue;
                }

                String externalCircuitId =
                        String.valueOf(dto.getCircuitKey());

                Circuit circuit =
                        circuitRepository
                                .findByExternalCircuitId(
                                        externalCircuitId
                                )
                                .orElseThrow(() ->
                                        new IllegalStateException(
                                                "Circuit not found for " +
                                                        "externalCircuitId: "
                                                        + externalCircuitId
                                        )
                                );

                /*
                 * Find calculated round number.
                 *
                 * A valid meeting should always have a round number.
                 */
                Integer roundNumber =
                        roundNumberByMeetingId.get(externalMeetingId);

                boolean nonChampionshipMeeting =
                        isNonChampionshipMeeting(dto);

                if (!nonChampionshipMeeting && roundNumber == null) {

                    failed++;

                    log.warn(
                            "Skipping race because round number " +
                                    "could not be calculated. " +
                                    "Meeting ID: {}",
                            externalMeetingId
                    );

                    continue;
                }

                /*
                 * Find existing Race using the OpenF1
                 * meeting_key.
                 */
                Race existingRace =
                        raceRepository
                                .findByExternalMeetingId(
                                        externalMeetingId
                                )
                                .orElse(null);

                if (existingRace == null) {

                    /*
                     * Create new Race.
                     */
                    Race race =
                            raceMapper.toEntity(
                                    dto,
                                    season,
                                    circuit
                            );

                    /*
                     * Round number is derived by F1Hub,
                     * not supplied by OpenF1.
                     */
                    race.setRoundNumber(roundNumber);

                    raceRepository.save(race);

                    newRaces++;

                    log.info(
                            "Race synchronized successfully: {} " +
                                    "(Round {})",
                            race.getName(),
                            roundNumber
                    );

                } else {

                    /*
                     * Update existing Race.
                     *
                     * The mapper updates only fields controlled
                     * by OpenF1.
                     */
                    raceMapper.updateEntityFromDto(
                            dto,
                            existingRace,
                            season,
                            circuit
                    );

                    /*
                     * Round number is a F1Hub-derived field.
                     *
                     * It is recalculated during synchronization
                     * so existing records with null or stale
                     * round numbers are corrected automatically.
                     */
                    existingRace.setRoundNumber(roundNumber);

                    raceRepository.save(existingRace);

                    existingRaces++;

                    log.info(
                            "Race already exists and was updated: {} " +
                                    "(Round {})",
                            existingRace.getName(),
                            roundNumber
                    );
                }

            } catch (Exception exception) {

                failed++;

                log.error(
                        "Failed to synchronize race. " +
                                "Meeting ID: {}, Name: {}",
                        dto.getMeetingKey(),
                        dto.getMeetingName(),
                        exception
                );
            }
        }

        log.info(
                "Race synchronization completed. " +
                        "Total: {}, New: {}, Existing: {}, Failed: {}",
                totalFetched,
                newRaces,
                existingRaces,
                failed
        );

        return RaceSyncResponseDto.builder()
                .totalFetched(totalFetched)
                .newRaces(newRaces)
                .existingRaces(existingRaces)
                .failed(failed)
                .build();
    }

    /**
     * Calculates chronological round numbers for race meetings.
     *
     * <p>
     * The first unique meeting in chronological order receives
     * round 1, the second receives round 2, and so on.
     * </p>
     *
     * @param raceDtos OpenF1 race meetings
     * @return mapping of external meeting ID to round number
     */
    private Map<String, Integer> calculateRoundNumbers(
            List<OpenF1RaceDto> raceDtos
    ) {
        Map<String, Integer> roundNumbers = new HashMap<>();

        int roundNumber = 1;

        for (OpenF1RaceDto dto : raceDtos) {

            if (dto == null || dto.getMeetingKey() == null) {
                continue;
            }

            if (isNonChampionshipMeeting(dto)) {
                continue;
            }

            roundNumbers.put(
                    String.valueOf(dto.getMeetingKey()),
                    roundNumber
            );

            roundNumber++;
        }

        return roundNumbers;
    }

    private boolean isNonChampionshipMeeting(OpenF1RaceDto dto) {
        return dto.getMeetingName() != null
                && dto.getMeetingName().trim()
                .equalsIgnoreCase("Pre-Season Testing");
    }

    /**
     * Parses OpenF1 date_start into OffsetDateTime.
     *
     * <p>
     * Invalid or missing dates return null so that the
     * synchronization process can continue safely.
     * </p>
     *
     * @param dateStart OpenF1 date_start value
     * @return parsed date-time or null
     */
    private OffsetDateTime parseDateStart(String dateStart) {

        if (dateStart == null || dateStart.isBlank()) {
            return null;
        }

        try {
            return OffsetDateTime.parse(dateStart);
        } catch (Exception exception) {

            log.warn(
                    "Unable to parse race date_start: {}",
                    dateStart
            );

            return null;
        }
    }

    /**
     * Finds an existing Season by year or creates it
     * when it does not exist.
     *
     * <p>
     * This method is intentionally kept inside RaceSyncService
     * because Season currently has no independent synchronization
     * workflow in F1Hub.
     * </p>
     *
     * @param year season year
     * @return existing or newly created Season
     */
    private Season getOrCreateSeason(Integer year) {

        return seasonRepository.findByYear(year)
                .orElseGet(() -> {

                    Season season = new Season();

                    season.setYear(year);
                    season.setActive(true);

                    Season savedSeason =
                            seasonRepository.save(season);

                    log.info(
                            "Season created automatically during " +
                                    "race synchronization: {}",
                            year
                    );

                    return savedSeason;
                });
    }
}