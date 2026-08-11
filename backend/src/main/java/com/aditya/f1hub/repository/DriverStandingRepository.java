package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.DriverStanding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverStandingRepository extends JpaRepository<DriverStanding, Long> {

    /**
     * Finds all driver championship standings for a specific season.
     *
     * Results are ordered by championship position.
     */
    List<DriverStanding> findBySeasonIdOrderByPositionAsc(Long seasonId);

    /**
     * Finds the standing of a specific driver in a specific season.
     */
    Optional<DriverStanding> findBySeasonIdAndDriverId(
            Long seasonId,
            Long driverId
    );

    /**
     * Checks whether a standing already exists for a driver
     * in a specific season.
     */
    boolean existsBySeasonIdAndDriverId(
            Long seasonId,
            Long driverId
    );
}