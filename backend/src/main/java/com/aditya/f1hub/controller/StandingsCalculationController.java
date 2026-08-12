package com.aditya.f1hub.controller;

import com.aditya.f1hub.dto.ApiResponse;
import com.aditya.f1hub.service.StandingsCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/standings")
@RequiredArgsConstructor
public class StandingsCalculationController {

    private final StandingsCalculationService standingsCalculationService;

    /**
     * Calculates and persists driver and constructor standings
     * for the specified season.
     *
     * Example:
     * POST /api/standings/calculate?seasonId=1
     */
    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<Void>> calculateStandings(
            @RequestParam Long seasonId) {

        standingsCalculationService.calculateStandings(seasonId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Standings calculated successfully.",
                        null
                )
        );
    }
}