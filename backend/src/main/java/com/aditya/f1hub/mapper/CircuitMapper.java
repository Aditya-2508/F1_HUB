package com.aditya.f1hub.mapper;

import com.aditya.f1hub.dto.circuit.CircuitRequestDto;
import com.aditya.f1hub.dto.circuit.CircuitResponseDto;
import com.aditya.f1hub.entity.Circuit;
import org.springframework.stereotype.Component;

@Component
public class CircuitMapper {

    /**
     * Converts CircuitRequestDto to Circuit Entity
     */
    public Circuit toEntity(CircuitRequestDto requestDto) {

        if (requestDto == null) {
            return null;
        }

        return Circuit.builder()
                .externalCircuitId(requestDto.getExternalCircuitId())
                .circuitName(requestDto.getCircuitName())
                .location(requestDto.getLocation())
                .country(requestDto.getCountry())
                .countryCode(requestDto.getCountryCode())
                .circuitLength(requestDto.getCircuitLength())
                .numberOfTurns(requestDto.getNumberOfTurns())
                .lapRecord(requestDto.getLapRecord())
                .lapRecordHolder(requestDto.getLapRecordHolder())
                .firstGrandPrix(requestDto.getFirstGrandPrix())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .imageUrl(requestDto.getImageUrl())
                .active(requestDto.getActive())
                .build();
    }

    /**
     * Updates an existing Circuit entity from CircuitRequestDto
     */
    public void updateEntityFromDto(
            CircuitRequestDto requestDto,
            Circuit circuit) {

        if (requestDto == null || circuit == null) {
            return;
        }

        circuit.setExternalCircuitId(requestDto.getExternalCircuitId());
        circuit.setCircuitName(requestDto.getCircuitName());
        circuit.setLocation(requestDto.getLocation());
        circuit.setCountry(requestDto.getCountry());
        circuit.setCountryCode(requestDto.getCountryCode());
        circuit.setCircuitLength(requestDto.getCircuitLength());
        circuit.setNumberOfTurns(requestDto.getNumberOfTurns());
        circuit.setLapRecord(requestDto.getLapRecord());
        circuit.setLapRecordHolder(requestDto.getLapRecordHolder());
        circuit.setFirstGrandPrix(requestDto.getFirstGrandPrix());
        circuit.setLatitude(requestDto.getLatitude());
        circuit.setLongitude(requestDto.getLongitude());
        circuit.setImageUrl(requestDto.getImageUrl());
        circuit.setActive(requestDto.getActive());
    }

    /**
     * Converts Circuit Entity to CircuitResponseDto
     */
    public CircuitResponseDto toResponseDto(Circuit circuit) {

        if (circuit == null) {
            return null;
        }

        return CircuitResponseDto.builder()
                .id(circuit.getId())
                .externalCircuitId(circuit.getExternalCircuitId())
                .circuitName(circuit.getCircuitName())
                .location(circuit.getLocation())
                .country(circuit.getCountry())
                .countryCode(circuit.getCountryCode())
                .circuitLength(circuit.getCircuitLength())
                .numberOfTurns(circuit.getNumberOfTurns())
                .lapRecord(circuit.getLapRecord())
                .lapRecordHolder(circuit.getLapRecordHolder())
                .firstGrandPrix(circuit.getFirstGrandPrix())
                .latitude(circuit.getLatitude())
                .longitude(circuit.getLongitude())
                .imageUrl(circuit.getImageUrl())
                .active(circuit.getActive())
                .createdAt(circuit.getCreatedAt())
                .updatedAt(circuit.getUpdatedAt())
                .build();
    }

}