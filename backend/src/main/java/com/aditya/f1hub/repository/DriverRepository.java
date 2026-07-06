package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    /**
     * Find a driver using the external API identifier.
     *
     * @param externalDriverId External API Driver ID
     * @return Optional containing the Driver if found
     */
    Optional<Driver> findByExternalDriverId(String externalDriverId);

    /**
     * Find a driver using the official three-letter abbreviation.
     *
     * Example:
     * VER
     * HAM
     * LEC
     */
    Optional<Driver> findByAbbreviation(String abbreviation);

    /**
     * Check whether a driver already exists using the external API ID.
     *
     * Useful while synchronizing data from external Formula 1 APIs.
     */
    boolean existsByExternalDriverId(String externalDriverId);

}