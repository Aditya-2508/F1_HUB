package com.aditya.f1hub.controller.analytics;

import com.aditya.f1hub.dto.analytics.DriverPointsProgressionDto;
import com.aditya.f1hub.dto.analytics.DriverPointsProgressionResponseDto;
import com.aditya.f1hub.service.analytics.PointsProgressionService;
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
class PointsProgressionControllerTest {

    @Mock
    private PointsProgressionService pointsProgressionService;

    @InjectMocks
    private PointsProgressionController pointsProgressionController;

    private DriverPointsProgressionResponseDto progressionResponse;

    @BeforeEach
    void setUp() {

        DriverPointsProgressionDto firstRace =
                DriverPointsProgressionDto.builder()
                        .raceId(1L)
                        .round(1)
                        .raceName("Australian Grand Prix")
                        .points(25.0)
                        .cumulativePoints(25.0)
                        .build();

        DriverPointsProgressionDto secondRace =
                DriverPointsProgressionDto.builder()
                        .raceId(2L)
                        .round(2)
                        .raceName("Japanese Grand Prix")
                        .points(18.0)
                        .cumulativePoints(43.0)
                        .build();

        progressionResponse =
                DriverPointsProgressionResponseDto.builder()
                        .driverId(1L)
                        .driverName("Driver A")
                        .seasonId(1L)
                        .seasonYear(2026)
                        .points(List.of(firstRace, secondRace))
                        .build();
    }

    @Test
    void shouldReturnDriverPointsProgression() {

        Long driverId = 1L;
        Long seasonId = 1L;

        when(pointsProgressionService.getDriverPointsProgression(
                driverId,
                seasonId
        )).thenReturn(progressionResponse);

        ResponseEntity<DriverPointsProgressionResponseDto> response =
                pointsProgressionController.getDriverPointsProgression(
                        driverId,
                        seasonId
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertSame(
                progressionResponse,
                response.getBody()
        );

        assertEquals(
                1L,
                response.getBody().getDriverId()
        );

        assertEquals(
                1L,
                response.getBody().getSeasonId()
        );

        assertEquals(
                2,
                response.getBody().getPoints().size()
        );

        assertEquals(
                25.0,
                response.getBody()
                        .getPoints()
                        .get(0)
                        .getCumulativePoints()
        );

        assertEquals(
                43.0,
                response.getBody()
                        .getPoints()
                        .get(1)
                        .getCumulativePoints()
        );

        verify(pointsProgressionService)
                .getDriverPointsProgression(
                        driverId,
                        seasonId
                );
    }

    @Test
    void shouldReturnEmptyProgression() {

        Long driverId = 1L;
        Long seasonId = 1L;

        DriverPointsProgressionResponseDto emptyResponse =
                DriverPointsProgressionResponseDto.builder()
                        .driverId(driverId)
                        .driverName("Driver A")
                        .seasonId(seasonId)
                        .seasonYear(2026)
                        .points(List.of())
                        .build();

        when(pointsProgressionService.getDriverPointsProgression(
                driverId,
                seasonId
        )).thenReturn(emptyResponse);

        ResponseEntity<DriverPointsProgressionResponseDto> response =
                pointsProgressionController.getDriverPointsProgression(
                        driverId,
                        seasonId
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                List.of(),
                response.getBody().getPoints()
        );

        verify(pointsProgressionService)
                .getDriverPointsProgression(
                        driverId,
                        seasonId
                );
    }

    @Test
    void shouldPassDriverIdAndSeasonIdToService() {

        Long driverId = 5L;
        Long seasonId = 2L;

        when(pointsProgressionService.getDriverPointsProgression(
                driverId,
                seasonId
        )).thenReturn(progressionResponse);

        pointsProgressionController.getDriverPointsProgression(
                driverId,
                seasonId
        );

        verify(pointsProgressionService)
                .getDriverPointsProgression(
                        driverId,
                        seasonId
                );
    }
}