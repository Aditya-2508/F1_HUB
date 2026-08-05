package com.aditya.f1hub.service;

import com.aditya.f1hub.dto.circuit.CircuitRequestDto;
import com.aditya.f1hub.dto.circuit.CircuitResponseDto;

import java.util.List;

public interface CircuitService {

    CircuitResponseDto createCircuit(CircuitRequestDto requestDto);

    List<CircuitResponseDto> getAllCircuits();

    CircuitResponseDto getCircuitById(Long id);

    CircuitResponseDto updateCircuit(
            Long id,
            CircuitRequestDto requestDto);

    void deleteCircuit(Long id);

    List<CircuitResponseDto> searchCircuits(
            String circuitName,
            String country,
            Boolean active);

}