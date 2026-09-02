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
}