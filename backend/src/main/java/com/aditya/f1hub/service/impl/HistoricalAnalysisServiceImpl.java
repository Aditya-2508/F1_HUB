package com.aditya.f1hub.service.impl;

import com.aditya.f1hub.dto.analytics.DriverHistoricalAnalysisResponseDto;
import com.aditya.f1hub.dto.analytics.DriverHistoricalSeasonDto;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.entity.DriverStanding;
import com.aditya.f1hub.repository.DriverStandingRepository;
import com.aditya.f1hub.repository.RaceResultRepository;
import com.aditya.f1hub.repository.DriverRepository;
import com.aditya.f1hub.service.HistoricalAnalysisService;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoricalAnalysisServiceImpl implements HistoricalAnalysisService {

    private final DriverRepository driverRepository;
    private final DriverStandingRepository driverStandingRepository;
    private final RaceResultRepository raceResultRepository;

    @Override
    public DriverHistoricalAnalysisResponseDto getDriverHistoricalAnalysis(Long driverId) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Driver",
                                "id",
                                driverId
                        )
                );

        List<DriverStanding> standings =
                driverStandingRepository.findByDriverIdOrderBySeason_YearAsc(driverId);

        Map<Long, Long> podiumsBySeason =
                getPodiumsBySeason(driverId);

        List<DriverHistoricalSeasonDto> seasons =
                standings.stream()
                        .map(standing -> buildHistoricalSeason(
                                standing,
                                podiumsBySeason
                        ))
                        .toList();

        return DriverHistoricalAnalysisResponseDto.builder()
                .driverId(driver.getId())
                .driverName(driver.getFullName())
                .seasons(seasons)
                .build();
    }

    private DriverHistoricalSeasonDto buildHistoricalSeason(
            DriverStanding standing,
            Map<Long, Long> podiumsBySeason
    ) {

        Long seasonId = standing.getSeason().getId();

        return DriverHistoricalSeasonDto.builder()
                .seasonId(seasonId)
                .seasonYear(standing.getSeason().getYear())
                .championshipPosition(standing.getPosition())
                .points(standing.getPoints())
                .wins(standing.getWins())
                .podiums(podiumsBySeason.getOrDefault(seasonId, 0L).intValue())
                .build();
    }

    private Map<Long, Long> getPodiumsBySeason(Long driverId) {

        List<RaceResultRepository.DriverSeasonPodiumProjection> podiums =
                raceResultRepository.findDriverPodiumsBySeason(driverId);

        return podiums.stream()
                .collect(Collectors.toMap(
                        RaceResultRepository.DriverSeasonPodiumProjection::getSeasonId,
                        RaceResultRepository.DriverSeasonPodiumProjection::getPodiums
                ));
    }
}