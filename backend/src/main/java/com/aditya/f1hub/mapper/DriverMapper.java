package com.aditya.f1hub.mapper;

import com.aditya.f1hub.dto.driver.DriverRequestDto;
import com.aditya.f1hub.dto.driver.DriverResponseDto;
import com.aditya.f1hub.entity.Driver;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    /**
     * Converts DriverRequestDto to Driver Entity.
     */
    public Driver toEntity(DriverRequestDto requestDto) {


        if (requestDto == null) {
            return null;
        }

        return Driver.builder()
                .externalDriverId(requestDto.getExternalDriverId())
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .fullName(requestDto.getFullName())
                .driverNumber(requestDto.getDriverNumber())
                .abbreviation(requestDto.getAbbreviation())
                .nationality(requestDto.getNationality())
                .dateOfBirth(requestDto.getDateOfBirth())
                .profileImageUrl(requestDto.getProfileImageUrl())
                .permanentNumber(requestDto.getPermanentNumber())
                .active(requestDto.getActive())
                .build();
    }


    /**
     * Updates an existing Driver entity using values from DriverRequestDto.
     */
    public void updateEntityFromDto(DriverRequestDto requestDto, Driver driver) {

        driver.setExternalDriverId(requestDto.getExternalDriverId());
        driver.setFirstName(requestDto.getFirstName());
        driver.setLastName(requestDto.getLastName());
        driver.setFullName(requestDto.getFullName());
        driver.setDriverNumber(requestDto.getDriverNumber());
        driver.setAbbreviation(requestDto.getAbbreviation());
        driver.setNationality(requestDto.getNationality());
        driver.setDateOfBirth(requestDto.getDateOfBirth());
        driver.setProfileImageUrl(requestDto.getProfileImageUrl());
        driver.setPermanentNumber(requestDto.getPermanentNumber());
        driver.setActive(requestDto.getActive());

    }

    /**
     * Converts Driver Entity to DriverResponseDto.
     */
    public DriverResponseDto toResponseDto(Driver driver) {

        if (driver == null) {
            return null;
        }

        return DriverResponseDto.builder()
                .id(driver.getId())
                .externalDriverId(driver.getExternalDriverId())
                .firstName(driver.getFirstName())
                .lastName(driver.getLastName())
                .fullName(driver.getFullName())
                .driverNumber(driver.getDriverNumber())
                .abbreviation(driver.getAbbreviation())
                .nationality(driver.getNationality())
                .dateOfBirth(driver.getDateOfBirth())
                .profileImageUrl(driver.getProfileImageUrl())
                .permanentNumber(driver.getPermanentNumber())
                .active(driver.getActive())
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }

}