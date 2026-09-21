package com.aditya.f1hub.integration.service.impl;

import com.aditya.f1hub.dto.result.RaceResultSyncResponse;
import com.aditya.f1hub.entity.Session;
import com.aditya.f1hub.entity.ConstructorStanding;
import com.aditya.f1hub.entity.DriverStanding;
import com.aditya.f1hub.integration.dto.CircuitSyncResponseDto;
import com.aditya.f1hub.integration.dto.ConstructorSyncResponseDto;
import com.aditya.f1hub.integration.dto.DriverSyncResponseDto;
import com.aditya.f1hub.integration.dto.RaceSyncResponseDto;
import com.aditya.f1hub.integration.dto.SeasonDataSyncResponseDto;
import com.aditya.f1hub.integration.dto.SeasonSyncResponseDto;
import com.aditya.f1hub.integration.dto.SessionSyncResponseDto;
import com.aditya.f1hub.integration.dto.SyncEntitySummaryDto;
import com.aditya.f1hub.integration.dto.SyncFailureDto;
import com.aditya.f1hub.integration.service.CircuitSyncService;
import com.aditya.f1hub.integration.service.ConstructorSyncService;
import com.aditya.f1hub.integration.service.DriverSyncService;
import com.aditya.f1hub.integration.service.RaceSyncService;
import com.aditya.f1hub.integration.service.SeasonDataSyncOrchestrator;
import com.aditya.f1hub.integration.service.SeasonSyncService;
import com.aditya.f1hub.integration.service.SessionSyncService;
import com.aditya.f1hub.repository.ConstructorStandingRepository;
import com.aditya.f1hub.repository.DriverStandingRepository;
import com.aditya.f1hub.repository.SessionRepository;
import com.aditya.f1hub.service.RaceResultService;
import com.aditya.f1hub.service.StandingsCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeasonDataSyncOrchestratorImpl
        implements SeasonDataSyncOrchestrator {

    private final SeasonSyncService seasonSyncService;
    private final CircuitSyncService circuitSyncService;
    private final RaceSyncService raceSyncService;
    private final SessionSyncService sessionSyncService;
    private final DriverSyncService driverSyncService;
    private final ConstructorSyncService constructorSyncService;

    private final RaceResultService raceResultService;
    private final StandingsCalculationService standingsCalculationService;

    private final SessionRepository sessionRepository;
    private final DriverStandingRepository driverStandingRepository;
    private final ConstructorStandingRepository constructorStandingRepository;

    @Override
    public SeasonDataSyncResponseDto synchronizeSeason(Integer year) {

        validateYear(year);

        long startTime = System.currentTimeMillis();

        List<SyncFailureDto> failures = new ArrayList<>();

        SyncEntitySummaryDto races = emptySummary();
        SyncEntitySummaryDto sessions = emptySummary();
        SyncEntitySummaryDto drivers = emptySummary();
        SyncEntitySummaryDto constructors = emptySummary();
        SyncEntitySummaryDto results = emptySummary();

        Integer driverStandingsUpdated = 0;
        Integer constructorStandingsUpdated = 0;

        log.info(
                "Starting master season synchronization: year={}",
                year
        );

        /*
         * =========================================================
         * 1. SEASON
         * =========================================================
         */

        try {

            SeasonSyncResponseDto seasonResponse =
                    seasonSyncService.synchronizeSeason(year);

            log.info(
                    "Season synchronization completed: year={}, created={}, existing={}",
                    year,
                    seasonResponse.isCreated(),
                    seasonResponse.isExisting()
            );

        } catch (Exception exception) {

            addFailure(
                    failures,
                    "SEASON",
                    String.valueOf(year),
                    exception
            );

            log.error(
                    "Season synchronization failed: year={}",
                    year,
                    exception
            );

            return buildResponse(
                    year,
                    startTime,
                    races,
                    sessions,
                    drivers,
                    constructors,
                    results,
                    driverStandingsUpdated,
                    constructorStandingsUpdated,
                    failures
            );
        }

        /*
         * =========================================================
         * 2. CIRCUITS
         * =========================================================
         */

        try {

            CircuitSyncResponseDto circuitResponse =
                    circuitSyncService.synchronizeCircuits();

            log.info(
                    "Circuit synchronization completed: "
                            + "fetched={}, new={}, existing={}, failed={}",
                    circuitResponse.getTotalFetched(),
                    circuitResponse.getNewCircuits(),
                    circuitResponse.getExistingCircuits(),
                    circuitResponse.getFailed()
            );

        } catch (Exception exception) {

            addFailure(
                    failures,
                    "CIRCUITS",
                    String.valueOf(year),
                    exception
            );

            log.error(
                    "Circuit synchronization failed: year={}",
                    year,
                    exception
            );
        }

        /*
         * =========================================================
         * 3. RACES
         * =========================================================
         */

        try {

            RaceSyncResponseDto raceResponse =
                    raceSyncService.synchronizeRaces(year);

            races = SyncEntitySummaryDto.builder()
                    .fetched(raceResponse.getTotalFetched())
                    .created(raceResponse.getNewRaces())
                    .updated(raceResponse.getExistingRaces())
                    .failed(raceResponse.getFailed())
                    .build();

        } catch (Exception exception) {

            addFailure(
                    failures,
                    "RACES",
                    String.valueOf(year),
                    exception
            );

            log.error(
                    "Race synchronization failed: year={}",
                    year,
                    exception
            );
        }

        /*
         * =========================================================
         * 4. SESSIONS
         * =========================================================
         */

        try {

            SessionSyncResponseDto sessionResponse =
                    sessionSyncService.synchronizeSessions(year);

            sessions = SyncEntitySummaryDto.builder()
                    .fetched(sessionResponse.getTotalFetched())
                    .created(sessionResponse.getNewSessions())
                    .updated(sessionResponse.getExistingSessions())
                    .failed(sessionResponse.getFailed())
                    .build();

        } catch (Exception exception) {

            addFailure(
                    failures,
                    "SESSIONS",
                    String.valueOf(year),
                    exception
            );

            log.error(
                    "Session synchronization failed: year={}",
                    year,
                    exception
            );
        }

        /*
         * =========================================================
         * 5. DRIVERS
         * =========================================================
         */

        try {

            DriverSyncResponseDto driverResponse =
                    driverSyncService.synchronizeDrivers();

            drivers = SyncEntitySummaryDto.builder()
                    .fetched(driverResponse.getTotalFetched())
                    .created(driverResponse.getNewDrivers())
                    .updated(driverResponse.getExistingDrivers())
                    .failed(driverResponse.getFailedDrivers())
                    .build();

        } catch (Exception exception) {

            addFailure(
                    failures,
                    "DRIVERS",
                    String.valueOf(year),
                    exception
            );

            log.error(
                    "Driver synchronization failed: year={}",
                    year,
                    exception
            );
        }

        /*
         * =========================================================
         * 6. CONSTRUCTORS
         * =========================================================
         */

        try {

            ConstructorSyncResponseDto constructorResponse =
                    constructorSyncService.synchronizeConstructors();

            constructors = SyncEntitySummaryDto.builder()
                    .fetched(constructorResponse.getTotalFetched())
                    .created(constructorResponse.getNewConstructors())
                    .updated(constructorResponse.getExistingConstructors())
                    .failed(constructorResponse.getFailedConstructors())
                    .build();

        } catch (Exception exception) {

            addFailure(
                    failures,
                    "CONSTRUCTORS",
                    String.valueOf(year),
                    exception
            );

            log.error(
                    "Constructor synchronization failed: year={}",
                    year,
                    exception
            );
        }

        /*
         * =========================================================
         * 7. LOAD ALL SEASON SESSIONS
         * =========================================================
         */

        List<Session> seasonSessions;

        try {

            seasonSessions =
                    sessionRepository
                            .findByRaceSeasonYearOrderByStartTimeAsc(year);

        } catch (Exception exception) {

            addFailure(
                    failures,
                    "SESSION_LOOKUP",
                    String.valueOf(year),
                    exception
            );

            log.error(
                    "Failed to load sessions for season: year={}",
                    year,
                    exception
            );

            return buildResponse(
                    year,
                    startTime,
                    races,
                    sessions,
                    drivers,
                    constructors,
                    results,
                    driverStandingsUpdated,
                    constructorStandingsUpdated,
                    failures
            );
        }

        /*
         * =========================================================
         * 8. SYNCHRONIZE RESULTS FOR EVERY SESSION
         * =========================================================
         */

        results.setFetched(seasonSessions.size());

        for (Session session : seasonSessions) {

            if (!shouldSynchronizeResults(session)) {

                log.debug(
                        "Skipping result synchronization for sessionId={}, sessionType={}",
                        session.getId(),
                        session.getSessionType()
                );

                continue;
            }

            try {

                RaceResultSyncResponse resultResponse =
                        raceResultService.synchronizeResults(
                                session.getId()
                        );

                if (resultResponse == null) {

                    results.setFailed(
                            results.getFailed() + 1
                    );

                    addFailure(
                            failures,
                            "RESULTS",
                            String.valueOf(session.getId()),
                            new IllegalStateException(
                                    "Result synchronization returned null response."
                            )
                    );

                    continue;
                }

                results.setCreated(
                        results.getCreated()
                                + safeValue(
                                resultResponse.getCreatedCount()
                        )
                );

                results.setUpdated(
                        results.getUpdated()
                                + safeValue(
                                resultResponse.getUpdatedCount()
                        )
                );

            } catch (Exception exception) {

                results.setFailed(
                        results.getFailed() + 1
                );

                addFailure(
                        failures,
                        "RESULTS",
                        String.valueOf(session.getId()),
                        exception
                );

                log.error(
                        "Result synchronization failed: sessionId={}",
                        session.getId(),
                        exception
                );
            }
        }

        /*
         * =========================================================
         * 9. CALCULATE STANDINGS
         * =========================================================
         */

        try {

            Long seasonId =
                    seasonSessions.stream()
                            .filter(session ->
                                    session.getRace() != null
                                            && session.getRace().getSeason() != null
                            )
                            .map(session ->
                                    session.getRace()
                                            .getSeason()
                                            .getId()
                            )
                            .findFirst()
                            .orElse(null);

            if (seasonId == null) {

                throw new IllegalStateException(
                        "Unable to resolve season ID for standings calculation."
                );
            }

            standingsCalculationService.calculateStandings(
                    seasonId
            );

            List<DriverStanding> driverStandings =
                    driverStandingRepository
                            .findBySeasonIdOrderByPositionAsc(
                                    seasonId
                            );

            List<ConstructorStanding> constructorStandings =
                    constructorStandingRepository
                            .findBySeasonIdOrderByPositionAsc(
                                    seasonId
                            );

            driverStandingsUpdated =
                    driverStandings.size();

            constructorStandingsUpdated =
                    constructorStandings.size();

        } catch (Exception exception) {

            addFailure(
                    failures,
                    "STANDINGS",
                    String.valueOf(year),
                    exception
            );

            log.error(
                    "Standings calculation failed: year={}",
                    year,
                    exception
            );
        }

        /*
         * =========================================================
         * 10. BUILD RESPONSE
         * =========================================================
         */

        SeasonDataSyncResponseDto response =
                buildResponse(
                        year,
                        startTime,
                        races,
                        sessions,
                        drivers,
                        constructors,
                        results,
                        driverStandingsUpdated,
                        constructorStandingsUpdated,
                        failures
                );

        log.info(
                "Master season synchronization completed: "
                        + "year={}, status={}, durationMs={}, failures={}",
                year,
                response.getStatus(),
                response.getDurationMs(),
                failures.size()
        );

        return response;
    }

    private boolean shouldSynchronizeResults(Session session) {

        if (session == null
                || session.getSessionType() == null) {
            return false;
        }

        String sessionType =
                session.getSessionType()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        return "race".equals(sessionType)
                || "sprint".equals(sessionType)
                || "qualifying".equals(sessionType);
    }

    private void validateYear(Integer year) {

        if (year == null || year < 1950) {

            throw new IllegalArgumentException(
                    "A valid season year is required."
            );
        }
    }

    private SyncEntitySummaryDto emptySummary() {

        return SyncEntitySummaryDto.builder()
                .fetched(0)
                .created(0)
                .updated(0)
                .failed(0)
                .build();
    }

    private int safeValue(Integer value) {

        return value == null ? 0 : value;
    }

    private void addFailure(
            List<SyncFailureDto> failures,
            String entityType,
            String entityId,
            Exception exception
    ) {

        String message =
                exception.getMessage() == null
                        ? exception.getClass().getSimpleName()
                        : exception.getMessage();

        failures.add(
                SyncFailureDto.builder()
                        .entityType(entityType)
                        .entityId(entityId)
                        .message(message)
                        .build()
        );
    }

    private SeasonDataSyncResponseDto buildResponse(
            Integer year,
            long startTime,
            SyncEntitySummaryDto races,
            SyncEntitySummaryDto sessions,
            SyncEntitySummaryDto drivers,
            SyncEntitySummaryDto constructors,
            SyncEntitySummaryDto results,
            Integer driverStandingsUpdated,
            Integer constructorStandingsUpdated,
            List<SyncFailureDto> failures
    ) {

        String status;

        if (failures.isEmpty()) {
            status = "SUCCESS";
        } else {
            status = "PARTIAL_SUCCESS";
        }

        return SeasonDataSyncResponseDto.builder()
                .seasonYear(year)
                .status(status)
                .durationMs(
                        System.currentTimeMillis() - startTime
                )
                .races(races)
                .sessions(sessions)
                .drivers(drivers)
                .constructors(constructors)
                .results(results)
                .driverStandingsUpdated(
                        driverStandingsUpdated
                )
                .constructorStandingsUpdated(
                        constructorStandingsUpdated
                )
                .failures(failures)
                .build();
    }
}