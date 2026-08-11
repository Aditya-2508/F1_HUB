package com.aditya.f1hub.dto.result;

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
public class RaceResultSyncResponse {

    private Long sessionId;

    private Integer synchronizedCount;

    private Integer createdCount;

    private Integer updatedCount;
}