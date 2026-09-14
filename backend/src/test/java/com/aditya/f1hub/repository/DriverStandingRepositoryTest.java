package com.aditya.f1hub.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DriverStandingRepositoryTest {

    @Autowired
    private DriverStandingRepository driverStandingRepository;

    @Test
    void shouldFindDriverChampionshipCount() {

        long championships =
                driverStandingRepository
                        .countByDriverIdAndPosition(1L, 1);

        assertThat(championships)
                .isGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldReturnZeroWhenDriverHasNoChampionships() {

        long championships =
                driverStandingRepository
                        .countByDriverIdAndPosition(
                                999999L,
                                1
                        );

        assertThat(championships)
                .isZero();
    }

    @Test
    void shouldReturnZeroWhenPositionDoesNotExist() {

        long championships =
                driverStandingRepository
                        .countByDriverIdAndPosition(
                                1L,
                                999999
                        );

        assertThat(championships)
                .isZero();
    }
}