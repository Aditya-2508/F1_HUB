package com.aditya.f1hub.service.analytics;

import com.aditya.f1hub.dto.analytics.DriverPointsProgressionResponseDto;

public interface PointsProgressionService {

    DriverPointsProgressionResponseDto getDriverPointsProgression(
            Long driverId,
            Long seasonId
    );
}