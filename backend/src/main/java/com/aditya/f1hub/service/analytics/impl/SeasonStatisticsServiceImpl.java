package com.aditya.f1hub.service.analytics.impl;

import com.aditya.f1hub.dto.analytics.SeasonConstructorStatisticsDto;
import com.aditya.f1hub.dto.analytics.SeasonDriverStatisticsDto;
import com.aditya.f1hub.dto.analytics.SeasonStatisticsResponseDto;
import com.aditya.f1hub.entity.ConstructorStanding;
import com.aditya.f1hub.entity.DriverStanding;
import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.RaceResult;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.repository.ConstructorStandingRepository;
import com.aditya.f1hub.repository.DriverStandingRepository;
import com.aditya.f1hub.repository.RaceRepository;
import com.aditya.f1hub.repository.RaceResultRepository;
import com.aditya.f1hub.service.analytics.SeasonStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeasonStatisticsServiceImpl
        implements SeasonStatisticsService {

    private final RaceRepository raceRepository;
    private final RaceResultRepository raceResultRepository;
    private final DriverStandingRepository driverStandingRepository;
    private final ConstructorStandingRepository constructorStandingRepository;

    @Override
    public SeasonStatisticsResponseDto getSeasonStatistics(Long seasonId) {

        List<Race> races =
                raceRepository
                        .findBySeasonIdAndRoundNumberIsNotNullOrderByRoundNumberAsc(
                                seasonId
                        );

        if (races.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Race",
                    "seasonId",
                    seasonId
            );
        }

        List<DriverStanding> driverStandings =
                driverStandingRepository
                        .findBySeasonIdOrderByPositionAsc(seasonId);

        List<ConstructorStanding> constructorStandings =
                constructorStandingRepository
                        .findBySeasonIdOrderByPositionAsc(seasonId);

        List<RaceResult> seasonResults =
                raceResultRepository.findAllBySeasonId(seasonId);

        Set<Long> completedRaceIds = seasonResults.stream()
                .filter(this::isGrandPrixRaceResult)
                .filter(result -> result.getSession().getRace() != null)
                .map(result -> result.getSession().getRace().getId())
                .collect(Collectors.toSet());

        int completedRaces = completedRaceIds.size();

        int cancelledRaces = (int) races.stream()
                .filter(race ->
                        Boolean.TRUE.equals(race.getCancelled())
                )
                .count();

        double totalPoints = seasonResults.stream()
                .filter(this::isChampionshipSession)
                .map(RaceResult::getPoints)
                .filter(points -> points != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        Map<Long, Integer> driverPodiums =
                calculateDriverPodiums(seasonResults);

        Map<Long, Integer> driverDnfs =
                calculateDriverDnfs(seasonResults);

        Map<Long, Integer> constructorPodiums =
                calculateConstructorPodiums(seasonResults);

        List<SeasonDriverStatisticsDto> drivers =
                driverStandings.stream()
                        .map(standing ->
                                toDriverStatistics(
                                        standing,
                                        driverPodiums,
                                        driverDnfs
                                )
                        )
                        .toList();

        List<SeasonConstructorStatisticsDto> constructors =
                constructorStandings.stream()
                        .map(standing ->
                                toConstructorStatistics(
                                        standing,
                                        constructorPodiums
                                )
                        )
                        .toList();

        Integer seasonYear = driverStandings.isEmpty()
                ? null
                : driverStandings.get(0)
                .getSeason()
                .getYear();

        return SeasonStatisticsResponseDto.builder()
                .seasonId(seasonId)
                .seasonYear(seasonYear)
                .totalRaces(races.size())
                .completedRaces(completedRaces)
                .cancelledRaces(cancelledRaces)
                .totalDrivers(driverStandings.size())
                .totalConstructors(constructorStandings.size())
                .totalPoints(totalPoints)
                .driverStatistics(drivers)
                .constructorStatistics(constructors)
                .build();
    }

    private boolean isChampionshipSession(RaceResult result) {

        if (result.getSession() == null
                || result.getSession().getSessionType() == null) {
            return false;
        }

        String sessionType =
                result.getSession().getSessionType();

        return "race".equalsIgnoreCase(sessionType)
                || "sprint".equalsIgnoreCase(sessionType);
    }

    private boolean isGrandPrixRaceResult(RaceResult result) {

        return result.getSession() != null
                && "race".equalsIgnoreCase(
                result.getSession().getSessionType()
        )
                && "race".equalsIgnoreCase(
                result.getSession().getSessionName()
        );
    }

    private Map<Long, Integer> calculateDriverPodiums(
            List<RaceResult> results
    ) {
        return results.stream()
                .filter(this::isGrandPrixRaceResult)
                .filter(result -> result.getPosition() != null)
                .filter(result -> result.getPosition() <= 3)
                .collect(Collectors.groupingBy(
                        result -> result.getDriver().getId(),
                        Collectors.summingInt(result -> 1)
                ));
    }

    private Map<Long, Integer> calculateDriverDnfs(
            List<RaceResult> results
    ) {
        return results.stream()
                .filter(this::isGrandPrixRaceResult)
                .filter(result -> Boolean.TRUE.equals(result.getDnf()))
                .collect(Collectors.groupingBy(
                        result -> result.getDriver().getId(),
                        Collectors.summingInt(result -> 1)
                ));
    }

    private Map<Long, Integer> calculateConstructorPodiums(
            List<RaceResult> results
    ) {
        return results.stream()
                .filter(this::isGrandPrixRaceResult)
                .filter(result -> result.getPosition() != null)
                .filter(result -> result.getPosition() <= 3)
                .collect(Collectors.groupingBy(
                        result -> result.getConstructor().getId(),
                        Collectors.summingInt(result -> 1)
                ));
    }

    private SeasonDriverStatisticsDto toDriverStatistics(
            DriverStanding standing,
            Map<Long, Integer> podiums,
            Map<Long, Integer> dnfs
    ) {
        Long driverId = standing.getDriver().getId();

        return SeasonDriverStatisticsDto.builder()
                .driverId(driverId)
                .driverName(standing.getDriver().getFullName())
                .position(standing.getPosition())
                .points(standing.getPoints())
                .wins(standing.getWins())
                .podiums(podiums.getOrDefault(driverId, 0))
                .dnfs(dnfs.getOrDefault(driverId, 0))
                .build();
    }

    private SeasonConstructorStatisticsDto toConstructorStatistics(
            ConstructorStanding standing,
            Map<Long, Integer> podiums
    ) {
        Long constructorId = standing.getConstructor().getId();

        return SeasonConstructorStatisticsDto.builder()
                .constructorId(constructorId)
                .constructorName(standing.getConstructor().getName())
                .position(standing.getPosition())
                .points(standing.getPoints())
                .wins(standing.getWins())
                .podiums(podiums.getOrDefault(constructorId, 0))
                .build();
    }
}