package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Race;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.aditya.f1hub.specification.RaceSpecification;
import org.springframework.data.jpa.domain.Specification;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RaceRepositoryTest {

    @Autowired
    private RaceRepository raceRepository;

    @Test
    @DisplayName("Should find races using partial race name")
    void shouldFindRacesByPartialName() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Race> result =
                raceRepository.search("grand", pageable);

        assertThat(result.getContent())
                .isNotEmpty();

        assertThat(result.getContent())
                .anyMatch(race ->
                        race.getName()
                                .toLowerCase()
                                .contains("grand"));
    }

    @Test
    @DisplayName("Should perform case-insensitive race search")
    void shouldSearchRacesCaseInsensitively() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Race> result =
                raceRepository.search("MONACO GRAND PRIX", pageable);

        assertThat(result.getContent())
                .isNotEmpty();

        assertThat(result.getContent())
                .anyMatch(race ->
                        race.getName()
                                .equalsIgnoreCase("Monaco Grand Prix"));
    }

    @Test
    @DisplayName("Should search races by country")
    void shouldSearchRacesByCountry() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Race> result =
                raceRepository.search("Japan", pageable);

        assertThat(result.getContent())
                .isNotEmpty();

        assertThat(result.getContent())
                .anyMatch(race ->
                        race.getCountryName()
                                .equalsIgnoreCase("Japan"));
    }

    @Test
    @DisplayName("Should return empty page when no race matches")
    void shouldReturnEmptyResultWhenNoRaceMatches() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Race> result =
                raceRepository.search(
                        "xyz-no-race-match",
                        pageable);

        assertThat(result)
                .isNotNull();

        assertThat(result.getContent())
                .isEmpty();
    }

    @Test
    @DisplayName("Should respect pageable result size")
    void shouldRespectPageableResultSize() {

        Pageable pageable = PageRequest.of(0, 2);

        Page<Race> result =
                raceRepository.search("grand", pageable);

        assertThat(result.getSize())
                .isEqualTo(2);

        assertThat(result.getContent().size())
                .isLessThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should return all races when no filters are provided")
    void shouldReturnAllRacesWhenNoFiltersAreProvided() {

        var specification =
                Specification.allOf(
                        RaceSpecification.hasName(null),
                        RaceSpecification.hasSeasonId(null),
                        RaceSpecification.hasCircuitId(null),
                        RaceSpecification.hasCountryName(null),
                        RaceSpecification.hasActive(null),
                        RaceSpecification.hasCancelled(null));

        var result =
                raceRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .hasSize(raceRepository.findAll().size());
    }

    @Test
    @DisplayName("Should filter races using multiple criteria")
    void shouldFilterRacesUsingMultipleCriteria() {

        var specification =
                Specification.allOf(
                        RaceSpecification.hasName("bahrain"),
                        RaceSpecification.hasSeasonId(1L),
                        RaceSpecification.hasCircuitId(2L),
                        RaceSpecification.hasCountryName("bahrain"),
                        RaceSpecification.hasActive(true),
                        RaceSpecification.hasCancelled(true));

        var result =
                raceRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .allMatch(race ->
                        race.getName()
                                .toLowerCase()
                                .contains("bahrain")
                                && race.getSeason() != null
                                && race.getSeason().getId().equals(1L)
                                && race.getCircuit() != null
                                && race.getCircuit().getId().equals(2L)
                                && race.getCountryName()
                                .toLowerCase()
                                .contains("bahrain")
                                && Boolean.TRUE.equals(race.getActive())
                                && Boolean.TRUE.equals(race.getCancelled()));
    }

    @Test
    @DisplayName("Should filter races by cancelled status")
    void shouldFilterRacesByCancelledStatus() {

        var specification =
                RaceSpecification.hasCancelled(true);

        var result =
                raceRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .allMatch(race ->
                        Boolean.TRUE.equals(race.getCancelled()));
    }

    @Test
    @DisplayName("Should filter races by active status")
    void shouldFilterRacesByActiveStatus() {

        var specification =
                RaceSpecification.hasActive(true);

        var result =
                raceRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .allMatch(race ->
                        Boolean.TRUE.equals(race.getActive()));
    }

    @Test
    @DisplayName("Should filter races by country name")
    void shouldFilterRacesByCountryName() {

        var specification =
                RaceSpecification.hasCountryName("bahrain");

        var result =
                raceRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .allMatch(race ->
                        race.getCountryName()
                                .toLowerCase()
                                .contains("bahrain"));
    }

    @Test
    @DisplayName("Should filter races by circuit ID")
    void shouldFilterRacesByCircuitId() {

        var specification =
                RaceSpecification.hasCircuitId(2L);

        var result =
                raceRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .allMatch(race ->
                        race.getCircuit() != null
                                && race.getCircuit().getId().equals(2L));
    }

    @Test
    @DisplayName("Should filter races by season ID")
    void shouldFilterRacesBySeasonId() {

        var specification =
                RaceSpecification.hasSeasonId(1L);

        var result =
                raceRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .allMatch(race ->
                        race.getSeason() != null
                                && race.getSeason().getId().equals(1L));
    }

    @Test
    @DisplayName("Should filter races by name")
    void shouldFilterRacesByName() {

        var specification =
                RaceSpecification.hasName("bahrain");

        var result =
                raceRepository.findAll(specification);

        assertThat(result)
                .isNotEmpty();

        assertThat(result)
                .allMatch(race ->
                        race.getName()
                                .toLowerCase()
                                .contains("bahrain"));
    }
}