package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RaceRepository extends
        JpaRepository<Race, Long>,
        JpaSpecificationExecutor<Race> {

    /**
     * Find Race by External Meeting ID
     */
    Optional<Race> findByExternalMeetingId(String externalMeetingId);

    /**
     * Check if External Meeting ID already exists
     */
    boolean existsByExternalMeetingId(String externalMeetingId);

    /**
     * Searches races by multiple user-facing fields.
     *
     * The search is case-insensitive and supports partial matching.
     */
    @Query("""
            SELECT r
            FROM Race r
            WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(r.officialName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(r.location) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(r.countryName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(r.countryCode) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    Page<Race> search(
            @Param("query") String query,
            Pageable pageable);

    /**
     * Finds championship races for a season.
     *
     * <p>
     * Non-championship meetings such as pre-season testing have
     * a null round number and are therefore excluded.
     * </p>
     */
    List<Race> findBySeasonIdAndRoundNumberIsNotNullOrderByRoundNumberAsc(
            Long seasonId
    );
}