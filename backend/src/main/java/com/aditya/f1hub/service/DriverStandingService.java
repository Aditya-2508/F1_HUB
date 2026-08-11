package com.aditya.f1hub.service;

import com.aditya.f1hub.entity.DriverStanding;

import java.util.List;

public interface DriverStandingService {

    List<DriverStanding> getStandingsBySeason(Long seasonId);

    DriverStanding getStandingBySeasonAndDriver(
            Long seasonId,
            Long driverId
    );
}