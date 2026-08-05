package com.aditya.f1hub.specification;

import com.aditya.f1hub.entity.Circuit;
import org.springframework.data.jpa.domain.Specification;

public final class CircuitSpecification {

    private CircuitSpecification() {
    }

    /**
     * Search by Circuit Name
     */
    public static Specification<Circuit> hasCircuitName(String circuitName) {

        return (root, query, criteriaBuilder) -> {

            if (circuitName == null || circuitName.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("circuitName")),
                    "%" + circuitName.toLowerCase() + "%");
        };
    }

    /**
     * Search by Country
     */
    public static Specification<Circuit> hasCountry(String country) {

        return (root, query, criteriaBuilder) -> {

            if (country == null || country.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("country")),
                    "%" + country.toLowerCase() + "%");
        };
    }

    /**
     * Search by Active Status
     */
    public static Specification<Circuit> isActive(Boolean active) {

        return (root, query, criteriaBuilder) -> {

            if (active == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("active"),
                    active);
        };
    }

}