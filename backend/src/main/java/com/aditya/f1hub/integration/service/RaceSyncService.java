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

import java.util.HashSet;
import java.util.List;
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

        List<OpenF1RaceDto> raceDtos =
                openF1Client.getRaces(year);

        int totalFetched = raceDtos.size();
        int newRaces = 0;
        int existingRaces = 0;
        int failed = 0;

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
                 * Resolve the Season using the championship year.
                 */
                Season season =
                        seasonRepository.findByYear(dto.getYear())
                                .orElseThrow(() ->
                                        new IllegalStateException(
                                                "Season not found for year: "
                                                        + dto.getYear()
                                        )
                                );

                /*
                 * circuit_key identifies the actual circuit.
                 *
                 * It maps to:
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
                                        externalCircuitId)
                                .orElseThrow(() ->
                                        new IllegalStateException(
                                                "Circuit not found for " +
                                                        "externalCircuitId: "
                                                        + externalCircuitId
                                        ));

                /*
                 * Find existing Race using the OpenF1
                 * meeting_key.
                 */
                Race existingRace =
                        raceRepository
                                .findByExternalMeetingId(
                                        externalMeetingId)
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

                    raceRepository.save(race);

                    newRaces++;

                    log.info(
                            "Race synchronized successfully: {}",
                            race.getName()
                    );

                } else {

                    /*
                     * Update existing Race.
                     *
                     * The mapper updates only fields controlled
                     * by OpenF1. Internal F1Hub fields such as
                     * active and roundNumber are preserved.
                     */
                    raceMapper.updateEntityFromDto(
                            dto,
                            existingRace,
                            season,
                            circuit
                    );

                    raceRepository.save(existingRace);

                    existingRaces++;

                    log.info(
                            "Race already exists and was updated: {}",
                            existingRace.getName()
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
}