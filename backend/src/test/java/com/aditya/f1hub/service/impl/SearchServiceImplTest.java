package com.aditya.f1hub.service.impl;

import com.aditya.f1hub.dto.circuit.CircuitResponseDto;
import com.aditya.f1hub.dto.constructor.ConstructorResponseDto;
import com.aditya.f1hub.dto.driver.DriverResponseDto;
import com.aditya.f1hub.dto.race.RaceResponseDto;
import com.aditya.f1hub.dto.search.SearchResponseDto;
import com.aditya.f1hub.entity.Circuit;
import com.aditya.f1hub.entity.Constructor;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.Season;
import com.aditya.f1hub.mapper.CircuitMapper;
import com.aditya.f1hub.mapper.ConstructorMapper;
import com.aditya.f1hub.mapper.DriverMapper;
import com.aditya.f1hub.mapper.RaceMapper;
import com.aditya.f1hub.repository.CircuitRepository;
import com.aditya.f1hub.repository.ConstructorRepository;
import com.aditya.f1hub.repository.DriverRepository;
import com.aditya.f1hub.repository.RaceRepository;
import com.aditya.f1hub.repository.SeasonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private ConstructorRepository constructorRepository;

    @Mock
    private CircuitRepository circuitRepository;

    @Mock
    private RaceRepository raceRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @Mock
    private DriverMapper driverMapper;

    @Mock
    private ConstructorMapper constructorMapper;

    @Mock
    private CircuitMapper circuitMapper;

    @Mock
    private RaceMapper raceMapper;

    private SearchServiceImpl searchService;

    @BeforeEach
    void setUp() {
        searchService = new SearchServiceImpl(
                driverRepository,
                constructorRepository,
                circuitRepository,
                raceRepository,
                seasonRepository,
                driverMapper,
                constructorMapper,
                circuitMapper,
                raceMapper
        );
    }

    @Test
    @DisplayName("Should return grouped results from all searchable domains")
    void shouldReturnGroupedSearchResults() {

        Driver driver = mock(Driver.class);
        Constructor constructor = mock(Constructor.class);
        Circuit circuit = mock(Circuit.class);
        Race race = mock(Race.class);

        DriverResponseDto driverDto = mock(DriverResponseDto.class);
        ConstructorResponseDto constructorDto = mock(ConstructorResponseDto.class);
        CircuitResponseDto circuitDto = mock(CircuitResponseDto.class);
        RaceResponseDto raceDto = mock(RaceResponseDto.class);

        when(driverRepository.search(eq("hamilton"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(driver)));

        when(constructorRepository.search(eq("hamilton"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(constructor)));

        when(circuitRepository.search(eq("hamilton"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(circuit)));

        when(raceRepository.search(eq("hamilton"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(race)));

        when(driverMapper.toResponseDto(driver))
                .thenReturn(driverDto);

        when(constructorMapper.toResponseDto(constructor))
                .thenReturn(constructorDto);

        when(circuitMapper.toResponseDto(circuit))
                .thenReturn(circuitDto);

        when(raceMapper.toResponseDto(race))
                .thenReturn(raceDto);

        SearchResponseDto response =
                searchService.search("  hamilton  ");

        assertThat(response)
                .isNotNull();

        assertThat(response.getDrivers())
                .containsExactly(driverDto);

        assertThat(response.getConstructors())
                .containsExactly(constructorDto);

        assertThat(response.getCircuits())
                .containsExactly(circuitDto);

        assertThat(response.getRaces())
                .containsExactly(raceDto);

        assertThat(response.getSeasons())
                .isEmpty();

        verify(seasonRepository, never())
                .findByYear(anyInt());
    }

    @Test
    @DisplayName("Should find season when search query is a valid championship year")
    void shouldFindSeasonForNumericQuery() {

        Season season = mock(Season.class);

        when(seasonRepository.findByYear(2026))
                .thenReturn(Optional.of(season));

        when(season.getYear())
                .thenReturn(2026);

        when(driverRepository.search(eq("2026"), any(Pageable.class)))
                .thenReturn(Page.empty());

        when(constructorRepository.search(eq("2026"), any(Pageable.class)))
                .thenReturn(Page.empty());

        when(circuitRepository.search(eq("2026"), any(Pageable.class)))
                .thenReturn(Page.empty());

        when(raceRepository.search(eq("2026"), any(Pageable.class)))
                .thenReturn(Page.empty());

        SearchResponseDto response =
                searchService.search("2026");

        assertThat(response.getSeasons())
                .containsExactly(2026);

        verify(seasonRepository)
                .findByYear(2026);
    }

    @Test
    @DisplayName("Should return empty season results when numeric year does not exist")
    void shouldReturnEmptySeasonResultsForUnknownYear() {

        when(seasonRepository.findByYear(9999))
                .thenReturn(Optional.empty());

        when(driverRepository.search(eq("9999"), any(Pageable.class)))
                .thenReturn(Page.empty());

        when(constructorRepository.search(eq("9999"), any(Pageable.class)))
                .thenReturn(Page.empty());

        when(circuitRepository.search(eq("9999"), any(Pageable.class)))
                .thenReturn(Page.empty());

        when(raceRepository.search(eq("9999"), any(Pageable.class)))
                .thenReturn(Page.empty());

        SearchResponseDto response =
                searchService.search("9999");

        assertThat(response.getSeasons())
                .isEmpty();

        verify(seasonRepository)
                .findByYear(9999);
    }

    @Test
    @DisplayName("Should return empty results when no domain matches query")
    void shouldReturnEmptyResultsWhenNothingMatches() {

        String query = "xyz-no-match";

        when(driverRepository.search(eq(query), any(Pageable.class)))
                .thenReturn(Page.empty());

        when(constructorRepository.search(eq(query), any(Pageable.class)))
                .thenReturn(Page.empty());

        when(circuitRepository.search(eq(query), any(Pageable.class)))
                .thenReturn(Page.empty());

        when(raceRepository.search(eq(query), any(Pageable.class)))
                .thenReturn(Page.empty());

        SearchResponseDto response =
                searchService.search(query);

        assertThat(response)
                .isNotNull();

        assertThat(response.getDrivers())
                .isEmpty();

        assertThat(response.getConstructors())
                .isEmpty();

        assertThat(response.getCircuits())
                .isEmpty();

        assertThat(response.getRaces())
                .isEmpty();

        assertThat(response.getSeasons())
                .isEmpty();

        verify(seasonRepository, never())
                .findByYear(anyInt());
    }

    @Test
    @DisplayName("Should use a maximum of ten results per entity")
    void shouldUseMaximumTenResultsPerEntity() {

        when(driverRepository.search(eq("a"), any(Pageable.class)))
                .thenReturn(Page.empty());

        when(constructorRepository.search(eq("a"), any(Pageable.class)))
                .thenReturn(Page.empty());

        when(circuitRepository.search(eq("a"), any(Pageable.class)))
                .thenReturn(Page.empty());

        when(raceRepository.search(eq("a"), any(Pageable.class)))
                .thenReturn(Page.empty());

        searchService.search("a");

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(driverRepository)
                .search(eq("a"), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();

        assertThat(pageable)
                .isEqualTo(PageRequest.of(0, 10));

        verify(constructorRepository)
                .search(eq("a"), eq(pageable));

        verify(circuitRepository)
                .search(eq("a"), eq(pageable));

        verify(raceRepository)
                .search(eq("a"), eq(pageable));
    }
}