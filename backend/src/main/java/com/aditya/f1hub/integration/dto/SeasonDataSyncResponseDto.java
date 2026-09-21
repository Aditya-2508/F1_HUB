package com.aditya.f1hub.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeasonDataSyncResponseDto {

    private Integer seasonYear;

    private String status;

    private long durationMs;

    private SyncEntitySummaryDto races;

    private SyncEntitySummaryDto sessions;

    private SyncEntitySummaryDto drivers;

    private SyncEntitySummaryDto constructors;

    private SyncEntitySummaryDto results;

    private Integer driverStandingsUpdated;

    private Integer constructorStandingsUpdated;

    private List<SyncFailureDto> failures;
}