package com.aditya.f1hub.service;

import com.aditya.f1hub.entity.RaceResult;

import java.util.List;

public interface StandingsRankingService {

    /**
     * Sorts race results according to championship
     * countback rules for a single competitor.
     */
    List<RaceResult> sortForCountback(
            List<RaceResult> results
    );
}