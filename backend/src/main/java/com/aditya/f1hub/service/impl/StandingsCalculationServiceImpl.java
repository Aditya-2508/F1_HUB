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

            data.recordPosition(result.getPosition());
        }

        List<DriverStandingData> entries =
                new ArrayList<>(standings.values());

        entries.sort(
                Comparator
                        .comparingDouble(
                                DriverStandingData::points
                        )
                        .reversed()
                        .thenComparing(
                                Comparator.comparingInt(
                                        DriverStandingData::wins
                                ).reversed()
                        )
        );

        return new DriverAggregation(entries);
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

            data.recordPosition(result.getPosition());
        }

        List<ConstructorStandingData> entries =
                new ArrayList<>(standings.values());

        entries.sort(
                Comparator
                        .comparingDouble(
                                ConstructorStandingData::points
                        )
                        .reversed()
                        .thenComparing(
                                Comparator.comparingInt(
                                        ConstructorStandingData::wins
                                ).reversed()
                        )
        );

        return new ConstructorAggregation(entries);
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

        private DriverStandingData(Driver driver) {
            this.driver = driver;
        }

        private void addPoints(double points) {
            this.points += points;
        }

        private void incrementWins() {
            this.wins++;
        }

        private void recordPosition(Integer position) {
            // Reserved for championship countback support.
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
    }

    private static class ConstructorStandingData {

        private final Constructor constructor;
        private double points;
        private int wins;

        private ConstructorStandingData(
                Constructor constructor) {

            this.constructor = constructor;
        }

        private void addPoints(double points) {
            this.points += points;
        }

        private void incrementWins() {
            this.wins++;
        }

        private void recordPosition(Integer position) {
            // Reserved for championship countback support.
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
    }
}