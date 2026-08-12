package com.aditya.f1hub.controller;

import com.aditya.f1hub.dto.ApiResponse;
import com.aditya.f1hub.dto.standings.ConstructorStandingResponseDto;
import com.aditya.f1hub.mapper.ConstructorStandingMapper;
import com.aditya.f1hub.service.ConstructorStandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/standings/constructors")
@RequiredArgsConstructor
public class ConstructorStandingController {

    private final ConstructorStandingService constructorStandingService;
    private final ConstructorStandingMapper constructorStandingMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConstructorStandingResponseDto>>> getConstructorStandings(
            @RequestParam Long seasonId) {

        List<ConstructorStandingResponseDto> standings =
                constructorStandingService
                        .getStandingsBySeason(seasonId)
                        .stream()
                        .map(constructorStandingMapper::toResponseDto)
                        .toList();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Constructor standings retrieved successfully.",
                        standings
                )
        );
    }
}