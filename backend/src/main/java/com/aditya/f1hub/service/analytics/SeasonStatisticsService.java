package com.aditya.f1hub.service.analytics;

import com.aditya.f1hub.dto.analytics.SeasonStatisticsResponseDto;

public interface SeasonStatisticsService {

    SeasonStatisticsResponseDto getSeasonStatistics(Long seasonId);
}