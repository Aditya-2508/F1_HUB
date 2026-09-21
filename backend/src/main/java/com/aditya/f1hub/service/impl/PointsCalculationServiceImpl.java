package com.aditya.f1hub.service.impl;

import com.aditya.f1hub.entity.RaceResult;
import com.aditya.f1hub.entity.Session;
import com.aditya.f1hub.service.PointsCalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class PointsCalculationServiceImpl
        implements PointsCalculationService {

    private static final Map<Integer, Double> RACE_POINTS = Map.of(
            1, 25.0,
            2, 18.0,
            3, 15.0,
            4, 12.0,
            5, 10.0,
            6, 8.0,
            7, 6.0,
            8, 4.0,
            9, 2.0,
            10, 1.0
    );

    private static final Map<Integer, Double> SPRINT_POINTS = Map.of(
            1, 8.0,
            2, 7.0,
            3, 6.0,
            4, 5.0,
            5, 4.0,
            6, 3.0,
            7, 2.0,
            8, 1.0
    );

    @Override
    public double calculatePoints(RaceResult raceResult) {

        if (raceResult == null) {
            return 0.0;
        }

        if (!isChampionshipResult(raceResult)) {
            return 0.0;
        }

        if (Boolean.TRUE.equals(raceResult.getDns())) {
            return 0.0;
        }

        if (Boolean.TRUE.equals(raceResult.getDsq())) {
            return 0.0;
        }

        Integer position = raceResult.getPosition();

        if (position == null || position < 1) {
            return 0.0;
        }

        if (isSprintSession(raceResult)) {
            return SPRINT_POINTS.getOrDefault(
                    position,
                    0.0
            );
        }

        if (isRaceSession(raceResult)) {
            return RACE_POINTS.getOrDefault(
                    position,
                    0.0
            );
        }

        return 0.0;
    }

    @Override
    public boolean isChampionshipResult(RaceResult raceResult) {

        if (raceResult == null
                || raceResult.getSession() == null) {
            return false;
        }

        return isRaceSession(raceResult)
                || isSprintSession(raceResult);
    }

    @Override
    public boolean isRaceWin(RaceResult raceResult) {

        if (raceResult == null
                || raceResult.getSession() == null) {
            return false;
        }

        if (Boolean.TRUE.equals(raceResult.getDns())
                || Boolean.TRUE.equals(raceResult.getDsq())) {
            return false;
        }

        Integer position = raceResult.getPosition();

        if (position == null || position != 1) {
            return false;
        }

        /*
         * A Grand Prix win requires an actual Race session.
         *
         * A Sprint victory must never be counted as
         * a Grand Prix race win.
         */
        return isRaceSession(raceResult);
    }

    /**
     * Determines whether the supplied result belongs to
     * the main Grand Prix race.
     *
     * OpenF1 may represent the Sprint as:
     *
     * sessionType = "Race"
     * sessionName = "Sprint"
     *
     * Therefore sessionType alone is not sufficient.
     */
    private boolean isRaceSession(RaceResult raceResult) {

        Session session = raceResult.getSession();

        if (session == null) {
            return false;
        }

        String sessionType =
                normalizeSessionValue(
                        session.getSessionType()
                );

        String sessionName =
                normalizeSessionValue(
                        session.getSessionName()
                );

        return "race".equals(sessionType)
                && "race".equals(sessionName);
    }

    /**
     * Determines whether the supplied result belongs
     * to a Sprint session.
     */
    private boolean isSprintSession(RaceResult raceResult) {

        Session session = raceResult.getSession();

        if (session == null) {
            return false;
        }

        String sessionType =
                normalizeSessionValue(
                        session.getSessionType()
                );

        String sessionName =
                normalizeSessionValue(
                        session.getSessionName()
                );

        return "race".equals(sessionType)
                && "sprint".equals(sessionName);
    }

    private String normalizeSessionValue(String value) {

        if (value == null || value.isBlank()) {
            return "";
        }

        return value
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}