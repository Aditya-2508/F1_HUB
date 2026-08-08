package com.aditya.f1hub.integration.mapper;

import com.aditya.f1hub.entity.Circuit;
import com.aditya.f1hub.integration.dto.OpenF1CircuitDto;
import org.springframework.stereotype.Component;

@Component
public class OpenF1CircuitMapper {

    public Circuit toEntity(OpenF1CircuitDto dto) {

        if (dto == null) {
            return null;
        }

        return Circuit.builder()
                .externalCircuitId(String.valueOf(dto.getCircuitKey()))
                .circuitName(dto.getCircuitShortName())
                .location(dto.getLocation())
                .country(dto.getCountryName())
                .countryCode(dto.getCountryCode())
                .imageUrl(dto.getCircuitImage())
                .active(true)
                .build();
    }
}