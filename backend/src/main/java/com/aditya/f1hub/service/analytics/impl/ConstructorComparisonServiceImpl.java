package com.aditya.f1hub.service.analytics.impl;

import com.aditya.f1hub.dto.analytics.ConstructorComparisonResponseDto;
import com.aditya.f1hub.entity.Constructor;
import com.aditya.f1hub.exception.BadRequestException;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.repository.ConstructorRepository;
import com.aditya.f1hub.repository.ConstructorStandingRepository;
import com.aditya.f1hub.repository.ConstructorStandingStatisticsProjection;
import com.aditya.f1hub.repository.RaceResultRepository;
import com.aditya.f1hub.service.analytics.ConstructorComparisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConstructorComparisonServiceImpl
        implements ConstructorComparisonService {

    private final ConstructorRepository constructorRepository;
    private final ConstructorStandingRepository constructorStandingRepository;
    private final RaceResultRepository raceResultRepository;

    @Override
    public List<ConstructorComparisonResponseDto> compareConstructors(
            List<Long> constructorIds
    ) {
        validateConstructorIds(constructorIds);

        return constructorIds.stream()
                .map(this::buildConstructorComparison)
                .toList();
    }

    private void validateConstructorIds(List<Long> constructorIds) {

        if (constructorIds == null || constructorIds.size() < 2) {
            throw new BadRequestException(
                    "At least two constructors are required for comparison."
            );
        }

        if (constructorIds.stream().anyMatch(Objects::isNull)) {
            throw new BadRequestException(
                    "Constructor IDs must not contain null values."
            );
        }

        if (constructorIds.size()
                != constructorIds.stream().distinct().count()) {

            throw new BadRequestException(
                    "Duplicate constructor IDs are not allowed."
            );
        }
    }

    private ConstructorComparisonResponseDto buildConstructorComparison(
            Long constructorId
    ) {

        Constructor constructor = constructorRepository.findById(constructorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Constructor",
                                "id",
                                constructorId
                        )
                );

        ConstructorStandingStatisticsProjection statistics =
                constructorStandingRepository
                        .findConstructorStandingStatistics(constructorId);

        long podiums =
                raceResultRepository.countConstructorPodiums(constructorId);

        return ConstructorComparisonResponseDto.builder()
                .constructorId(constructor.getId())
                .constructorName(constructor.getName())
                .constructorFullName(constructor.getFullName())
                .nationality(constructor.getNationality())
                .countryCode(constructor.getCountryCode())
                .championships(
                        toInteger(statistics.getChampionships())
                )
                .wins(
                        toInteger(statistics.getWins())
                )
                .podiums(
                        toInteger(podiums)
                )
                .careerPoints(
                        toDouble(statistics.getPoints())
                )
                .build();
    }

    private Integer toInteger(Object value) {

        if (value == null) {
            return 0;
        }

        return ((Number) value).intValue();
    }

    private Double toDouble(Object value) {

        if (value == null) {
            return 0.0;
        }

        return ((Number) value).doubleValue();
    }
}