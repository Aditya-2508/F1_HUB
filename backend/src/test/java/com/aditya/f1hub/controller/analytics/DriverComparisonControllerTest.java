package com.aditya.f1hub.controller.analytics;

import com.aditya.f1hub.dto.analytics.DriverComparisonResponseDto;
import com.aditya.f1hub.service.analytics.DriverComparisonService;
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
class DriverComparisonControllerTest {

    @Mock
    private DriverComparisonService driverComparisonService;

    @InjectMocks
    private DriverComparisonController driverComparisonController;

    private DriverComparisonResponseDto firstDriver;
    private DriverComparisonResponseDto secondDriver;

    @BeforeEach
    void setUp() {

        firstDriver = DriverComparisonResponseDto.builder()
                .driverId(1L)
                .driverName("Driver A")
                .nationality("British")
                .championships(2)
                .wins(5)
                .podiums(12)
                .polePositions(8)
                .fastestLaps(6)
                .careerPoints(250.5)
                .build();

        secondDriver = DriverComparisonResponseDto.builder()
                .driverId(2L)
                .driverName("Driver B")
                .nationality("Dutch")
                .championships(1)
                .wins(3)
                .podiums(9)
                .polePositions(5)
                .fastestLaps(4)
                .careerPoints(180.0)
                .build();
    }

    @Test
    void shouldCompareDrivers() {

        List<Long> driverIds = List.of(1L, 2L);

        List<DriverComparisonResponseDto> comparison =
                List.of(firstDriver, secondDriver);

        when(driverComparisonService.compareDrivers(driverIds))
                .thenReturn(comparison);

        ResponseEntity<List<DriverComparisonResponseDto>> response =
                driverComparisonController.compareDrivers(driverIds);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertSame(
                comparison,
                response.getBody()
        );

        assertEquals(
                2,
                response.getBody().size()
        );

        assertEquals(
                1L,
                response.getBody().get(0).getDriverId()
        );

        assertEquals(
                2L,
                response.getBody().get(1).getDriverId()
        );

        verify(driverComparisonService)
                .compareDrivers(driverIds);
    }

    @Test
    void shouldReturnEmptyComparisonResult() {

        List<Long> driverIds = List.of(1L, 2L);

        when(driverComparisonService.compareDrivers(driverIds))
                .thenReturn(List.of());

        ResponseEntity<List<DriverComparisonResponseDto>> response =
                driverComparisonController.compareDrivers(driverIds);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                List.of(),
                response.getBody()
        );

        verify(driverComparisonService)
                .compareDrivers(driverIds);
    }

    @Test
    void shouldPassDriverIdsToService() {

        List<Long> driverIds = List.of(5L, 10L, 15L);

        when(driverComparisonService.compareDrivers(driverIds))
                .thenReturn(List.of(
                        firstDriver,
                        secondDriver
                ));

        driverComparisonController.compareDrivers(driverIds);

        verify(driverComparisonService)
                .compareDrivers(driverIds);
    }
}