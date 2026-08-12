package com.aditya.f1hub.controller;

import com.aditya.f1hub.service.StandingsCalculationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StandingsCalculationControllerTest {

    @Mock
    private StandingsCalculationService standingsCalculationService;

    @InjectMocks
    private StandingsCalculationController standingsCalculationController;

    @Test
    void shouldCalculateStandings() {

        ResponseEntity<?> response =
                standingsCalculationController.calculateStandings(1L);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        verify(standingsCalculationService)
                .calculateStandings(1L);
    }
}