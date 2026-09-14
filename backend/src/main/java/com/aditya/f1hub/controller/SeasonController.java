package com.aditya.f1hub.controller;

import com.aditya.f1hub.dto.ApiResponse;
import com.aditya.f1hub.integration.dto.SeasonSyncResponseDto;
import com.aditya.f1hub.integration.service.SeasonSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seasons")
@RequiredArgsConstructor
public class SeasonController {

    private final SeasonSyncService seasonSyncService;

    /**
     * Synchronize a season.
     */
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<SeasonSyncResponseDto>> syncSeason(
            @RequestParam Integer year) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Season synchronization completed successfully.",
                        seasonSyncService.synchronizeSeason(year)
                )
        );
    }
}