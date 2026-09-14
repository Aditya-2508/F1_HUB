package com.aditya.f1hub.integration.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeasonSyncResponseDto {

    private Integer year;

    private boolean created;

    private boolean existing;
}