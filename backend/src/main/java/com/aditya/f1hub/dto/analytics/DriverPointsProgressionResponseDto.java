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
public class DriverPointsProgressionResponseDto {

    private Long driverId;
    private String driverName;
    private Long seasonId;
    private Integer seasonYear;
    private List<DriverPointsProgressionDto> points;
}