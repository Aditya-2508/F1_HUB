package com.aditya.f1hub.service;

import com.aditya.f1hub.dto.common.PageResponse;
import com.aditya.f1hub.dto.driver.DriverRequestDto;
import com.aditya.f1hub.dto.driver.DriverResponseDto;

import java.util.List;

public interface DriverService {

    DriverResponseDto createDriver(DriverRequestDto requestDto);

    List<DriverResponseDto> getAllDrivers();

    DriverResponseDto getDriverById(Long id);

    DriverResponseDto updateDriver(Long id, DriverRequestDto requestDto);

    void deleteDriver(Long id);

    PageResponse<DriverResponseDto> searchDrivers(
            String name,
            String nationality,
            Boolean active,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );
}