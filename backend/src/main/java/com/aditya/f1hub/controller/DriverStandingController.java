package com.aditya.f1hub.controller;

import com.aditya.f1hub.dto.ApiResponse;
import com.aditya.f1hub.dto.standings.DriverStandingResponseDto;
import com.aditya.f1hub.mapper.DriverStandingMapper;
import com.aditya.f1hub.service.DriverStandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/standings/drivers")
@RequiredArgsConstructor
public class DriverStandingController {

    private final DriverStandingService driverStandingService;
    private final DriverStandingMapper driverStandingMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DriverStandingResponseDto>>> getDriverStandings(
            @RequestParam Long seasonId) {

        List<DriverStandingResponseDto> standings =
                driverStandingService
                        .getStandingsBySeason(seasonId)
                        .stream()
                        .map(driverStandingMapper::toResponseDto)
                        .toList();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Driver standings retrieved successfully.",
                        standings
                )
        );
    }
}