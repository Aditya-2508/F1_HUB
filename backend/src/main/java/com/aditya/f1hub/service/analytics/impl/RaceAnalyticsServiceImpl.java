package com.aditya.f1hub.service.analytics.impl;

import com.aditya.f1hub.dto.analytics.RaceAnalyticsDriverDto;
import com.aditya.f1hub.dto.analytics.RaceAnalyticsPodiumDto;
import com.aditya.f1hub.dto.analytics.RaceAnalyticsResponseDto;
import com.aditya.f1hub.dto.analytics.RaceAnalyticsStatisticsDto;
import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.RaceResult;
import com.aditya.f1hub.repository.RaceRepository;
import com.aditya.f1hub.repository.RaceResultRepository;
import com.aditya.f1hub.service.analytics.RaceAnalyticsService;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RaceAnalyticsServiceImpl implements RaceAnalyticsService {

    private final RaceRepository raceRepository;
    private final RaceResultRepository raceResultRepository;

    @Override
    public RaceAnalyticsResponseDto getRaceAnalytics(Long raceId) {

        Race race = raceRepository.findById(raceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Race",
                                "id",
                                raceId
                        )
                );

        List<RaceResult> results =
                raceResultRepository.findRaceResultsByRaceId(raceId);

        RaceAnalyticsStatisticsDto statistics =
                buildStatistics(results);

        List<RaceAnalyticsPodiumDto> podium =
                buildPodium(results);

        RaceAnalyticsDriverDto fastestLap =
                buildFastestLap(results);

        List<RaceAnalyticsDriverDto> driverPerformances =
                results.stream()
                        .map(this::buildDriverPerformance)
                        .toList();

        return RaceAnalyticsResponseDto.builder()
                .raceId(race.getId())
                .raceName(race.getName())
                .seasonId(race.getSeason().getId())
                .seasonYear(race.getSeason().getYear())
                .roundNumber(race.getRoundNumber())
                .statistics(statistics)
                .podium(podium)
                .fastestLap(fastestLap)
                .driverPerformances(driverPerformances)
                .build();
    }

    private RaceAnalyticsStatisticsDto buildStatistics(
            List<RaceResult> results
    ) {
        int totalDrivers = results.size();

        int dnfs = (int) results.stream()
                .filter(result -> Boolean.TRUE.equals(result.getDnf()))
                .count();

        int dns = (int) results.stream()
                .filter(result -> Boolean.TRUE.equals(result.getDns()))
                .count();

        int dsqs = (int) results.stream()
                .filter(result -> Boolean.TRUE.equals(result.getDsq()))
                .count();

        int finishers = (int) results.stream()
                .filter(result ->
                        !Boolean.TRUE.equals(result.getDnf())
                                && !Boolean.TRUE.equals(result.getDns())
                                && !Boolean.TRUE.equals(result.getDsq())
                )
                .count();

        int pointsScorers = (int) results.stream()
                .filter(result ->
                        result.getPoints() != null
                                && result.getPoints() > 0
                )
                .count();

        double totalPoints = results.stream()
                .map(RaceResult::getPoints)
                .filter(points -> points != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        return RaceAnalyticsStatisticsDto.builder()
                .totalDrivers(totalDrivers)
                .finishers(finishers)
                .dnfs(dnfs)
                .dns(dns)
                .dsqs(dsqs)
                .pointsScorers(pointsScorers)
                .totalPoints(totalPoints)
                .build();
    }

    private List<RaceAnalyticsPodiumDto> buildPodium(
            List<RaceResult> results
    ) {
        return results.stream()
                .filter(result -> result.getPosition() != null)
                .filter(result -> result.getPosition() <= 3)
                .sorted(Comparator.comparing(RaceResult::getPosition))
                .map(result ->
                        RaceAnalyticsPodiumDto.builder()
                                .position(result.getPosition())
                                .driverId(result.getDriver().getId())
                                .driverName(result.getDriver().getFullName())
                                .constructorId(result.getConstructor().getId())
                                .constructorName(result.getConstructor().getName())
                                .points(result.getPoints())
                                .build()
                )
                .toList();
    }

    private RaceAnalyticsDriverDto buildFastestLap(
            List<RaceResult> results
    ) {
        return results.stream()
                .filter(result ->
                        result.getFastestLapTimeSeconds() != null
                )
                .min(Comparator.comparing(
                        RaceResult::getFastestLapTimeSeconds
                ))
                .map(this::buildDriverPerformance)
                .orElse(null);
    }

    private RaceAnalyticsDriverDto buildDriverPerformance(
            RaceResult result
    ) {
        Integer positionChange = calculatePositionChange(result);

        return RaceAnalyticsDriverDto.builder()
                .driverId(result.getDriver().getId())
                .driverName(result.getDriver().getFullName())
                .constructorId(result.getConstructor().getId())
                .constructorName(result.getConstructor().getName())
                .gridPosition(result.getGridPosition())
                .finishPosition(result.getPosition())
                .positionChange(positionChange)
                .points(result.getPoints())
                .status(determineStatus(result))
                .lapsCompleted(result.getNumberOfLaps())
                .durationSeconds(result.getDurationSeconds())
                .gapToLeaderSeconds(result.getGapToLeaderSeconds())
                .gapToLeaderText(result.getGapToLeaderText())
                .fastestLapTimeSeconds(result.getFastestLapTimeSeconds())
                .fastestLapNumber(result.getFastestLapNumber())
                .build();
    }

    private Integer calculatePositionChange(RaceResult result) {

        Integer gridPosition = result.getGridPosition();
        Integer finishPosition = result.getPosition();

        if (gridPosition == null || finishPosition == null) {
            return null;
        }

        return gridPosition - finishPosition;
    }

    private String determineStatus(RaceResult result) {

        if (Boolean.TRUE.equals(result.getDsq())) {
            return "DSQ";
        }

        if (Boolean.TRUE.equals(result.getDns())) {
            return "DNS";
        }

        if (Boolean.TRUE.equals(result.getDnf())) {
            return "DNF";
        }

        return "FINISHED";
    }
}