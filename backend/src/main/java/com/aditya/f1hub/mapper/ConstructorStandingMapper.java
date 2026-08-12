package com.aditya.f1hub.mapper;

import com.aditya.f1hub.dto.standings.ConstructorStandingResponseDto;
import com.aditya.f1hub.entity.ConstructorStanding;
import org.springframework.stereotype.Component;

@Component
public class ConstructorStandingMapper {

    public ConstructorStandingResponseDto toResponseDto(
            ConstructorStanding standing
    ) {

        if (standing == null) {
            return null;
        }

        return ConstructorStandingResponseDto.builder()
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
                .constructorId(
                        standing.getConstructor() != null
                                ? standing.getConstructor().getId()
                                : null
                )
                .constructorName(
                        standing.getConstructor() != null
                                ? standing.getConstructor().getName()
                                : null
                )
                .position(standing.getPosition())
                .points(standing.getPoints())
                .wins(standing.getWins())
                .build();
    }
}