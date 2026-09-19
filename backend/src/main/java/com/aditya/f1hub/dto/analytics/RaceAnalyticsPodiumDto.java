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
public class RaceAnalyticsPodiumDto {

    private Integer position;

    private Long driverId;
    private String driverName;

    private Long constructorId;
    private String constructorName;

    private Double points;
}