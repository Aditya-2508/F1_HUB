package com.aditya.f1hub.service;

public interface StandingsCalculationService {

    /**
     * Recalculates the complete driver and constructor
     * championship standings for a season.
     *
     * @param seasonId F1Hub season ID
     */
    void calculateStandings(Long seasonId);
}