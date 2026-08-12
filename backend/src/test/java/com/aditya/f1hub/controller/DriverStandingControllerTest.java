package com.aditya.f1hub.controller;

import com.aditya.f1hub.dto.standings.DriverStandingResponseDto;
import com.aditya.f1hub.entity.DriverStanding;
import com.aditya.f1hub.mapper.DriverStandingMapper;
import com.aditya.f1hub.service.DriverStandingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverStandingControllerTest {

    @Mock
    private DriverStandingService driverStandingService;

    @Mock
    private DriverStandingMapper driverStandingMapper;

    @InjectMocks
    private DriverStandingController driverStandingController;

    private DriverStanding driverStanding;
    private DriverStandingResponseDto driverResponse;

    @BeforeEach
    void setUp() {

        driverStanding = new DriverStanding();

        driverResponse = DriverStandingResponseDto.builder()
                .id(1L)
                .seasonId(1L)
                .seasonYear(2026)
                .driverId(1L)
                .driverName("Driver A")
                .driverAbbreviation("DRA")
                .position(1)
                .points(51.0)
                .wins(2)
                .build();
    }

    @Test
    void shouldGetDriverStandings() {

        when(driverStandingService.getStandingsBySeason(1L))
                .thenReturn(List.of(driverStanding));

        when(driverStandingMapper.toResponseDto(driverStanding))
                .thenReturn(driverResponse);

        ResponseEntity<?> response =
                driverStandingController.getDriverStandings(1L);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        verify(driverStandingService)
                .getStandingsBySeason(1L);

        verify(driverStandingMapper)
                .toResponseDto(driverStanding);
    }

    @Test
    void shouldReturnEmptyDriverStandings() {

        when(driverStandingService.getStandingsBySeason(1L))
                .thenReturn(List.of());

        ResponseEntity<?> response =
                driverStandingController.getDriverStandings(1L);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        verify(driverStandingService)
                .getStandingsBySeason(1L);
    }
}