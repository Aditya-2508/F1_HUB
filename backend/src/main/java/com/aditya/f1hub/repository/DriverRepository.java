package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long>,
        JpaSpecificationExecutor<Driver> {

    Optional<Driver> findByExternalDriverId(String externalDriverId);

    Optional<Driver> findByAbbreviation(String abbreviation);

    Optional<Driver> findByDriverNumber(Integer driverNumber);

    boolean existsByExternalDriverId(String externalDriverId);

    /**
     * Searches drivers by multiple user-facing fields.
     *
     * The search is case-insensitive and supports partial matching.
     */
    @Query("""
            SELECT d
            FROM Driver d
            WHERE LOWER(d.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(d.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(d.abbreviation) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(d.nationality) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    Page<Driver> search(@Param("query") String query, Pageable pageable);
}