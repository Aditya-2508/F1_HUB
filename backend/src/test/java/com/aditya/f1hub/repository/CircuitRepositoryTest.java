package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Circuit;
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
class CircuitRepositoryTest {

    @Autowired
    private CircuitRepository circuitRepository;

    @Test
    @DisplayName("Should find circuits using partial circuit name")
    void shouldFindCircuitsByPartialName() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Circuit> result =
                circuitRepository.search("mon", pageable);

        assertThat(result.getContent())
                .isNotEmpty();

        assertThat(result.getContent())
                .anyMatch(circuit ->
                        circuit.getCircuitName()
                                .toLowerCase()
                                .contains("mon"));
    }

    @Test
    @DisplayName("Should perform case-insensitive circuit search")
    void shouldSearchCircuitsCaseInsensitively() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Circuit> result =
                circuitRepository.search("MONZA", pageable);

        assertThat(result.getContent())
                .isNotEmpty();

        assertThat(result.getContent())
                .anyMatch(circuit ->
                        circuit.getCircuitName()
                                .equalsIgnoreCase("Monza"));
    }

    @Test
    @DisplayName("Should search circuits by country")
    void shouldSearchCircuitsByCountry() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Circuit> result =
                circuitRepository.search("Italy", pageable);

        assertThat(result.getContent())
                .isNotEmpty();

        assertThat(result.getContent())
                .anyMatch(circuit ->
                        circuit.getCountry()
                                .equalsIgnoreCase("Italy"));
    }

    @Test
    @DisplayName("Should return empty page when no circuit matches")
    void shouldReturnEmptyResultWhenNoCircuitMatches() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Circuit> result =
                circuitRepository.search(
                        "xyz-no-circuit-match",
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

        Page<Circuit> result =
                circuitRepository.search("a", pageable);

        assertThat(result.getSize())
                .isEqualTo(2);

        assertThat(result.getContent().size())
                .isLessThanOrEqualTo(2);
    }
}