package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.RaceResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RaceResultRepository
        extends JpaRepository<RaceResult, Long>,
        JpaSpecificationExecutor<RaceResult> {

    @Query("""
        SELECT result
        FROM RaceResult result
        JOIN FETCH result.session session
        JOIN FETCH session.race race
        JOIN FETCH result.driver driver
        JOIN FETCH result.constructor constructor
        WHERE race.season.id = :seasonId
        ORDER BY race.roundNumber ASC,
                 session.startTime ASC,
                 result.position ASC
        """)
    List<RaceResult> findAllBySeasonId(@Param("seasonId") Long seasonId);

    List<RaceResult> findByDriverId(Long driverId);

    List<RaceResult> findByConstructorId(Long constructorId);

    List<RaceResult> findBySessionId(Long sessionId);

    boolean existsBySessionIdAndDriverId(
            Long sessionId,
            Long driverId
    );

    Optional<RaceResult> findBySessionIdAndDriverId(
            Long sessionId,
            Long driverId
    );

    /**
     * Retrieves aggregate career statistics for a driver.
     *
     * Result order:
     * 1. Race wins
     * 2. Race podiums
     * 3. Qualifying poles
     * 4. Championship points
     *
     * Native SQL is intentionally used here so the aggregate result
     * is returned directly as a four-column Object[].
     */
    @Query("""
    SELECT
        COUNT(
            CASE
                WHEN LOWER(session.sessionType) = 'race'
                     AND result.position = 1
                THEN 1
            END
        ) AS raceWins,

        COUNT(
            CASE
                WHEN LOWER(session.sessionType) = 'race'
                     AND result.position <= 3
                THEN 1
            END
        ) AS racePodiums,

        COUNT(
            CASE
                WHEN LOWER(session.sessionType) = 'qualifying'
                     AND result.position = 1
                THEN 1
            END
        ) AS qualifyingPoles,

        COALESCE(
            SUM(
                CASE
                    WHEN LOWER(session.sessionType) IN ('race', 'sprint')
                    THEN result.points
                    ELSE 0
                END
            ),
            0
        ) AS championshipPoints

    FROM RaceResult result
    JOIN result.session session
    WHERE result.driver.id = :driverId
    """)
    DriverCareerStatisticsProjection findDriverCareerStatistics(
            @Param("driverId") Long driverId
    );

    @Query("""
        SELECT COUNT(result)
        FROM RaceResult result
        JOIN result.session session
        WHERE result.driver.id = :driverId
          AND LOWER(session.sessionType) = 'race'
          AND result.fastestLapTimeSeconds IS NOT NULL
          AND result.fastestLapTimeSeconds = (
              SELECT MIN(otherResult.fastestLapTimeSeconds)
              FROM RaceResult otherResult
              JOIN otherResult.session otherSession
              WHERE otherSession.race.id = session.race.id
                AND LOWER(otherSession.sessionType) = 'race'
                AND otherResult.fastestLapTimeSeconds IS NOT NULL
          )
        """)
    long countDriverFastestLaps(@Param("driverId") Long driverId);

    /**
     * Counts constructor podium finishes across race sessions.
     *
     * A podium is a race result with a finishing position
     * from 1st through 3rd.
     */
    @Query("""
        SELECT COUNT(result)
        FROM RaceResult result
        JOIN result.session session
        WHERE result.constructor.id = :constructorId
          AND LOWER(session.sessionType) = 'race'
          AND result.position <= 3
        """)
    long countConstructorPodiums(
            @Param("constructorId") Long constructorId
    );
}