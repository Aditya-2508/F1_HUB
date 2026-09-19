package com.aditya.f1hub.service.analytics;

import com.aditya.f1hub.dto.analytics.RaceAnalyticsResponseDto;

public interface RaceAnalyticsService {

    /**
     * Retrieves analytical information for a specific race.
     *
     * @param raceId F1Hub race ID
     * @return race analytics response
     */
    RaceAnalyticsResponseDto getRaceAnalytics(Long raceId);
}