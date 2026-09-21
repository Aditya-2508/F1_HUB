package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends
        JpaRepository<Session, Long>,
        JpaSpecificationExecutor<Session> {

    /**
     * Find Session by External Session ID.
     */
    Optional<Session> findByExternalSessionId(String externalSessionId);

    /**
     * Check if External Session ID already exists.
     */
    boolean existsByExternalSessionId(String externalSessionId);

    /**
     * Finds all sessions belonging to a specific season.
     *
     * Sessions are ordered chronologically so the master synchronization
     * processes them in a deterministic order.
     */
    List<Session> findByRaceSeasonYearOrderByStartTimeAsc(Integer year);
}