package com.aditya.f1hub.controller.analytics;

import com.aditya.f1hub.dto.analytics.RaceAnalyticsResponseDto;
import com.aditya.f1hub.service.analytics.RaceAnalyticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RaceAnalyticsControllerTest {

    @Mock
    private RaceAnalyticsService raceAnalyticsService;

    @InjectMocks
    private RaceAnalyticsController raceAnalyticsController;

    @Test
    void shouldReturnRaceAnalyticsSuccessfully() {
        // Arrange
        Long raceId = 11L;

        RaceAnalyticsResponseDto responseDto = RaceAnalyticsResponseDto.builder()
                .raceId(raceId)
                .raceName("Australian Grand Prix")
                .seasonId(1L)
                .seasonYear(2026)
                .roundNumber(1)
                .build();

        when(raceAnalyticsService.getRaceAnalytics(raceId))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<RaceAnalyticsResponseDto> response =
                raceAnalyticsController.getRaceAnalytics(raceId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(responseDto);
    }

    @Test
    void shouldPassRaceIdToService() {
        // Arrange
        Long raceId = 11L;

        RaceAnalyticsResponseDto responseDto = RaceAnalyticsResponseDto.builder()
                .raceId(raceId)
                .raceName("Australian Grand Prix")
                .build();

        when(raceAnalyticsService.getRaceAnalytics(raceId))
                .thenReturn(responseDto);

        // Act
        raceAnalyticsController.getRaceAnalytics(raceId);

        // Assert
        verify(raceAnalyticsService).getRaceAnalytics(raceId);
    }

    @Test
    void shouldReturnResponseBodyFromService() {
        // Arrange
        Long raceId = 11L;

        RaceAnalyticsResponseDto responseDto = RaceAnalyticsResponseDto.builder()
                .raceId(raceId)
                .raceName("Australian Grand Prix")
                .seasonId(1L)
                .seasonYear(2026)
                .roundNumber(1)
                .build();

        when(raceAnalyticsService.getRaceAnalytics(raceId))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<RaceAnalyticsResponseDto> response =
                raceAnalyticsController.getRaceAnalytics(raceId);

        // Assert
        assertThat(response.getBody().getRaceId()).isEqualTo(raceId);
        assertThat(response.getBody().getRaceName())
                .isEqualTo("Australian Grand Prix");
        assertThat(response.getBody().getSeasonYear())
                .isEqualTo(2026);
        assertThat(response.getBody().getRoundNumber())
                .isEqualTo(1);
    }
}