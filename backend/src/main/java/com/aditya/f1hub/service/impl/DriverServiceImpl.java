package com.aditya.f1hub.service.impl;

import com.aditya.f1hub.dto.driver.DriverRequestDto;
import com.aditya.f1hub.dto.driver.DriverResponseDto;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.mapper.DriverMapper;
import com.aditya.f1hub.repository.DriverRepository;
import com.aditya.f1hub.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import com.aditya.f1hub.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Override
    public DriverResponseDto createDriver(DriverRequestDto requestDto) {

        Driver driver = driverMapper.toEntity(requestDto);

        Driver savedDriver = driverRepository.save(driver);

        return driverMapper.toResponseDto(savedDriver);
    }

    @Override
    public List<DriverResponseDto> getAllDrivers() {

        return driverRepository.findAll()
                .stream()
                .map(driverMapper::toResponseDto)
                .toList();
    }

    @Override
    public DriverResponseDto getDriverById(Long id) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Driver", "id", id));

        return driverMapper.toResponseDto(driver);
    }

}