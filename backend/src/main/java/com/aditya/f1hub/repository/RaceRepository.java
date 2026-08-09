package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Race;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

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
}