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
}