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
public class DriverHistoricalSeasonDto {

    private Long seasonId;

    private Integer seasonYear;

    private Integer championshipPosition;

    private Double points;

    private Integer wins;

    private Integer podiums;
}