package com.aditya.f1hub.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstructorSyncResponseDto {

    private int totalFetched;

    private int newConstructors;

    private int existingConstructors;

    private int failedConstructors;

}