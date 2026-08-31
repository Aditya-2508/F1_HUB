package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Driver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DriverRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;

    @Test
    @DisplayName("Should find drivers using partial case-insensitive name")
    void shouldFindDriversByPartialName() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Driver> result =
                driverRepository.search("ham", pageable);

        assertThat(result.getContent())
                .isNotEmpty();

        assertThat(result.getContent())
                .anyMatch(driver ->
                        driver.getFullName()
                                .toLowerCase()
                                .contains("ham"));
    }

    @Test
    @DisplayName("Should perform case-insensitive driver search")
    void shouldSearchDriversCaseInsensitively() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Driver> result =
                driverRepository.search("HAMILTON", pageable);

        assertThat(result.getContent())
                .isNotEmpty();

        assertThat(result.getContent())
                .anyMatch(driver ->
                        driver.getFullName()
                                .equalsIgnoreCase("Lewis Hamilton"));
    }

    @Test
    @DisplayName("Should search driver abbreviation")
    void shouldSearchDriversByAbbreviation() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Driver> result =
                driverRepository.search("HAM", pageable);

        assertThat(result.getContent())
                .isNotEmpty();

        assertThat(result.getContent())
                .anyMatch(driver ->
                        driver.getAbbreviation()
                                .equalsIgnoreCase("HAM"));
    }

    @Test
    @DisplayName("Should return empty page when no driver matches")
    void shouldReturnEmptyResultWhenNoDriverMatches() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Driver> result =
                driverRepository.search(
                        "xyz-no-driver-match",
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

        Page<Driver> result =
                driverRepository.search("a", pageable);

        assertThat(result.getSize())
                .isEqualTo(2);

        assertThat(result.getContent().size())
                .isLessThanOrEqualTo(2);
    }
}