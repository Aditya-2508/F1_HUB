package com.aditya.f1hub.integration.dto;

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
public class CircuitSyncResponseDto {

    private int totalFetched;

    private int newCircuits;

    private int existingCircuits;

    private int failed;
}