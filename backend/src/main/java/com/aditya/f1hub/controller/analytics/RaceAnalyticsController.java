package com.aditya.f1hub.controller.analytics;

import com.aditya.f1hub.dto.analytics.RaceAnalyticsResponseDto;
import com.aditya.f1hub.service.analytics.RaceAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics/races")
@RequiredArgsConstructor
public class RaceAnalyticsController {

    private final RaceAnalyticsService raceAnalyticsService;

    @GetMapping("/{raceId}")
    public ResponseEntity<RaceAnalyticsResponseDto> getRaceAnalytics(
            @PathVariable Long raceId
    ) {
        return ResponseEntity.ok(
                raceAnalyticsService.getRaceAnalytics(raceId)
        );
    }
}