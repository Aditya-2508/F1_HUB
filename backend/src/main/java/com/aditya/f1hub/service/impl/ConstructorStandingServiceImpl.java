package com.aditya.f1hub.service.impl;

import com.aditya.f1hub.entity.ConstructorStanding;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.repository.ConstructorStandingRepository;
import com.aditya.f1hub.service.ConstructorStandingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ConstructorStandingServiceImpl
        implements ConstructorStandingService {

    private final ConstructorStandingRepository constructorStandingRepository;

    @Override
    public List<ConstructorStanding> getStandingsBySeason(Long seasonId) {

        log.debug(
                "Fetching constructor standings for season ID: {}",
                seasonId
        );

        return constructorStandingRepository
                .findBySeasonIdOrderByPositionAsc(seasonId);
    }

    @Override
    public ConstructorStanding getStandingBySeasonAndConstructor(
            Long seasonId,
            Long constructorId
    ) {

        log.debug(
                "Fetching constructor standing for season ID: {} and constructor ID: {}",
                seasonId,
                constructorId
        );

        return constructorStandingRepository
                .findBySeasonIdAndConstructorId(
                        seasonId,
                        constructorId
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ConstructorStanding",
                        "seasonId + constructorId",
                        seasonId + ":" + constructorId
                ));
    }
}