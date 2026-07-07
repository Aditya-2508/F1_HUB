package com.aditya.f1hub.specification;

import com.aditya.f1hub.entity.Driver;
import org.springframework.data.jpa.domain.Specification;

public final class DriverSpecification {

    private DriverSpecification() {
    }

    public static Specification<Driver> hasName(String name) {

        return (root, query, criteriaBuilder) -> {

            if (name == null || name.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("fullName")),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Driver> hasNationality(String nationality) {

        return (root, query, criteriaBuilder) -> {

            if (nationality == null || nationality.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("nationality")),
                    nationality.toLowerCase()
            );
        };
    }

    public static Specification<Driver> isActive(Boolean active) {

        return (root, query, criteriaBuilder) -> {

            if (active == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("active"),
                    active
            );
        };
    }
}