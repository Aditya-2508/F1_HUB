package com.aditya.f1hub.specification;

import com.aditya.f1hub.entity.Race;
import org.springframework.data.jpa.domain.Specification;

public final class RaceSpecification {

    private RaceSpecification() {
        // Utility class - prevent instantiation
    }

    /**
     * Filters races by name using a case-insensitive partial match.
     */
    public static Specification<Race> hasName(String name) {

        return (root, query, criteriaBuilder) -> {

            if (name == null || name.trim().isEmpty()) {
                return null;
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.trim().toLowerCase() + "%"
            );
        };
    }

    /**
     * Filters races by Season ID.
     */
    public static Specification<Race> hasSeasonId(Long seasonId) {

        return (root, query, criteriaBuilder) -> {

            if (seasonId == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("season").get("id"),
                    seasonId
            );
        };
    }

    /**
     * Filters races by Circuit ID.
     */
    public static Specification<Race> hasCircuitId(Long circuitId) {

        return (root, query, criteriaBuilder) -> {

            if (circuitId == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("circuit").get("id"),
                    circuitId
            );
        };
    }

    /**
     * Filters races by country name using a case-insensitive partial match.
     */
    public static Specification<Race> hasCountryName(String countryName) {

        return (root, query, criteriaBuilder) -> {

            if (countryName == null || countryName.trim().isEmpty()) {
                return null;
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("countryName")),
                    "%" + countryName.trim().toLowerCase() + "%"
            );
        };
    }

    /**
     * Filters races by active status.
     */
    public static Specification<Race> hasActive(Boolean active) {

        return (root, query, criteriaBuilder) -> {

            if (active == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("active"),
                    active
            );
        };
    }

    /**
     * Filters races by cancelled status.
     */
    public static Specification<Race> hasCancelled(Boolean cancelled) {

        return (root, query, criteriaBuilder) -> {

            if (cancelled == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("cancelled"),
                    cancelled
            );
        };
    }
}