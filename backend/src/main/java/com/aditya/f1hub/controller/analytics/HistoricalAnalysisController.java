package com.aditya.f1hub.controller.analytics;

import com.aditya.f1hub.dto.analytics.DriverHistoricalAnalysisResponseDto;
import com.aditya.f1hub.service.HistoricalAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics/historical")
@RequiredArgsConstructor
public class HistoricalAnalysisController {

    private final HistoricalAnalysisService historicalAnalysisService;

    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<DriverHistoricalAnalysisResponseDto> getDriverHistoricalAnalysis(
            @PathVariable Long driverId
    ) {
        return ResponseEntity.ok(
                historicalAnalysisService.getDriverHistoricalAnalysis(driverId)
        );
    }
}