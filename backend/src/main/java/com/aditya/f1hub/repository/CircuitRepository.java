package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Circuit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CircuitRepository extends
        JpaRepository<Circuit, Long>,
        JpaSpecificationExecutor<Circuit> {

    /**
     * Find Circuit by External Circuit ID
     */
    Optional<Circuit> findByExternalCircuitId(String externalCircuitId);

    /**
     * Check if External Circuit ID already exists
     */
    boolean existsByExternalCircuitId(String externalCircuitId);

    /**
     * Searches circuits by multiple user-facing fields.
     *
     * The search is case-insensitive and supports partial matching.
     */
    @Query("""
            SELECT c
            FROM Circuit c
            WHERE LOWER(c.circuitName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(c.location) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(c.country) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(c.countryCode) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    Page<Circuit> search(
            @Param("query") String query,
            Pageable pageable);
}