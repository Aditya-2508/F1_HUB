package com.aditya.f1hub.service;

import com.aditya.f1hub.entity.ConstructorStanding;

import java.util.List;

public interface ConstructorStandingService {

    List<ConstructorStanding> getStandingsBySeason(Long seasonId);

    ConstructorStanding getStandingBySeasonAndConstructor(
            Long seasonId,
            Long constructorId
    );
}