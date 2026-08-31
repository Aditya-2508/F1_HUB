package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Season;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SeasonRepositoryTest {

    @Autowired
    private SeasonRepository seasonRepository;

    @Test
    @DisplayName("Should find season by championship year")
    void shouldFindSeasonByYear() {

        Optional<Season> result =
                seasonRepository.findByYear(2026);

        assertThat(result)
                .isPresent();

        assertThat(result.get().getYear())
                .isEqualTo(2026);
    }

    @Test
    @DisplayName("Should return empty result when season year does not exist")
    void shouldReturnEmptyResultWhenSeasonYearDoesNotExist() {

        Optional<Season> result =
                seasonRepository.findByYear(9999);

        assertThat(result)
                .isEmpty();
    }

    @Test
    @DisplayName("Should return season with correct database identifier")
    void shouldReturnCorrectSeason() {

        Optional<Season> result =
                seasonRepository.findByYear(2026);

        assertThat(result)
                .isPresent();

        assertThat(result.get().getId())
                .isEqualTo(1L);
    }
}