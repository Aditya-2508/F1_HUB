package com.aditya.f1hub.controller.analytics;

import com.aditya.f1hub.dto.analytics.DriverPointsProgressionResponseDto;
import com.aditya.f1hub.service.analytics.PointsProgressionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics/points-progression")
@RequiredArgsConstructor
public class PointsProgressionController {

    private final PointsProgressionService pointsProgressionService;

    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<DriverPointsProgressionResponseDto>
    getDriverPointsProgression(
            @PathVariable Long driverId,
            @RequestParam Long seasonId
    ) {
        return ResponseEntity.ok(
                pointsProgressionService.getDriverPointsProgression(
                        driverId,
                        seasonId
                )
        );
    }
}