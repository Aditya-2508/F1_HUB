package com.aditya.f1hub.service;

import com.aditya.f1hub.dto.race.RaceRequestDto;
import com.aditya.f1hub.dto.race.RaceResponseDto;

import java.util.List;

public interface RaceService {

    RaceResponseDto createRace(RaceRequestDto requestDto);

    List<RaceResponseDto> getAllRaces();

    RaceResponseDto getRaceById(Long id);

    RaceResponseDto updateRace(
            Long id,
            RaceRequestDto requestDto);

    void deleteRace(Long id);

    List<RaceResponseDto> searchRaces(
            String name,
            Long seasonId,
            Long circuitId,
            String countryName,
            Boolean active,
            Boolean cancelled);
}