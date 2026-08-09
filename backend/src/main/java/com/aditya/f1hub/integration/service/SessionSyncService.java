package com.aditya.f1hub.integration.service;

import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.Session;
import com.aditya.f1hub.integration.client.OpenF1Client;
import com.aditya.f1hub.integration.dto.OpenF1SessionDto;
import com.aditya.f1hub.integration.dto.SessionSyncResponseDto;
import com.aditya.f1hub.integration.mapper.OpenF1SessionMapper;
import com.aditya.f1hub.repository.RaceRepository;
import com.aditya.f1hub.repository.SessionRepository;
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
public class SessionSyncService {

    private final OpenF1Client openF1Client;
    private final SessionRepository sessionRepository;
    private final RaceRepository raceRepository;
    private final OpenF1SessionMapper sessionMapper;

    /**
     * Synchronizes Session data from OpenF1
     * for a specific season year.
     *
     * @param year season year to synchronize
     * @return synchronization summary
     */
    @Transactional
    public SessionSyncResponseDto synchronizeSessions(Integer year) {

        if (year == null || year < 1950) {
            throw new IllegalArgumentException(
                    "A valid season year is required."
            );
        }

        List<OpenF1SessionDto> sessionDtos =
                openF1Client.getSessions(year);

        int totalFetched = sessionDtos.size();
        int newSessions = 0;
        int existingSessions = 0;
        int failed = 0;

        Set<String> processedSessionIds =
                new HashSet<>();

        for (OpenF1SessionDto dto : sessionDtos) {

            try {

                /*
                 * session_key uniquely identifies
                 * a specific F1 session.
                 */
                if (dto.getSessionKey() == null) {

                    failed++;

                    log.warn(
                            "Skipping session because " +
                                    "session_key is missing."
                    );

                    continue;
                }

                String externalSessionId =
                        String.valueOf(dto.getSessionKey());

                /*
                 * Prevent duplicate processing if the same
                 * session appears more than once in the response.
                 */
                if (!processedSessionIds.add(
                        externalSessionId)) {

                    log.warn(
                            "Skipping duplicate session " +
                                    "in OpenF1 response. " +
                                    "Session ID: {}",
                            externalSessionId
                    );

                    continue;
                }

                /*
                 * A valid Session must contain a year.
                 */
                if (dto.getYear() == null) {

                    failed++;

                    log.warn(
                            "Skipping session because year " +
                                    "is missing. Session ID: {}",
                            externalSessionId
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
                            "Skipping session because session " +
                                    "year {} does not match " +
                                    "requested year {}. " +
                                    "Session ID: {}",
                            dto.getYear(),
                            year,
                            externalSessionId
                    );

                    continue;
                }

                /*
                 * meeting_key identifies the Race/meeting
                 * to which this Session belongs.
                 */
                if (dto.getMeetingKey() == null) {

                    failed++;

                    log.warn(
                            "Skipping session because " +
                                    "meeting_key is missing. " +
                                    "Session ID: {}",
                            externalSessionId
                    );

                    continue;
                }

                String externalMeetingId =
                        String.valueOf(dto.getMeetingKey());

                /*
                 * Resolve the parent Race using the
                 * OpenF1 meeting_key.
                 */
                Race race =
                        raceRepository
                                .findByExternalMeetingId(
                                        externalMeetingId)
                                .orElseThrow(() ->
                                        new IllegalStateException(
                                                "Race not found for " +
                                                        "externalMeetingId: "
                                                        + externalMeetingId
                                        ));

                /*
                 * Find existing Session using the
                 * OpenF1 session_key.
                 */
                Session existingSession =
                        sessionRepository
                                .findByExternalSessionId(
                                        externalSessionId)
                                .orElse(null);

                if (existingSession == null) {

                    /*
                     * Create new Session.
                     */
                    Session session =
                            sessionMapper.toEntity(
                                    dto,
                                    race
                            );

                    sessionRepository.save(session);

                    newSessions++;

                    log.info(
                            "Session synchronized successfully: {}",
                            session.getSessionName()
                    );

                } else {

                    /*
                     * Update existing Session.
                     *
                     * F1Hub-owned fields such as active
                     * are intentionally preserved.
                     */
                    sessionMapper.updateEntityFromDto(
                            dto,
                            existingSession,
                            race
                    );

                    sessionRepository.save(existingSession);

                    existingSessions++;

                    log.info(
                            "Session already exists and was updated: {}",
                            existingSession.getSessionName()
                    );
                }

            } catch (Exception exception) {

                failed++;

                log.error(
                        "Failed to synchronize session. " +
                                "Session ID: {}, Name: {}",
                        dto.getSessionKey(),
                        dto.getSessionName(),
                        exception
                );
            }
        }

        log.info(
                "Session synchronization completed. " +
                        "Total: {}, New: {}, Existing: {}, Failed: {}",
                totalFetched,
                newSessions,
                existingSessions,
                failed
        );

        return SessionSyncResponseDto.builder()
                .totalFetched(totalFetched)
                .newSessions(newSessions)
                .existingSessions(existingSessions)
                .failed(failed)
                .build();
    }
}