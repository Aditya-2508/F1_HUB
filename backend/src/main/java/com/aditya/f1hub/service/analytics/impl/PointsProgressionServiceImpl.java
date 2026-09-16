package com.aditya.f1hub.service.analytics.impl;

import com.aditya.f1hub.dto.analytics.DriverPointsProgressionDto;
import com.aditya.f1hub.dto.analytics.DriverPointsProgressionResponseDto;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.RaceResult;
import com.aditya.f1hub.entity.Season;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.repository.DriverRepository;
import com.aditya.f1hub.repository.RaceResultRepository;
import com.aditya.f1hub.repository.SeasonRepository;
import com.aditya.f1hub.service.analytics.PointsProgressionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointsProgressionServiceImpl implements PointsProgressionService {

    private final DriverRepository driverRepository;
    private final SeasonRepository seasonRepository;
    private final RaceResultRepository raceResultRepository;

    @Override
    public DriverPointsProgressionResponseDto getDriverPointsProgression(
            Long driverId,
            Long seasonId
    ) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Driver",
                                "id",
                                driverId
                        )
                );

        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Season",
                                "id",
                                seasonId
                        )
                );

        List<RaceResult> seasonResults =
                raceResultRepository.findAllBySeasonId(seasonId);

        double cumulativePoints = 0.0;

        List<DriverPointsProgressionDto> progression =
                new ArrayList<>();

        for (RaceResult result : seasonResults) {

            if (!result.getDriver().getId().equals(driverId)) {
                continue;
            }

            if (!"race".equalsIgnoreCase(
                    result.getSession().getSessionType()
            )) {
                continue;
            }

            Race race = result.getSession().getRace();

            double racePoints =
                    result.getPoints() != null
                            ? result.getPoints()
                            : 0.0;

            cumulativePoints += racePoints;

            progression.add(
                    DriverPointsProgressionDto.builder()
                            .raceId(race.getId())
                            .round(race.getRoundNumber())
                            .raceName(race.getName())
                            .points(racePoints)
                            .cumulativePoints(cumulativePoints)
                            .build()
            );
        }

        return DriverPointsProgressionResponseDto.builder()
                .driverId(driver.getId())
                .driverName(driver.getFullName())
                .seasonId(season.getId())
                .seasonYear(season.getYear())
                .points(progression)
                .build();
    }
}