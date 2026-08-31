package com.aditya.f1hub.dto.search;

import com.aditya.f1hub.dto.circuit.CircuitResponseDto;
import com.aditya.f1hub.dto.constructor.ConstructorResponseDto;
import com.aditya.f1hub.dto.driver.DriverResponseDto;
import com.aditya.f1hub.dto.race.RaceResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDto {

    private List<DriverResponseDto> drivers;

    private List<ConstructorResponseDto> constructors;

    private List<CircuitResponseDto> circuits;

    private List<RaceResponseDto> races;

    private List<Integer> seasons;
}