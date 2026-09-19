package com.aditya.f1hub.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceAnalyticsResponseDto {

    private Long raceId;
    private String raceName;
    private Long seasonId;
    private Integer seasonYear;
    private Integer roundNumber;

    private RaceAnalyticsStatisticsDto statistics;

    private List<RaceAnalyticsPodiumDto> podium;

    private RaceAnalyticsDriverDto fastestLap;

    private List<RaceAnalyticsDriverDto> driverPerformances;
}