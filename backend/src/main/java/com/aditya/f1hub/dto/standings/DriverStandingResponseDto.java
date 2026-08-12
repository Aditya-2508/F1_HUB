package com.aditya.f1hub.dto.standings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverStandingResponseDto {

    private Long id;

    private Long seasonId;

    private Integer seasonYear;

    private Long driverId;

    private String driverName;

    private String driverAbbreviation;

    private Integer position;

    private Double points;

    private Integer wins;
}