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
public class DriverComparisonResponseDto {

    private Long driverId;

    private String driverName;

    private String driverCode;

    private Integer driverNumber;

    private String nationality;

    private Integer championships;

    private Integer wins;

    private Integer podiums;

    private Integer polePositions;

    private Integer fastestLaps;

    private Double careerPoints;
}