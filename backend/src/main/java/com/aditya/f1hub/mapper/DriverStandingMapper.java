package com.aditya.f1hub.mapper;

import com.aditya.f1hub.dto.standings.DriverStandingResponseDto;
import com.aditya.f1hub.entity.DriverStanding;
import org.springframework.stereotype.Component;

@Component
public class DriverStandingMapper {

    public DriverStandingResponseDto toResponseDto(
            DriverStanding standing
    ) {

        if (standing == null) {
            return null;
        }

        return DriverStandingResponseDto.builder()
                .id(standing.getId())
                .seasonId(
                        standing.getSeason() != null
                                ? standing.getSeason().getId()
                                : null
                )
                .seasonYear(
                        standing.getSeason() != null
                                ? standing.getSeason().getYear()
                                : null
                )
                .driverId(
                        standing.getDriver() != null
                                ? standing.getDriver().getId()
                                : null
                )
                .driverName(
                        standing.getDriver() != null
                                ? standing.getDriver().getFullName()
                                : null
                )
                .driverAbbreviation(
                        standing.getDriver() != null
                                ? standing.getDriver().getAbbreviation()
                                : null
                )
                .position(standing.getPosition())
                .points(standing.getPoints())
                .wins(standing.getWins())
                .build();
    }
}