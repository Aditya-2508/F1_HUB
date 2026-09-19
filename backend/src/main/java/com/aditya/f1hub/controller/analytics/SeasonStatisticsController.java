package com.aditya.f1hub.controller.analytics;

import com.aditya.f1hub.dto.analytics.SeasonStatisticsResponseDto;
import com.aditya.f1hub.service.analytics.SeasonStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics/seasons")
@RequiredArgsConstructor
public class SeasonStatisticsController {

    private final SeasonStatisticsService seasonStatisticsService;

    @GetMapping("/{seasonId}/statistics")
    public ResponseEntity<SeasonStatisticsResponseDto> getSeasonStatistics(
            @PathVariable Long seasonId
    ) {
        return ResponseEntity.ok(
                seasonStatisticsService.getSeasonStatistics(seasonId)
        );
    }
}