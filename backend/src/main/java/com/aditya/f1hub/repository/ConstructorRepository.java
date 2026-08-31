package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Constructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConstructorRepository extends
        JpaRepository<Constructor, Long>,
        JpaSpecificationExecutor<Constructor> {

    Optional<Constructor> findByExternalConstructorId(String externalConstructorId);

    Optional<Constructor> findByName(String name);

    boolean existsByExternalConstructorId(String externalConstructorId);

    /**
     * Searches constructors by multiple user-facing fields.
     *
     * The search is case-insensitive and supports partial matching.
     */
    @Query("""
            SELECT c
            FROM Constructor c
            WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(c.nationality) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(c.countryCode) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    Page<Constructor> search(
            @Param("query") String query,
            Pageable pageable);
}