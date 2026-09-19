package com.aditya.f1hub.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeasonStatisticsResponseDto {

    private Long seasonId;
    private Integer seasonYear;

    private int totalRaces;
    private int completedRaces;
    private int cancelledRaces;

    private int totalDrivers;
    private int totalConstructors;

    private double totalPoints;

    private List<SeasonDriverStatisticsDto> driverStatistics;
    private List<SeasonConstructorStatisticsDto> constructorStatistics;
}