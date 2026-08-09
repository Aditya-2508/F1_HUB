package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeasonRepository extends JpaRepository<Season, Long> {

    /**
     * Find Season by championship year.
     */
    Optional<Season> findByYear(Integer year);
}