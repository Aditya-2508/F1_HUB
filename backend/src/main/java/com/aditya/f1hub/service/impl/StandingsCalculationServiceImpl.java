package com.aditya.f1hub.service.impl;

import com.aditya.f1hub.entity.Constructor;
import com.aditya.f1hub.entity.ConstructorStanding;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.entity.DriverStanding;
import com.aditya.f1hub.entity.RaceResult;
import com.aditya.f1hub.entity.Season;
import com.aditya.f1hub.repository.ConstructorStandingRepository;
import com.aditya.f1hub.repository.DriverStandingRepository;
import com.aditya.f1hub.repository.RaceResultRepository;
import com.aditya.f1hub.repository.SeasonRepository;
import com.aditya.f1hub.service.PointsCalculationService;
import com.aditya.f1hub.service.StandingsCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StandingsCalculationServiceImpl
        implements StandingsCalculationService {

    private final SeasonRepository seasonRepository;
    private final RaceResultRepository raceResultRepository;
    private final DriverStandingRepository driverStandingRepository;
    private final ConstructorStandingRepository constructorStandingRepository;
    private final PointsCalculationService pointsCalculationService;

    @Override
    @Transactional
    public void calculateStandings(Long seasonId) {

        log.info(
                "Starting standings calculation for seasonId={}",
                seasonId
        );

        Season season = seasonRepository
                .findById(seasonId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Season not found: " + seasonId
                        )
                );

        List<RaceResult> results =
                raceResultRepository.findAllBySeasonId(seasonId);

        log.info(
                "Loaded {} race results for seasonId={}",
                results.size(),
                seasonId
        );

        DriverAggregation driverAggregation =
                aggregateDrivers(results);

        ConstructorAggregation constructorAggregation =
                aggregateConstructors(results);

        replaceDriverStandings(
                season,
                driverAggregation
        );

        replaceConstructorStandings(
                season,
                constructorAggregation
        );

        log.info(
                "Standings calculation completed successfully: "
                        + "seasonId={}, drivers={}, constructors={}",
                seasonId,
                driverAggregation.entries().size(),
                constructorAggregation.entries().size()
        );
    }

    private DriverAggregation aggregateDrivers(
            List<RaceResult> results) {

        Map<Long, DriverStandingData> standings =
                new HashMap<>();

        for (RaceResult result : results) {

            if (!pointsCalculationService
                    .isChampionshipResult(result)) {
                continue;
            }

            Driver driver = result.getDriver();

            if (driver == null || driver.getId() == null) {
                continue;
            }

            double points =
                    pointsCalculationService
                            .calculatePoints(result);

            boolean raceWin =
                    pointsCalculationService
                            .isRaceWin(result);

            DriverStandingData data =
                    standings.computeIfAbsent(
                            driver.getId(),
                            id -> new DriverStandingData(driver)
                    );

            data.addPoints(points);

            if (raceWin) {
                data.incrementWins();
            }

            data.recordPosition(
                    result.getPosition(),
                    result.getSession() != null
                            ? result.getSession().getSessionType()
                            : null
            );
        }

        List<DriverStandingData> entries =
                new ArrayList<>(standings.values());

        entries.sort(
                this::compareDrivers
        );

        return new DriverAggregation(entries);
    }

    private int compareDrivers(
            DriverStandingData first,
            DriverStandingData second
    ) {

        int comparison =
                Double.compare(
                        second.points(),
                        first.points()
                );

        if (comparison != 0) {
            return comparison;
        }

        comparison =
                Integer.compare(
                        second.wins(),
                        first.wins()
                );

        if (comparison != 0) {
            return comparison;
        }

        int maxRacePosition =
                Math.max(
                        first.racePositionCounts.keySet()
                                .stream()
                                .mapToInt(Integer::intValue)
                                .max()
                                .orElse(0),
                        second.racePositionCounts.keySet()
                                .stream()
                                .mapToInt(Integer::intValue)
                                .max()
                                .orElse(0)
                );

        for (int position = 2;
             position <= maxRacePosition;
             position++) {

            comparison =
                    Integer.compare(
                            second.raceFinishesAt(position),
                            first.raceFinishesAt(position)
                    );

            if (comparison != 0) {
                return comparison;
            }
        }

        int maxQualifyingPosition =
                Math.max(
                        first.qualifyingPositionCounts.keySet()
                                .stream()
                                .mapToInt(Integer::intValue)
                                .max()
                                .orElse(0),
                        second.qualifyingPositionCounts.keySet()
                                .stream()
                                .mapToInt(Integer::intValue)
                                .max()
                                .orElse(0)
                );

        for (int position = 1;
             position <= maxQualifyingPosition;
             position++) {

            comparison =
                    Integer.compare(
                            second.qualifyingFinishesAt(position),
                            first.qualifyingFinishesAt(position)
                    );

            if (comparison != 0) {
                return comparison;
            }
        }

        return Long.compare(
                first.driver().getId(),
                second.driver().getId()
        );
    }

    private ConstructorAggregation aggregateConstructors(
            List<RaceResult> results) {

        Map<Long, ConstructorStandingData> standings =
                new HashMap<>();

        for (RaceResult result : results) {

            if (!pointsCalculationService
                    .isChampionshipResult(result)) {
                continue;
            }

            Constructor constructor =
                    result.getConstructor();

            if (constructor == null
                    || constructor.getId() == null) {
                continue;
            }

            double points =
                    pointsCalculationService
                            .calculatePoints(result);

            boolean raceWin =
                    pointsCalculationService
                            .isRaceWin(result);

            ConstructorStandingData data =
                    standings.computeIfAbsent(
                            constructor.getId(),
                            id -> new ConstructorStandingData(
                                    constructor
                            )
                    );

            data.addPoints(points);

            if (raceWin) {
                data.incrementWins();
            }

            data.recordPosition(
                    result.getPosition(),
                    result.getSession() != null
                            ? result.getSession().getSessionType()
                            : null
            );
        }

        List<ConstructorStandingData> entries =
                new ArrayList<>(standings.values());

        entries.sort(
                this::compareConstructors
        );

        return new ConstructorAggregation(entries);
    }

    private int compareConstructors(
            ConstructorStandingData first,
            ConstructorStandingData second
    ) {

        int comparison =
                Double.compare(
                        second.points(),
                        first.points()
                );

        if (comparison != 0) {
            return comparison;
        }

        comparison =
                Integer.compare(
                        second.wins(),
                        first.wins()
                );

        if (comparison != 0) {
            return comparison;
        }

        int maxRacePosition =
                Math.max(
                        first.racePositionCounts.keySet()
                                .stream()
                                .mapToInt(Integer::intValue)
                                .max()
                                .orElse(0),
                        second.racePositionCounts.keySet()
                                .stream()
                                .mapToInt(Integer::intValue)
                                .max()
                                .orElse(0)
                );

        for (int position = 2;
             position <= maxRacePosition;
             position++) {

            comparison =
                    Integer.compare(
                            second.raceFinishesAt(position),
                            first.raceFinishesAt(position)
                    );

            if (comparison != 0) {
                return comparison;
            }
        }

        return Long.compare(
                first.constructor().getId(),
                second.constructor().getId()
        );
    }

    private void replaceDriverStandings(
            Season season,
            DriverAggregation aggregation) {

        driverStandingRepository.deleteBySeasonId(
                season.getId()
        );

        List<DriverStanding> standings =
                new ArrayList<>();

        int position = 1;

        for (DriverStandingData data :
                aggregation.entries()) {

            DriverStanding standing =
                    DriverStanding.builder()
                            .season(season)
                            .driver(data.driver())
                            .position(position++)
                            .points(data.points())
                            .wins(data.wins())
                            .build();

            standings.add(standing);
        }

        driverStandingRepository.saveAll(standings);
    }

    private void replaceConstructorStandings(
            Season season,
            ConstructorAggregation aggregation) {

        constructorStandingRepository.deleteBySeasonId(
                season.getId()
        );

        List<ConstructorStanding> standings =
                new ArrayList<>();

        int position = 1;

        for (ConstructorStandingData data :
                aggregation.entries()) {

            ConstructorStanding standing =
                    ConstructorStanding.builder()
                            .season(season)
                            .constructor(data.constructor())
                            .position(position++)
                            .points(data.points())
                            .wins(data.wins())
                            .build();

            standings.add(standing);
        }

        constructorStandingRepository.saveAll(standings);
    }

    private record DriverAggregation(
            List<DriverStandingData> entries
    ) {
    }

    private record ConstructorAggregation(
            List<ConstructorStandingData> entries
    ) {
    }

    private static class DriverStandingData {

        private final Driver driver;

        private double points;

        private int wins;

        private final Map<Integer, Integer> racePositionCounts =
                new HashMap<>();

        private final Map<Integer, Integer> qualifyingPositionCounts =
                new HashMap<>();

        private DriverStandingData(Driver driver) {
            this.driver = driver;
        }

        private void addPoints(double points) {
            this.points += points;
        }

        private void incrementWins() {
            this.wins++;
        }

        private void recordPosition(
                Integer position,
                String sessionType
        ) {

            if (position == null || position <= 0) {
                return;
            }

            if (sessionType == null) {
                return;
            }

            String normalizedType =
                    sessionType.trim().toLowerCase();

            if ("race".equals(normalizedType)) {

                racePositionCounts.merge(
                        position,
                        1,
                        Integer::sum
                );

                return;
            }

            if ("qualifying".equals(normalizedType)) {

                qualifyingPositionCounts.merge(
                        position,
                        1,
                        Integer::sum
                );
            }
        }

        private Driver driver() {
            return driver;
        }

        private double points() {
            return points;
        }

        private int wins() {
            return wins;
        }

        private int raceFinishesAt(int position) {
            return racePositionCounts.getOrDefault(
                    position,
                    0
            );
        }

        private int qualifyingFinishesAt(int position) {
            return qualifyingPositionCounts.getOrDefault(
                    position,
                    0
            );
        }
    }

    private static class ConstructorStandingData {

        private final Constructor constructor;

        private double points;

        private int wins;

        private final Map<Integer, Integer> racePositionCounts =
                new HashMap<>();

        private ConstructorStandingData(
                Constructor constructor
        ) {
            this.constructor = constructor;
        }

        private void addPoints(double points) {
            this.points += points;
        }

        private void incrementWins() {
            this.wins++;
        }

        private void recordPosition(
                Integer position,
                String sessionType
        ) {

            if (position == null || position <= 0) {
                return;
            }

            if (sessionType == null) {
                return;
            }

            String normalizedType =
                    sessionType.trim().toLowerCase();

            if ("race".equals(normalizedType)) {

                racePositionCounts.merge(
                        position,
                        1,
                        Integer::sum
                );
            }
        }

        private Constructor constructor() {
            return constructor;
        }

        private double points() {
            return points;
        }

        private int wins() {
            return wins;
        }

        private int raceFinishesAt(int position) {
            return racePositionCounts.getOrDefault(
                    position,
                    0
            );
        }
    }
}