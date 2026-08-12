package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.RaceResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface RaceResultRepository extends JpaRepository<RaceResult, Long> {

    /**
     * Finds all results belonging to a specific session.
     */
    List<RaceResult> findBySessionId(Long sessionId);

    /**
     * Finds all results belonging to a specific driver.
     */
    List<RaceResult> findByDriverId(Long driverId);

    /**
     * Finds all results belonging to a specific constructor.
     */
    List<RaceResult> findByConstructorId(Long constructorId);

    /**
     * Finds a result for a specific driver in a specific session.
     *
     * This supports synchronization idempotency and duplicate detection.
     */
    Optional<RaceResult> findBySessionIdAndDriverId(
            Long sessionId,
            Long driverId
    );

    /**
     * Checks whether a result already exists for a driver
     * in a specific session.
     */
    boolean existsBySessionIdAndDriverId(
            Long sessionId,
            Long driverId
    );

    /**
     * Finds all race results belonging to a specific season.
     *
     * Related Session, Race, Driver and Constructor entities
     * are fetched together to avoid N+1 queries during
     * standings calculation.
     */
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
    List<RaceResult> findAllBySeasonId(
            @Param("seasonId") Long seasonId
    );
}