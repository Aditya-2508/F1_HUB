package com.aditya.f1hub.integration.controller;

import com.aditya.f1hub.integration.dto.SeasonDataSyncResponseDto;
import com.aditya.f1hub.integration.service.SeasonDataSyncOrchestrator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeasonDataSyncControllerTest {

    @Mock
    private SeasonDataSyncOrchestrator seasonDataSyncOrchestrator;

    @InjectMocks
    private SeasonDataSyncController controller;

    @Test
    void shouldSynchronizeSeasonSuccessfully() {

        Integer year = 2026;

        SeasonDataSyncResponseDto response =
                SeasonDataSyncResponseDto.builder()
                        .seasonYear(year)
                        .status("SUCCESS")
                        .durationMs(5000L)
                        .driverStandingsUpdated(22)
                        .constructorStandingsUpdated(11)
                        .build();

        when(
                seasonDataSyncOrchestrator.synchronizeSeason(year)
        ).thenReturn(response);

        ResponseEntity<SeasonDataSyncResponseDto> result =
                controller.synchronizeSeason(year);

        assertThat(result.getStatusCode().value())
                .isEqualTo(200);

        assertThat(result.getBody())
                .isNotNull();

        assertThat(result.getBody().getSeasonYear())
                .isEqualTo(2026);

        assertThat(result.getBody().getStatus())
                .isEqualTo("SUCCESS");

        assertThat(result.getBody().getDriverStandingsUpdated())
                .isEqualTo(22);

        assertThat(result.getBody().getConstructorStandingsUpdated())
                .isEqualTo(11);

        verify(
                seasonDataSyncOrchestrator
        ).synchronizeSeason(year);
    }

    @Test
    void shouldPropagateOrchestratorException() {

        Integer year = 2026;

        when(
                seasonDataSyncOrchestrator.synchronizeSeason(year)
        ).thenThrow(
                new IllegalArgumentException(
                        "A valid season year is required."
                )
        );

        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> controller.synchronizeSeason(year)
                )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "A valid season year is required."
                );

        verify(
                seasonDataSyncOrchestrator
        ).synchronizeSeason(year);
    }
}