package com.aditya.f1hub.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ConstructorStandingRepositoryTest {

    @Autowired
    private ConstructorStandingRepository constructorStandingRepository;

    @Test
    void shouldFindConstructorStandingStatistics() {

        ConstructorStandingStatisticsProjection statistics =
                constructorStandingRepository
                        .findConstructorStandingStatistics(1L);

        assertThat(statistics).isNotNull();

        assertThat(statistics.getChampionships())
                .isNotNull()
                .isGreaterThanOrEqualTo(0);

        assertThat(statistics.getWins())
                .isNotNull()
                .isGreaterThanOrEqualTo(0);

        assertThat(statistics.getPoints())
                .isNotNull()
                .isGreaterThanOrEqualTo(0.0);
    }

    @Test
    void shouldReturnZeroStatisticsWhenConstructorHasNoStandings() {

        ConstructorStandingStatisticsProjection statistics =
                constructorStandingRepository
                        .findConstructorStandingStatistics(999999L);

        assertThat(statistics).isNotNull();

        assertThat(statistics.getChampionships())
                .isZero();

        assertThat(statistics.getWins())
                .isZero();

        assertThat(statistics.getPoints())
                .isZero();
    }
}