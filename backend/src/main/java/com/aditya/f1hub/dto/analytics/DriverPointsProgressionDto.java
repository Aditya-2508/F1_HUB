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
public class DriverPointsProgressionDto {

    private Long raceId;
    private Integer round;
    private String raceName;
    private Double points;
    private Double cumulativePoints;
}