package com.aditya.f1hub.service.analytics.impl;

import com.aditya.f1hub.dto.analytics.DriverHistoricalAnalysisResponseDto;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.entity.DriverStanding;
import com.aditya.f1hub.entity.Season;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.repository.DriverRepository;
import com.aditya.f1hub.repository.DriverStandingRepository;
import com.aditya.f1hub.repository.RaceResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.aditya.f1hub.service.impl.HistoricalAnalysisServiceImpl;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoricalAnalysisServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DriverStandingRepository driverStandingRepository;

    @Mock
    private RaceResultRepository raceResultRepository;

    @InjectMocks
    private HistoricalAnalysisServiceImpl historicalAnalysisService;

    @Test
    void shouldReturnDriverHistoricalAnalysisSuccessfully() {

        Driver driver = Driver.builder()
                .fullName("Max VERSTAPPEN")
                .build();

        setEntityId(driver, 4L);

        Season season2025 = new Season();
        setEntityId(season2025, 1L);
        season2025.setYear(2025);

        Season season2026 = new Season();
        setEntityId(season2026, 2L);
        season2026.setYear(2026);

        DriverStanding standing2025 = DriverStanding.builder()
                .season(season2025)
                .driver(driver)
                .position(1)
                .points(575.0)
                .wins(19)
                .build();

        DriverStanding standing2026 = DriverStanding.builder()
                .season(season2026)
                .driver(driver)
                .position(2)
                .points(250.0)
                .wins(8)
                .build();

        RaceResultRepository.DriverSeasonPodiumProjection podium2025 =
                mock(RaceResultRepository.DriverSeasonPodiumProjection.class);

        when(podium2025.getSeasonId()).thenReturn(1L);
        when(podium2025.getPodiums()).thenReturn(21L);

        RaceResultRepository.DriverSeasonPodiumProjection podium2026 =
                mock(RaceResultRepository.DriverSeasonPodiumProjection.class);

        when(podium2026.getSeasonId()).thenReturn(2L);
        when(podium2026.getPodiums()).thenReturn(15L);

        when(driverRepository.findById(4L))
                .thenReturn(java.util.Optional.of(driver));

        when(driverStandingRepository.findByDriverIdOrderBySeason_YearAsc(4L))
                .thenReturn(List.of(standing2025, standing2026));

        when(raceResultRepository.findDriverPodiumsBySeason(4L))
                .thenReturn(List.of(podium2025, podium2026));

        DriverHistoricalAnalysisResponseDto response =
                historicalAnalysisService.getDriverHistoricalAnalysis(4L);

        assertThat(response).isNotNull();
        assertThat(response.getDriverId()).isEqualTo(4L);
        assertThat(response.getDriverName()).isEqualTo("Max VERSTAPPEN");

        assertThat(response.getSeasons()).hasSize(2);

        assertThat(response.getSeasons().get(0).getSeasonId()).isEqualTo(1L);
        assertThat(response.getSeasons().get(0).getSeasonYear()).isEqualTo(2025);
        assertThat(response.getSeasons().get(0).getChampionshipPosition()).isEqualTo(1);
        assertThat(response.getSeasons().get(0).getPoints()).isEqualTo(575.0);
        assertThat(response.getSeasons().get(0).getWins()).isEqualTo(19);
        assertThat(response.getSeasons().get(0).getPodiums()).isEqualTo(21);

        assertThat(response.getSeasons().get(1).getSeasonId()).isEqualTo(2L);
        assertThat(response.getSeasons().get(1).getSeasonYear()).isEqualTo(2026);
        assertThat(response.getSeasons().get(1).getChampionshipPosition()).isEqualTo(2);
        assertThat(response.getSeasons().get(1).getPoints()).isEqualTo(250.0);
        assertThat(response.getSeasons().get(1).getWins()).isEqualTo(8);
        assertThat(response.getSeasons().get(1).getPodiums()).isEqualTo(15);

        verify(driverRepository).findById(4L);
        verify(driverStandingRepository)
                .findByDriverIdOrderBySeason_YearAsc(4L);
        verify(raceResultRepository)
                .findDriverPodiumsBySeason(4L);
    }

    @Test
    void shouldReturnEmptySeasonsWhenDriverHasNoHistoricalStandings() {

        Driver driver = Driver.builder()
                .fullName("Max VERSTAPPEN")
                .build();

        setEntityId(driver, 4L);

        when(driverRepository.findById(4L))
                .thenReturn(java.util.Optional.of(driver));

        when(driverStandingRepository.findByDriverIdOrderBySeason_YearAsc(4L))
                .thenReturn(List.of());

        when(raceResultRepository.findDriverPodiumsBySeason(4L))
                .thenReturn(List.of());

        DriverHistoricalAnalysisResponseDto response =
                historicalAnalysisService.getDriverHistoricalAnalysis(4L);

        assertThat(response).isNotNull();
        assertThat(response.getDriverId()).isEqualTo(4L);
        assertThat(response.getDriverName()).isEqualTo("Max VERSTAPPEN");
        assertThat(response.getSeasons()).isEmpty();

        verify(driverRepository).findById(4L);
        verify(driverStandingRepository)
                .findByDriverIdOrderBySeason_YearAsc(4L);
        verify(raceResultRepository)
                .findDriverPodiumsBySeason(4L);
    }

    @Test
    void shouldReturnZeroPodiumsWhenSeasonHasNoPodiumData() {

        Driver driver = Driver.builder()
                .fullName("Max VERSTAPPEN")
                .build();

        setEntityId(driver, 4L);

        Season season = new Season();
        setEntityId(season, 1L);
        season.setYear(2026);

        DriverStanding standing = DriverStanding.builder()
                .season(season)
                .driver(driver)
                .position(1)
                .points(250.0)
                .wins(8)
                .build();

        when(driverRepository.findById(4L))
                .thenReturn(java.util.Optional.of(driver));

        when(driverStandingRepository.findByDriverIdOrderBySeason_YearAsc(4L))
                .thenReturn(List.of(standing));

        when(raceResultRepository.findDriverPodiumsBySeason(4L))
                .thenReturn(List.of());

        DriverHistoricalAnalysisResponseDto response =
                historicalAnalysisService.getDriverHistoricalAnalysis(4L);

        assertThat(response.getSeasons()).hasSize(1);
        assertThat(response.getSeasons().get(0).getPodiums()).isZero();
    }

    @Test
    void shouldThrowExceptionWhenDriverDoesNotExist() {

        when(driverRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() ->
                historicalAnalysisService.getDriverHistoricalAnalysis(999L)
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verify(driverRepository).findById(999L);

        verifyNoInteractions(
                driverStandingRepository,
                raceResultRepository
        );
    }

    private void setEntityId(Object entity, Long id) {
        try {
            var field = entity.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }
}