package com.aditya.f1hub.controller;

import com.aditya.f1hub.dto.standings.ConstructorStandingResponseDto;
import com.aditya.f1hub.entity.ConstructorStanding;
import com.aditya.f1hub.mapper.ConstructorStandingMapper;
import com.aditya.f1hub.service.ConstructorStandingService;
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
class ConstructorStandingControllerTest {

    @Mock
    private ConstructorStandingService constructorStandingService;

    @Mock
    private ConstructorStandingMapper constructorStandingMapper;

    @InjectMocks
    private ConstructorStandingController constructorStandingController;

    private ConstructorStanding constructorStanding;
    private ConstructorStandingResponseDto constructorResponse;

    @BeforeEach
    void setUp() {

        constructorStanding = new ConstructorStanding();

        constructorResponse = ConstructorStandingResponseDto.builder()
                .id(1L)
                .seasonId(1L)
                .seasonYear(2026)
                .constructorId(1L)
                .constructorName("Constructor A")
                .position(1)
                .points(51.0)
                .wins(2)
                .build();
    }

    @Test
    void shouldGetConstructorStandings() {

        when(constructorStandingService.getStandingsBySeason(1L))
                .thenReturn(List.of(constructorStanding));

        when(constructorStandingMapper.toResponseDto(constructorStanding))
                .thenReturn(constructorResponse);

        ResponseEntity<?> response =
                constructorStandingController.getConstructorStandings(1L);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        verify(constructorStandingService)
                .getStandingsBySeason(1L);

        verify(constructorStandingMapper)
                .toResponseDto(constructorStanding);
    }

    @Test
    void shouldReturnEmptyConstructorStandings() {

        when(constructorStandingService.getStandingsBySeason(1L))
                .thenReturn(List.of());

        ResponseEntity<?> response =
                constructorStandingController.getConstructorStandings(1L);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        verify(constructorStandingService)
                .getStandingsBySeason(1L);
    }
}