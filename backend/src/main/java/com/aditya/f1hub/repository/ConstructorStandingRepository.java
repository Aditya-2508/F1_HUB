package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.ConstructorStanding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConstructorStandingRepository extends JpaRepository<ConstructorStanding, Long> {

    /**
     * Finds all constructor championship standings for a specific season.
     *
     * Results are ordered by championship position.
     */
    List<ConstructorStanding> findBySeasonIdOrderByPositionAsc(Long seasonId);

    /**
     * Finds the standing of a specific constructor in a specific season.
     */
    Optional<ConstructorStanding> findBySeasonIdAndConstructorId(
            Long seasonId,
            Long constructorId
    );

    /**
     * Checks whether a standing already exists for a constructor
     * in a specific season.
     */
    boolean existsBySeasonIdAndConstructorId(
            Long seasonId,
            Long constructorId
    );

    void deleteBySeasonId(Long seasonId);

    /**
     * Retrieves aggregate championship statistics for a constructor.
     */
    @Query("""
            SELECT
                COUNT(CASE
                    WHEN standing.position = 1
                    THEN 1
                END) AS championships,

                COALESCE(SUM(standing.wins), 0) AS wins,

                COALESCE(SUM(standing.points), 0.0) AS points

            FROM ConstructorStanding standing
            WHERE standing.constructor.id = :constructorId
            """)
    ConstructorStandingStatisticsProjection findConstructorStandingStatistics(
            @Param("constructorId") Long constructorId
    );
}