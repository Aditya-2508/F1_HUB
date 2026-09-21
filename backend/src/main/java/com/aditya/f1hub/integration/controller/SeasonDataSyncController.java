package com.aditya.f1hub.integration.controller;

import com.aditya.f1hub.integration.dto.SeasonDataSyncResponseDto;
import com.aditya.f1hub.integration.service.SeasonDataSyncOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync/seasons")
@RequiredArgsConstructor
public class SeasonDataSyncController {

    private final SeasonDataSyncOrchestrator seasonDataSyncOrchestrator;

    /**
     * Synchronizes all F1Hub data for a complete season.
     *
     * @param year championship season year
     * @return complete synchronization summary
     */
    @PostMapping("/{year}")
    public ResponseEntity<SeasonDataSyncResponseDto> synchronizeSeason(
            @PathVariable Integer year
    ) {

        SeasonDataSyncResponseDto response =
                seasonDataSyncOrchestrator.synchronizeSeason(year);

        return ResponseEntity.ok(response);
    }
}