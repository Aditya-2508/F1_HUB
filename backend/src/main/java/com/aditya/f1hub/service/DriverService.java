package com.aditya.f1hub.service;

import com.aditya.f1hub.dto.driver.DriverRequestDto;
import com.aditya.f1hub.dto.driver.DriverResponseDto;

import java.util.List;

public interface DriverService {

    DriverResponseDto createDriver(DriverRequestDto requestDto);

    List<DriverResponseDto> getAllDrivers();

    DriverResponseDto getDriverById(Long id);

}