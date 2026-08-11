package com.aditya.f1hub.service.impl;

import com.aditya.f1hub.entity.DriverStanding;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.repository.DriverStandingRepository;
import com.aditya.f1hub.service.DriverStandingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DriverStandingServiceImpl implements DriverStandingService {

    private final DriverStandingRepository driverStandingRepository;

    @Override
    public List<DriverStanding> getStandingsBySeason(Long seasonId) {

        log.debug(
                "Fetching driver standings for season ID: {}",
                seasonId
        );

        return driverStandingRepository
                .findBySeasonIdOrderByPositionAsc(seasonId);
    }

    @Override
    public DriverStanding getStandingBySeasonAndDriver(
            Long seasonId,
            Long driverId
    ) {

        log.debug(
                "Fetching driver standing for season ID: {} and driver ID: {}",
                seasonId,
                driverId
        );

        return driverStandingRepository
                .findBySeasonIdAndDriverId(seasonId, driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DriverStanding",
                        "seasonId + driverId",
                        seasonId + ":" + driverId
                ));
    }
}