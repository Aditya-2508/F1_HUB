package com.aditya.f1hub.dto.race;

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
public class RaceResponseDto {

    private Long id;

    private String externalMeetingId;

    private String name;

    private String officialName;

    private Integer roundNumber;

    private LocalDateTime weekendStart;

    private LocalDateTime weekendEnd;

    private String location;

    private String countryName;

    private String countryCode;

    private String gmtOffset;

    private Boolean cancelled;

    private Boolean active;

    private Long seasonId;

    private Long circuitId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}