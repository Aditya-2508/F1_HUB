package com.aditya.f1hub.repository;

public interface DriverCareerStatisticsProjection {

    Long getRaceWins();

    Long getRacePodiums();

    Long getQualifyingPoles();

    Double getChampionshipPoints();
}