package com.aditya.f1hub.specification;

import com.aditya.f1hub.entity.RaceResult;
import org.springframework.data.jpa.domain.Specification;

public final class RaceResultSpecification {

    private RaceResultSpecification() {
        // Utility class
    }

    public static Specification<RaceResult> hasDriverId(Long driverId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("driver").get("id"), driverId);
    }

    public static Specification<RaceResult> hasConstructorId(Long constructorId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("constructor").get("id"), constructorId);
    }

    public static Specification<RaceResult> hasSessionId(Long sessionId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("session").get("id"), sessionId);
    }

    public static Specification<RaceResult> hasRaceId(Long raceId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("session").get("race").get("id"),
                        raceId
                );
    }

    public static Specification<RaceResult> hasSeasonId(Long seasonId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("session").get("race").get("season").get("id"),
                        seasonId
                );
    }

    public static Specification<RaceResult> hasPosition(Integer position) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("position"), position);
    }

    public static Specification<RaceResult> isActive(Boolean active) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("active"), active);
    }
}