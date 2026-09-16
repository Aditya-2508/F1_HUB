package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.RaceResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RaceResultRepositoryTest {

    @Autowired
    private RaceResultRepository raceResultRepository;

    @Test
    void shouldFindRaceResultsBySessionId() {

        List<RaceResult> results =
                raceResultRepository.findBySessionId(1L);

        assertThat(results).isNotNull();
    }

    @Test
    void shouldFindRaceResultsByDriverId() {

        List<RaceResult> results =
                raceResultRepository.findByDriverId(1L);

        assertThat(results).isNotNull();
    }

    @Test
    void shouldFindRaceResultsByConstructorId() {

        List<RaceResult> results =
                raceResultRepository.findByConstructorId(1L);

        assertThat(results).isNotNull();
    }

    @Test
    void shouldCheckRaceResultExistsBySessionAndDriver() {

        boolean exists =
                raceResultRepository.existsBySessionIdAndDriverId(
                        1L,
                        1L
                );

        assertThat(exists).isIn(true, false);
    }

    @Test
    void shouldFindRaceResultBySessionAndDriver() {

        Optional<RaceResult> result =
                raceResultRepository.findBySessionIdAndDriverId(
                        1L,
                        1L
                );

        assertThat(result).isNotNull();
    }

    @Test
    void shouldReturnEmptyWhenRaceResultDoesNotExist() {

        Optional<RaceResult> result =
                raceResultRepository.findBySessionIdAndDriverId(
                        999999L,
                        999999L
                );

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindDriverCareerStatistics() {

        DriverCareerStatisticsProjection statistics =
                raceResultRepository.findDriverCareerStatistics(1L);

        assertThat(statistics).isNotNull();

        assertThat(statistics.getRaceWins())
                .isNotNull()
                .isGreaterThanOrEqualTo(0);

        assertThat(statistics.getRacePodiums())
                .isNotNull()
                .isGreaterThanOrEqualTo(0);

        assertThat(statistics.getQualifyingPoles())
                .isNotNull()
                .isGreaterThanOrEqualTo(0);

        assertThat(statistics.getChampionshipPoints())
                .isNotNull()
                .isGreaterThanOrEqualTo(0.0);
    }

    @Test
    void shouldCountDriverFastestLaps() {

        long fastestLaps =
                raceResultRepository.countDriverFastestLaps(1L);

        assertThat(fastestLaps)
                .isGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldCountConstructorPodiums() {

        long podiums =
                raceResultRepository.countConstructorPodiums(1L);

        assertThat(podiums)
                .isGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldReturnZeroConstructorPodiumsWhenConstructorHasNoResults() {

        long podiums =
                raceResultRepository.countConstructorPodiums(999999L);

        assertThat(podiums)
                .isZero();
    }
}