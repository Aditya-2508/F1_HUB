package com.aditya.f1hub.controller.analytics;

import com.aditya.f1hub.dto.analytics.SeasonStatisticsResponseDto;
import com.aditya.f1hub.service.analytics.SeasonStatisticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeasonStatisticsControllerTest {

    @Mock
    private SeasonStatisticsService seasonStatisticsService;

    @InjectMocks
    private SeasonStatisticsController seasonStatisticsController;

    @Test
    void shouldReturnSeasonStatistics() {

        Long seasonId = 1L;

        SeasonStatisticsResponseDto response =
                SeasonStatisticsResponseDto.builder()
                        .seasonId(seasonId)
                        .seasonYear(2026)
                        .totalRaces(25)
                        .completedRaces(23)
                        .cancelledRaces(2)
                        .totalDrivers(22)
                        .totalConstructors(11)
                        .totalPoints(101.0)
                        .build();

        when(seasonStatisticsService.getSeasonStatistics(seasonId))
                .thenReturn(response);

        ResponseEntity<SeasonStatisticsResponseDto> result =
                seasonStatisticsController.getSeasonStatistics(seasonId);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getSeasonId()).isEqualTo(seasonId);
        assertThat(result.getBody().getSeasonYear()).isEqualTo(2026);
    }
}