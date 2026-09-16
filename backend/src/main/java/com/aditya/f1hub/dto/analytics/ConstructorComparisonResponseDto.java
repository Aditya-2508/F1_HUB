package com.aditya.f1hub.dto.analytics;

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
public class ConstructorComparisonResponseDto {

    private Long constructorId;

    private String constructorName;

    private String constructorFullName;

    private String nationality;

    private String countryCode;

    private Integer championships;

    private Integer wins;

    private Integer podiums;

    private Double careerPoints;
}