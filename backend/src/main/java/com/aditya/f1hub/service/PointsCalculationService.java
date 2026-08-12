package com.aditya.f1hub.service;

import com.aditya.f1hub.entity.RaceResult;

public interface PointsCalculationService {

    /**
     * Calculates championship points for a race result
     * based on the associated session type and finishing position.
     *
     * @param raceResult race result to evaluate
     * @return championship points earned by the driver
     */
    double calculatePoints(RaceResult raceResult);

    /**
     * Determines whether the result belongs to a
     * championship-scoring session.
     *
     * @param raceResult race result to evaluate
     * @return true when the session contributes championship points
     */
    boolean isChampionshipResult(RaceResult raceResult);

    /**
     * Determines whether the result represents a
     * Grand Prix race victory.
     *
     * @param raceResult race result to evaluate
     * @return true when the driver finished first in a race
     */
    boolean isRaceWin(RaceResult raceResult);
}