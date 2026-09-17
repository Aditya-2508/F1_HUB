package com.aditya.f1hub.controller.analytics;

import com.aditya.f1hub.dto.analytics.DriverHistoricalAnalysisResponseDto;
import com.aditya.f1hub.dto.analytics.DriverHistoricalSeasonDto;
import com.aditya.f1hub.service.HistoricalAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoricalAnalysisControllerTest {

    @Mock
    private HistoricalAnalysisService historicalAnalysisService;

    @InjectMocks
    private HistoricalAnalysisController historicalAnalysisController;

    private DriverHistoricalAnalysisResponseDto historicalResponse;

    @BeforeEach
    void setUp() {

        DriverHistoricalSeasonDto firstSeason =
                DriverHistoricalSeasonDto.builder()
                        .seasonId(1L)
                        .seasonYear(2025)
                        .championshipPosition(1)
                        .points(575.0)
                        .wins(19)
                        .podiums(21)
                        .build();

        DriverHistoricalSeasonDto secondSeason =
                DriverHistoricalSeasonDto.builder()
                        .seasonId(2L)
                        .seasonYear(2026)
                        .championshipPosition(2)
                        .points(250.0)
                        .wins(8)
                        .podiums(15)
                        .build();

        historicalResponse =
                DriverHistoricalAnalysisResponseDto.builder()
                        .driverId(4L)
                        .driverName("Max VERSTAPPEN")
                        .seasons(List.of(firstSeason, secondSeason))
                        .build();
    }

    @Test
    void shouldReturnDriverHistoricalAnalysis() {

        Long driverId = 4L;

        when(historicalAnalysisService.getDriverHistoricalAnalysis(
                driverId
        )).thenReturn(historicalResponse);

        ResponseEntity<DriverHistoricalAnalysisResponseDto> response =
                historicalAnalysisController.getDriverHistoricalAnalysis(
                        driverId
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertSame(
                historicalResponse,
                response.getBody()
        );

        assertEquals(
                4L,
                response.getBody().getDriverId()
        );

        assertEquals(
                "Max VERSTAPPEN",
                response.getBody().getDriverName()
        );

        assertEquals(
                2,
                response.getBody().getSeasons().size()
        );

        assertEquals(
                2025,
                response.getBody()
                        .getSeasons()
                        .get(0)
                        .getSeasonYear()
        );

        assertEquals(
                1,
                response.getBody()
                        .getSeasons()
                        .get(0)
                        .getChampionshipPosition()
        );

        assertEquals(
                21,
                response.getBody()
                        .getSeasons()
                        .get(0)
                        .getPodiums()
        );

        verify(historicalAnalysisService)
                .getDriverHistoricalAnalysis(driverId);
    }

    @Test
    void shouldReturnEmptyHistoricalAnalysis() {

        Long driverId = 4L;

        DriverHistoricalAnalysisResponseDto emptyResponse =
                DriverHistoricalAnalysisResponseDto.builder()
                        .driverId(driverId)
                        .driverName("Max VERSTAPPEN")
                        .seasons(List.of())
                        .build();

        when(historicalAnalysisService.getDriverHistoricalAnalysis(
                driverId
        )).thenReturn(emptyResponse);

        ResponseEntity<DriverHistoricalAnalysisResponseDto> response =
                historicalAnalysisController.getDriverHistoricalAnalysis(
                        driverId
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                List.of(),
                response.getBody().getSeasons()
        );

        verify(historicalAnalysisService)
                .getDriverHistoricalAnalysis(driverId);
    }

    @Test
    void shouldPassDriverIdToService() {

        Long driverId = 10L;

        when(historicalAnalysisService.getDriverHistoricalAnalysis(
                driverId
        )).thenReturn(historicalResponse);

        historicalAnalysisController.getDriverHistoricalAnalysis(
                driverId
        );

        verify(historicalAnalysisService)
                .getDriverHistoricalAnalysis(driverId);
    }
}