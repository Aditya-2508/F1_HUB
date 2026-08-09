package com.aditya.f1hub.specification;

import com.aditya.f1hub.entity.Session;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class SessionSpecification {

    private SessionSpecification() {
        // Utility class
    }

    /**
     * Filter Sessions by Session name.
     */
    public static Specification<Session> hasSessionName(
            String sessionName) {

        return (root, query, criteriaBuilder) -> {

            if (sessionName == null || sessionName.isBlank()) {
                return null;
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(
                            root.get("sessionName")
                    ),
                    "%" + sessionName.toLowerCase() + "%"
            );
        };
    }

    /**
     * Filter Sessions by Session type.
     */
    public static Specification<Session> hasSessionType(
            String sessionType) {

        return (root, query, criteriaBuilder) -> {

            if (sessionType == null || sessionType.isBlank()) {
                return null;
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(
                            root.get("sessionType")
                    ),
                    sessionType.toLowerCase()
            );
        };
    }

    /**
     * Filter Sessions by Race ID.
     */
    public static Specification<Session> hasRaceId(
            Long raceId) {

        return (root, query, criteriaBuilder) -> {

            if (raceId == null) {
                return null;
            }

            Join<Session, ?> raceJoin =
                    root.join("race", JoinType.INNER);

            return criteriaBuilder.equal(
                    raceJoin.get("id"),
                    raceId
            );
        };
    }

    /**
     * Filter Sessions by active status.
     */
    public static Specification<Session> hasActive(
            Boolean active) {

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
     * Filter Sessions by cancellation status.
     */
    public static Specification<Session> hasCancelled(
            Boolean cancelled) {

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