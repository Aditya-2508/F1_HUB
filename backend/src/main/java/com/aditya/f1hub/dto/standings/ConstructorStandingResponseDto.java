package com.aditya.f1hub.dto.standings;

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
public class ConstructorStandingResponseDto {

    private Long id;

    private Long seasonId;

    private Integer seasonYear;

    private Long constructorId;

    private String constructorName;

    private Integer position;

    private Double points;

    private Integer wins;
}