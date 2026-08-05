package com.aditya.f1hub.dto.constructor;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstructorResponseDto {

    private Long id;

    private String externalConstructorId;

    private String name;

    private String fullName;

    private String nationality;

    private String countryCode;

    private String teamColour;

    private String logoUrl;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}