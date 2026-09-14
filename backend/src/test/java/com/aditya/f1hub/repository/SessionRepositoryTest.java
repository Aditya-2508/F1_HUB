package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Session;
import com.aditya.f1hub.specification.SessionSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SessionRepositoryTest {

    @Autowired
    private SessionRepository sessionRepository;

    @Test
    @DisplayName("Should find session by external session ID")
    void shouldFindSessionByExternalSessionId() {

        var sessions = sessionRepository.findAll();

        assertThat(sessions)
                .isNotEmpty();

        var expectedSession = sessions.get(0);

        var result =
                sessionRepository.findByExternalSessionId(
                        expectedSession.getExternalSessionId());

        assertThat(result)
                .isPresent();

        assertThat(result.get().getId())
                .isEqualTo(expectedSession.getId());
    }

    @Test
    @DisplayName("Should return true when external session ID exists")
    void shouldReturnTrueWhenExternalSessionIdExists() {

        var sessions = sessionRepository.findAll();

        assertThat(sessions)
                .isNotEmpty();

        var externalSessionId =
                sessions.get(0).getExternalSessionId();

        assertThat(
                sessionRepository.existsByExternalSessionId(
                        externalSessionId))
                .isTrue();
    }

    @Test
    @DisplayName("Should filter sessions by session name")
    void shouldFilterSessionsBySessionName() {

        var specification =
                SessionSpecification.hasSessionName("practice");

        var result =
                sessionRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .allMatch(session ->
                        session.getSessionName()
                                .toLowerCase()
                                .contains("practice"));
    }

    @Test
    @DisplayName("Should filter sessions by session type")
    void shouldFilterSessionsBySessionType() {

        var specification =
                SessionSpecification.hasSessionType("practice");

        var result =
                sessionRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .allMatch(session ->
                        session.getSessionType()
                                .equalsIgnoreCase("practice"));
    }

    @Test
    @DisplayName("Should filter sessions by race ID")
    void shouldFilterSessionsByRaceId() {

        var specification =
                SessionSpecification.hasRaceId(5L);

        var result =
                sessionRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .allMatch(session ->
                        session.getRace() != null
                                && session.getRace()
                                .getId()
                                .equals(5L));
    }

    @Test
    @DisplayName("Should filter sessions by active status")
    void shouldFilterSessionsByActiveStatus() {

        var specification =
                SessionSpecification.hasActive(true);

        var result =
                sessionRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .allMatch(session ->
                        Boolean.TRUE.equals(
                                session.getActive()));
    }

    @Test
    @DisplayName("Should filter sessions by cancelled status")
    void shouldFilterSessionsByCancelledStatus() {

        var specification =
                SessionSpecification.hasCancelled(true);

        var result =
                sessionRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .allMatch(session ->
                        Boolean.TRUE.equals(
                                session.getCancelled()));
    }

    @Test
    @DisplayName("Should filter sessions using multiple criteria")
    void shouldFilterSessionsUsingMultipleCriteria() {

        var specification =
                Specification.allOf(
                        SessionSpecification.hasSessionName("practice"),
                        SessionSpecification.hasSessionType("practice"),
                        SessionSpecification.hasRaceId(5L),
                        SessionSpecification.hasActive(true),
                        SessionSpecification.hasCancelled(false));

        var result =
                sessionRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .allMatch(session ->
                        session.getSessionName()
                                .toLowerCase()
                                .contains("practice")
                                && session.getSessionType()
                                .equalsIgnoreCase("practice")
                                && session.getRace() != null
                                && session.getRace()
                                .getId()
                                .equals(5L)
                                && Boolean.TRUE.equals(
                                session.getActive())
                                && Boolean.FALSE.equals(
                                session.getCancelled()));
    }

    @Test
    @DisplayName("Should return all sessions when no filters are provided")
    void shouldReturnAllSessionsWhenNoFiltersAreProvided() {

        var specification =
                Specification.allOf(
                        SessionSpecification.hasSessionName(null),
                        SessionSpecification.hasSessionType(null),
                        SessionSpecification.hasRaceId(null),
                        SessionSpecification.hasActive(null),
                        SessionSpecification.hasCancelled(null));

        var result =
                sessionRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .hasSize(sessionRepository.findAll().size());
    }
}