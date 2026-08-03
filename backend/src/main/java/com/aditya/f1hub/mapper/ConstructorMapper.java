package com.aditya.f1hub.mapper;

import com.aditya.f1hub.dto.constructor.ConstructorRequestDto;
import com.aditya.f1hub.dto.constructor.ConstructorResponseDto;
import com.aditya.f1hub.entity.Constructor;
import org.springframework.stereotype.Component;

@Component
public class ConstructorMapper {

    /**
     * Converts ConstructorRequestDto to Constructor Entity.
     */
    public Constructor toEntity(ConstructorRequestDto requestDto) {

        if (requestDto == null) {
            return null;
        }

        return Constructor.builder()
                .externalConstructorId(requestDto.getExternalConstructorId())
                .name(requestDto.getName())
                .fullName(requestDto.getFullName())
                .nationality(requestDto.getNationality())
                .countryCode(requestDto.getCountryCode())
                .teamColour(requestDto.getTeamColour())
                .logoUrl(requestDto.getLogoUrl())
                .active(requestDto.getActive())
                .build();
    }

    /**
     * Updates an existing Constructor entity.
     */
    public void updateEntityFromDto(
            ConstructorRequestDto requestDto,
            Constructor constructor) {

        constructor.setExternalConstructorId(requestDto.getExternalConstructorId());
        constructor.setName(requestDto.getName());
        constructor.setFullName(requestDto.getFullName());
        constructor.setNationality(requestDto.getNationality());
        constructor.setCountryCode(requestDto.getCountryCode());
        constructor.setTeamColour(requestDto.getTeamColour());
        constructor.setLogoUrl(requestDto.getLogoUrl());
        constructor.setActive(requestDto.getActive());
    }

    /**
     * Converts Constructor Entity to ConstructorResponseDto.
     */
    public ConstructorResponseDto toResponseDto(Constructor constructor) {

        if (constructor == null) {
            return null;
        }

        return ConstructorResponseDto.builder()
                .id(constructor.getId())
                .externalConstructorId(constructor.getExternalConstructorId())
                .name(constructor.getName())
                .fullName(constructor.getFullName())
                .nationality(constructor.getNationality())
                .countryCode(constructor.getCountryCode())
                .teamColour(constructor.getTeamColour())
                .logoUrl(constructor.getLogoUrl())
                .active(constructor.getActive())
                .createdAt(constructor.getCreatedAt())
                .updatedAt(constructor.getUpdatedAt())
                .build();
    }

}