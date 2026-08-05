package com.aditya.f1hub.dto.circuit;

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
public class CircuitResponseDto {

    private Long id;

    private String externalCircuitId;

    private String circuitName;

    private String location;

    private String country;

    private String countryCode;

    private Double circuitLength;

    private Integer numberOfTurns;

    private String lapRecord;

    private String lapRecordHolder;

    private Integer firstGrandPrix;

    private Double latitude;

    private Double longitude;

    private String imageUrl;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}