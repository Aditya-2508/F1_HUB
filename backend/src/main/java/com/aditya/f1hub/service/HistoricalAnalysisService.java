package com.aditya.f1hub.service;

import com.aditya.f1hub.dto.analytics.DriverHistoricalAnalysisResponseDto;

public interface HistoricalAnalysisService {

    DriverHistoricalAnalysisResponseDto getDriverHistoricalAnalysis(Long driverId);
}