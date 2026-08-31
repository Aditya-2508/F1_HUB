package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Constructor;
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
class ConstructorRepositoryTest {

    @Autowired
    private ConstructorRepository constructorRepository;

    @Test
    @DisplayName("Should find constructors using partial name")
    void shouldFindConstructorsByPartialName() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Constructor> result =
                constructorRepository.search("mer", pageable);

        assertThat(result.getContent())
                .isNotEmpty();

        assertThat(result.getContent())
                .anyMatch(constructor ->
                        constructor.getName()
                                .toLowerCase()
                                .contains("mer"));
    }

    @Test
    @DisplayName("Should perform case-insensitive constructor search")
    void shouldSearchConstructorsCaseInsensitively() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Constructor> result =
                constructorRepository.search("MERCEDES", pageable);

        assertThat(result.getContent())
                .isNotEmpty();

        assertThat(result.getContent())
                .anyMatch(constructor ->
                        constructor.getName()
                                .equalsIgnoreCase("Mercedes"));
    }

    @Test
    @DisplayName("Should search constructors by country code")
    void shouldSearchConstructorsByCountryCode() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Constructor> result =
                constructorRepository.search("GBR", pageable);

        assertThat(result.getContent())
                .isNotEmpty();

        assertThat(result.getContent())
                .anyMatch(constructor ->
                        constructor.getCountryCode() != null &&
                                constructor.getCountryCode()
                                        .equalsIgnoreCase("GBR"));
    }

    @Test
    @DisplayName("Should return empty page when no constructor matches")
    void shouldReturnEmptyResultWhenNoConstructorMatches() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Constructor> result =
                constructorRepository.search(
                        "xyz-no-constructor-match",
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

        Page<Constructor> result =
                constructorRepository.search("a", pageable);

        assertThat(result.getSize())
                .isEqualTo(2);

        assertThat(result.getContent().size())
                .isLessThanOrEqualTo(2);
    }
}