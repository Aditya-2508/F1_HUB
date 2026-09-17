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
public class DriverHistoricalAnalysisResponseDto {

    private Long driverId;

    private String driverName;

    private List<DriverHistoricalSeasonDto> seasons;
}