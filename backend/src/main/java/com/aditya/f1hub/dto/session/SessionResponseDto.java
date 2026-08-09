package com.aditya.f1hub.dto.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponseDto {

    private Long id;

    private String externalSessionId;

    private String sessionName;

    private String sessionType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Boolean cancelled;

    private Boolean active;

    private Long raceId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}