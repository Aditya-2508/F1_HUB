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
}