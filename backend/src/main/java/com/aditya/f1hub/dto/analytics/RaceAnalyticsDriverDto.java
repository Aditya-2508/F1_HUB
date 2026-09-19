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
public class RaceAnalyticsDriverDto {

    private Long driverId;
    private String driverName;

    private Long constructorId;
    private String constructorName;

    private Integer gridPosition;
    private Integer finishPosition;
    private Integer positionChange;

    private Double points;

    private String status;

    private Integer lapsCompleted;

    private Double durationSeconds;

    private Double gapToLeaderSeconds;
    private String gapToLeaderText;

    private Double fastestLapTimeSeconds;
    private Integer fastestLapNumber;
}