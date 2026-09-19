package com.aditya.f1hub.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceAnalyticsStatisticsDto {

    private int totalDrivers;
    private int finishers;
    private int dnfs;
    private int dns;
    private int dsqs;
    private int pointsScorers;
    private double totalPoints;
}