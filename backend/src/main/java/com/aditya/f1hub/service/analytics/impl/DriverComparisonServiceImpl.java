package com.aditya.f1hub.service.analytics.impl;

import com.aditya.f1hub.dto.analytics.DriverComparisonResponseDto;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.exception.BadRequestException;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.repository.DriverRepository;
import com.aditya.f1hub.repository.DriverStandingRepository;
import com.aditya.f1hub.repository.RaceResultRepository;
import com.aditya.f1hub.service.analytics.DriverComparisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import com.aditya.f1hub.repository.DriverCareerStatisticsProjection;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverComparisonServiceImpl implements DriverComparisonService {

    private final DriverRepository driverRepository;
    private final RaceResultRepository raceResultRepository;
    private final DriverStandingRepository driverStandingRepository;

    @Override
    public List<DriverComparisonResponseDto> compareDrivers(List<Long> driverIds) {

        validateDriverIds(driverIds);

        return driverIds.stream()
                .map(this::buildDriverComparison)
                .toList();
    }

    private void validateDriverIds(List<Long> driverIds) {

        if (driverIds == null || driverIds.size() < 2) {
            throw new BadRequestException(
                    "At least two drivers are required for comparison."
            );
        }

        if (driverIds.stream().anyMatch(Objects::isNull)) {
            throw new BadRequestException(
                    "Driver IDs must not contain null values."
            );
        }

        if (driverIds.size() != driverIds.stream().distinct().count()) {
            throw new BadRequestException(
                    "Duplicate driver IDs are not allowed."
            );
        }
    }

    private DriverComparisonResponseDto buildDriverComparison(Long driverId) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Driver",
                                "id",
                                driverId
                        )
                );

        DriverCareerStatisticsProjection statistics =
                raceResultRepository.findDriverCareerStatistics(driverId);

        return DriverComparisonResponseDto.builder()
                .driverId(driver.getId())
                .driverName(driver.getFullName())
                .nationality(driver.getNationality())
                .championships(
                        (int) driverStandingRepository
                                .countByDriverIdAndPosition(driverId, 1)
                )
                .wins(toInteger(statistics.getRaceWins()))
                .podiums(toInteger(statistics.getRacePodiums()))
                .polePositions(toInteger(statistics.getQualifyingPoles()))
                .fastestLaps(
                        (int) raceResultRepository
                                .countDriverFastestLaps(driverId)
                )
                .careerPoints(toDouble(statistics.getChampionshipPoints()))
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