package com.aditya.f1hub.controller.analytics;

import com.aditya.f1hub.dto.analytics.DriverComparisonResponseDto;
import com.aditya.f1hub.service.analytics.DriverComparisonService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics/drivers")
@RequiredArgsConstructor
public class DriverComparisonController {

    private final DriverComparisonService driverComparisonService;

    @GetMapping("/compare")
    public ResponseEntity<List<DriverComparisonResponseDto>> compareDrivers(
            @RequestParam
            @NotEmpty(message = "At least one driver ID is required")
            List<Long> driverIds
    ) {
        return ResponseEntity.ok(
                driverComparisonService.compareDrivers(driverIds)
        );
    }
}