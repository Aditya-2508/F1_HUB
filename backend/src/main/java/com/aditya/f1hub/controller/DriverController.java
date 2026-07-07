package com.aditya.f1hub.controller;

import com.aditya.f1hub.dto.ApiResponse;
import com.aditya.f1hub.dto.driver.DriverRequestDto;
import com.aditya.f1hub.dto.driver.DriverResponseDto;
import com.aditya.f1hub.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<ApiResponse<DriverResponseDto>> createDriver(
            @Valid @RequestBody DriverRequestDto requestDto) {

        DriverResponseDto responseDto = driverService.createDriver(requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Driver created successfully.",
                        responseDto
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DriverResponseDto>>> getAllDrivers() {

        List<DriverResponseDto> drivers = driverService.getAllDrivers();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Drivers retrieved successfully.",
                        drivers
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverResponseDto>> getDriverById(
            @PathVariable Long id) {

        DriverResponseDto driver = driverService.getDriverById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Driver retrieved successfully.",
                        driver
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverResponseDto>> updateDriver(
            @PathVariable Long id,
            @Valid @RequestBody DriverRequestDto requestDto) {

        DriverResponseDto updatedDriver = driverService.updateDriver(id, requestDto);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Driver updated successfully.",
                        updatedDriver
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDriver(
            @PathVariable Long id) {

        driverService.deleteDriver(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Driver deleted successfully.",
                        null
                )
        );
    }

}