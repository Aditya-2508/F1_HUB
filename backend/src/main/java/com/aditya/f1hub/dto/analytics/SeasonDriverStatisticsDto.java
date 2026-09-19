package com.aditya.f1hub.dto.analytics;

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
public class SeasonDriverStatisticsDto {

    private Long driverId;
    private String driverName;

    private Integer position;
    private Double points;
    private Integer wins;
    private Integer podiums;
    private Integer dnfs;
}