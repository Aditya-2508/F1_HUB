package com.aditya.f1hub.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverSyncResponseDto {

    private int totalFetched;

    private int newDrivers;

    private int existingDrivers;

    private int failedDrivers;

}