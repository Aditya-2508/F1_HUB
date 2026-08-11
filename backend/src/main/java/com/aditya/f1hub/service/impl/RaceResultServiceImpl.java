package com.aditya.f1hub.service.impl;

import com.aditya.f1hub.dto.result.RaceResultRequest;
import com.aditya.f1hub.dto.result.RaceResultResponse;
import com.aditya.f1hub.dto.result.RaceResultSyncResponse;
import com.aditya.f1hub.entity.Constructor;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.entity.RaceResult;
import com.aditya.f1hub.entity.Session;
import com.aditya.f1hub.exception.ResourceAlreadyExistsException;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.integration.client.OpenF1Client;
import com.aditya.f1hub.integration.dto.OpenF1DriverDto;
import com.aditya.f1hub.integration.dto.OpenF1LapDto;
import com.aditya.f1hub.integration.dto.OpenF1SessionResultDto;
import com.aditya.f1hub.integration.dto.OpenF1StartingGridDto;
import com.aditya.f1hub.integration.mapper.OpenF1ConstructorMapper;
import com.aditya.f1hub.mapper.RaceResultMapper;
import com.aditya.f1hub.repository.ConstructorRepository;
import com.aditya.f1hub.repository.DriverRepository;
import com.aditya.f1hub.repository.RaceResultRepository;
import com.aditya.f1hub.repository.SessionRepository;
import com.aditya.f1hub.service.RaceResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Locale;
import java.util.Optional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RaceResultServiceImpl implements RaceResultService {

    private final RaceResultRepository raceResultRepository;
    private final SessionRepository sessionRepository;
    private final DriverRepository driverRepository;
    private final ConstructorRepository constructorRepository;
    private final RaceResultMapper raceResultMapper;
    private final OpenF1Client openF1Client;
    private final OpenF1ConstructorMapper openF1ConstructorMapper;

    @Override
    @Transactional
    public RaceResultResponse createResult(
            RaceResultRequest request) {

        if (raceResultRepository
                .existsBySessionIdAndDriverId(
                        request.getSessionId(),
                        request.getDriverId())) {

            throw new ResourceAlreadyExistsException(
                    "RaceResult",
                    "sessionId + driverId",
                    request.getSessionId()
                            + " + "
                            + request.getDriverId()
            );
        }

        Session session = sessionRepository
                .findById(request.getSessionId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Session",
                                "id",
                                request.getSessionId()
                        ));

        Driver driver = driverRepository
                .findById(request.getDriverId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Driver",
                                "id",
                                request.getDriverId()
                        ));

        Constructor constructor = constructorRepository
                .findById(request.getConstructorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Constructor",
                                "id",
                                request.getConstructorId()
                        ));

        RaceResult result = buildEntity(
                request,
                session,
                driver,
                constructor
        );

        RaceResult savedResult =
                raceResultRepository.save(result);

        log.info(
                "Race result created successfully: sessionId={}, driverId={}",
                session.getId(),
                driver.getId()
        );

        return raceResultMapper.toResponse(savedResult);
    }

    @Override
    public RaceResultResponse getResultById(Long id) {

        RaceResult result = raceResultRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "RaceResult",
                                "id",
                                id
                        ));

        return raceResultMapper.toResponse(result);
    }

    @Override
    public List<RaceResultResponse> getAllResults() {

        return raceResultRepository.findAll()
                .stream()
                .map(raceResultMapper::toResponse)
                .toList();
    }

    @Override
    public List<RaceResultResponse> getResultsBySessionId(
            Long sessionId) {

        return raceResultRepository
                .findBySessionId(sessionId)
                .stream()
                .map(raceResultMapper::toResponse)
                .toList();
    }

    @Override
    public List<RaceResultResponse> getResultsByDriverId(
            Long driverId) {

        return raceResultRepository
                .findByDriverId(driverId)
                .stream()
                .map(raceResultMapper::toResponse)
                .toList();
    }

    @Override
    public List<RaceResultResponse> getResultsByConstructorId(
            Long constructorId) {

        return raceResultRepository
                .findByConstructorId(constructorId)
                .stream()
                .map(raceResultMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RaceResultResponse updateResult(
            Long id,
            RaceResultRequest request) {

        RaceResult result = raceResultRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "RaceResult",
                                "id",
                                id
                        ));

        if (!result.getSession().getId()
                .equals(request.getSessionId())
                || !result.getDriver().getId()
                .equals(request.getDriverId())) {

            if (raceResultRepository
                    .existsBySessionIdAndDriverId(
                            request.getSessionId(),
                            request.getDriverId())) {

                throw new ResourceAlreadyExistsException(
                        "RaceResult",
                        "sessionId + driverId",
                        request.getSessionId()
                                + " + "
                                + request.getDriverId()
                );
            }
        }

        Session session = sessionRepository
                .findById(request.getSessionId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Session",
                                "id",
                                request.getSessionId()
                        ));

        Driver driver = driverRepository
                .findById(request.getDriverId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Driver",
                                "id",
                                request.getDriverId()
                        ));

        Constructor constructor = constructorRepository
                .findById(request.getConstructorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Constructor",
                                "id",
                                request.getConstructorId()
                        ));

        updateEntity(
                result,
                request,
                session,
                driver,
                constructor
        );

        RaceResult updatedResult =
                raceResultRepository.save(result);

        log.info(
                "Race result updated successfully: id={}",
                id
        );

        return raceResultMapper.toResponse(updatedResult);
    }

    @Override
    @Transactional
    public void deleteResult(Long id) {

        RaceResult result = raceResultRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "RaceResult",
                                "id",
                                id
                        ));

        raceResultRepository.delete(result);

        log.info(
                "Race result deleted successfully: id={}",
                id
        );
    }

    @Override
    @Transactional
    public RaceResultSyncResponse synchronizeResults(Long sessionId) {

        Session session = sessionRepository
                .findById(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Session",
                                "id",
                                sessionId
                        ));

        Long sessionKey;

        try {
            sessionKey = Long.valueOf(
                    session.getExternalSessionId()
            );
        } catch (NumberFormatException exception) {

            throw new IllegalStateException(
                    "Invalid OpenF1 session key: "
                            + session.getExternalSessionId(),
                    exception
            );
        }

        List<OpenF1SessionResultDto> resultDtos =
                openF1Client.getSessionResults(sessionKey);

        List<OpenF1StartingGridDto> startingGridDtos =
                openF1Client.getStartingGrid(sessionKey);

        List<OpenF1DriverDto> driverDtos =
                openF1Client.getDrivers(sessionKey);

        List<OpenF1LapDto> lapDtos =
                openF1Client.getLaps(sessionKey);

        if (resultDtos == null || resultDtos.isEmpty()) {

            log.info(
                    "No OpenF1 results found for session: {}",
                    sessionKey
            );

            return RaceResultSyncResponse.builder()
                    .sessionId(sessionId)
                    .synchronizedCount(0)
                    .createdCount(0)
                    .updatedCount(0)
                    .build();
        }

        Map<Integer, OpenF1StartingGridDto> startingGridByDriver =
                startingGridDtos == null
                        ? Map.of()
                        : startingGridDtos.stream()
                        .filter(dto ->
                                dto.getDriverNumber() != null
                        )
                        .collect(
                                Collectors.toMap(
                                        OpenF1StartingGridDto::getDriverNumber,
                                        dto -> dto,
                                        (first, second) -> first
                                )
                        );

        Map<Integer, OpenF1DriverDto> driversByNumber =
                driverDtos == null
                        ? Map.of()
                        : driverDtos.stream()
                        .filter(dto ->
                                dto.getDriverNumber() != null
                        )
                        .collect(
                                Collectors.toMap(
                                        OpenF1DriverDto::getDriverNumber,
                                        dto -> dto,
                                        (first, second) -> first
                                )
                        );

        int createdCount = 0;
        int updatedCount = 0;

        for (OpenF1SessionResultDto resultDto : resultDtos) {

            Integer driverNumber =
                    resultDto.getDriverNumber();

            if (driverNumber == null) {

                log.warn(
                        "Skipping OpenF1 result without driver number. "
                                + "sessionKey={}",
                        sessionKey
                );

                continue;
            }

            OpenF1DriverDto driverDto =
                    driversByNumber.get(driverNumber);

            if (driverDto == null) {

                throw new ResourceNotFoundException(
                        "OpenF1 Driver",
                        "driverNumber",
                        driverNumber
                );
            }

            if (driverDto.getAbbreviation() == null
                    || driverDto.getAbbreviation().isBlank()) {

                throw new IllegalStateException(
                        "OpenF1 driver has no abbreviation: "
                                + driverNumber
                );
            }

            String externalDriverId =
                    driverDto.getAbbreviation()
                            .trim()
                            .toLowerCase(Locale.ROOT);

            Driver driver = driverRepository
                    .findByExternalDriverId(externalDriverId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Driver",
                                    "externalDriverId",
                                    externalDriverId
                            ));

            if (driverDto.getTeamName() == null
                    || driverDto.getTeamName().isBlank()) {

                throw new IllegalStateException(
                        "OpenF1 driver has no team name: "
                                + driverNumber
                );
            }

            String externalConstructorId =
                    openF1ConstructorMapper
                            .resolveExternalConstructorId(
                                    driverDto.getTeamName()
                            );

            Constructor constructor =
                    constructorRepository
                            .findByExternalConstructorId(
                                    externalConstructorId
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Constructor",
                                            "externalConstructorId",
                                            externalConstructorId
                                    ));

            OpenF1StartingGridDto startingGridDto =
                    startingGridByDriver.get(driverNumber);

            Optional<RaceResult> existingResult =
                    raceResultRepository
                            .findBySessionIdAndDriverId(
                                    session.getId(),
                                    driver.getId()
                            );
            RaceResult result;

            if (existingResult.isPresent()) {

                result = existingResult.get();

                updatedCount++;

            } else {

                result = new RaceResult();

                createdCount++;
            }

            raceResultMapper.updateEntity(
                    result,
                    resultDto,
                    startingGridDto,
                    session,
                    driver,
                    constructor
            );

            raceResultMapper.mapFastestLap(
                    result,
                    lapDtos,
                    driverNumber
            );

            raceResultRepository.save(result);
        }

        int synchronizedCount =
                createdCount + updatedCount;

        log.info(
                "Race results synchronized successfully: "
                        + "sessionId={}, sessionKey={}, "
                        + "created={}, updated={}, total={}",
                sessionId,
                sessionKey,
                createdCount,
                updatedCount,
                synchronizedCount
        );

        return RaceResultSyncResponse.builder()
                .sessionId(sessionId)
                .synchronizedCount(synchronizedCount)
                .createdCount(createdCount)
                .updatedCount(updatedCount)
                .build();
    }
    private RaceResult buildEntity(
            RaceResultRequest request,
            Session session,
            Driver driver,
            Constructor constructor) {

        RaceResult result = new RaceResult();

        result.setSession(session);
        result.setDriver(driver);
        result.setConstructor(constructor);

        applyRequestValues(result, request);

        return result;
    }

    private void updateEntity(
            RaceResult result,
            RaceResultRequest request,
            Session session,
            Driver driver,
            Constructor constructor) {

        result.setSession(session);
        result.setDriver(driver);
        result.setConstructor(constructor);

        applyRequestValues(result, request);
    }

    private void applyRequestValues(
            RaceResult result,
            RaceResultRequest request) {

        result.setPosition(
                request.getPosition()
        );

        result.setGridPosition(
                request.getGridPosition()
        );

        result.setPoints(
                request.getPoints()
        );

        result.setDnf(
                Boolean.TRUE.equals(request.getDnf())
        );

        result.setDns(
                Boolean.TRUE.equals(request.getDns())
        );

        result.setDsq(
                Boolean.TRUE.equals(request.getDsq())
        );

        result.setDurationSeconds(
                request.getDurationSeconds()
        );

        result.setGapToLeaderSeconds(
                request.getGapToLeaderSeconds()
        );

        result.setGapToLeaderText(
                request.getGapToLeaderText()
        );

        result.setNumberOfLaps(
                request.getNumberOfLaps()
        );

        result.setFastestLapTimeSeconds(
                request.getFastestLapTimeSeconds()
        );

        result.setFastestLapNumber(
                request.getFastestLapNumber()
        );

        result.setQ1TimeSeconds(
                request.getQ1TimeSeconds()
        );

        result.setQ1GapToLeaderSeconds(
                request.getQ1GapToLeaderSeconds()
        );

        result.setQ2TimeSeconds(
                request.getQ2TimeSeconds()
        );

        result.setQ2GapToLeaderSeconds(
                request.getQ2GapToLeaderSeconds()
        );

        result.setQ3TimeSeconds(
                request.getQ3TimeSeconds()
        );

        result.setQ3GapToLeaderSeconds(
                request.getQ3GapToLeaderSeconds()
        );

        if (request.getActive() != null) {
            result.setActive(
                    request.getActive()
            );
        }
    }
}