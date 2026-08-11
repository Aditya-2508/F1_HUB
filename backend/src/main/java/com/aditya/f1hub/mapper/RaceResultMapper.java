package com.aditya.f1hub.mapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Locale;
import com.aditya.f1hub.dto.result.RaceResultResponse;
import com.aditya.f1hub.entity.Constructor;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.entity.RaceResult;
import com.aditya.f1hub.entity.Session;
import com.aditya.f1hub.integration.dto.OpenF1SessionResultDto;
import com.aditya.f1hub.integration.dto.OpenF1StartingGridDto;
import org.springframework.stereotype.Component;
import com.aditya.f1hub.integration.dto.OpenF1LapDto;

import java.util.Comparator;
import java.util.List;

@Component
public class RaceResultMapper {

    /**
     * Creates a new RaceResult entity from OpenF1 data.
     *
     * Relationships are supplied by the service layer because
     * resolving them requires database/business logic.
     */
    public RaceResult toEntity(
            OpenF1SessionResultDto resultDto,
            OpenF1StartingGridDto startingGridDto,
            Session session,
            Driver driver,
            Constructor constructor
    ) {

        RaceResult raceResult = new RaceResult();

        updateEntity(
                raceResult,
                resultDto,
                startingGridDto,
                session,
                driver,
                constructor
        );

        return raceResult;
    }

    /**
     * Updates an existing RaceResult entity with OpenF1 data.
     *
     * The existing entity instance is preserved so that its
     * database identity and audit fields remain intact.
     */
    public void updateEntity(
            RaceResult raceResult,
            OpenF1SessionResultDto resultDto,
            OpenF1StartingGridDto startingGridDto,
            Session session,
            Driver driver,
            Constructor constructor
    ) {

        raceResult.setSession(session);
        raceResult.setDriver(driver);
        raceResult.setConstructor(constructor);

        raceResult.setPosition(resultDto.getPosition());

        raceResult.setDnf(
                Boolean.TRUE.equals(resultDto.getDnf())
        );

        raceResult.setDns(
                Boolean.TRUE.equals(resultDto.getDns())
        );

        raceResult.setDsq(
                Boolean.TRUE.equals(resultDto.getDsq())
        );

        raceResult.setNumberOfLaps(
                resultDto.getNumberOfLaps()
        );

        if (startingGridDto != null) {
            raceResult.setGridPosition(
                    startingGridDto.getPosition()
            );
        }

        mapTimingData(
                raceResult,
                resultDto,
                session
        );
    }


    private void mapTimingData(
            RaceResult raceResult,
            OpenF1SessionResultDto resultDto,
            Session session
    ) {

        String sessionType = session.getSessionType();

        if (sessionType == null) {
            return;
        }

        String normalizedSessionType =
                sessionType.trim().toLowerCase(Locale.ROOT);

        if (normalizedSessionType.contains("qualifying")) {

            mapQualifyingTiming(
                    raceResult,
                    resultDto
            );

            return;
        }

        mapStandardTiming(
                raceResult,
                resultDto
        );
    }

    private void mapStandardTiming(
            RaceResult raceResult,
            OpenF1SessionResultDto resultDto
    ) {

        Double durationSeconds =
                extractDouble(resultDto.getDuration());

        if (durationSeconds != null) {
            raceResult.setDurationSeconds(
                    durationSeconds
            );
        }

        JsonNode gapNode =
                resultDto.getGapToLeader();

        if (gapNode == null || gapNode.isNull()) {
            return;
        }

        if (gapNode.isNumber()) {

            raceResult.setGapToLeaderSeconds(
                    gapNode.doubleValue()
            );

            raceResult.setGapToLeaderText(null);

            return;
        }

        if (gapNode.isTextual()) {

            raceResult.setGapToLeaderText(
                    gapNode.asText()
            );

            raceResult.setGapToLeaderSeconds(null);
        }
    }

    private void mapQualifyingTiming(
            RaceResult raceResult,
            OpenF1SessionResultDto resultDto
    ) {

        JsonNode durationNode =
                resultDto.getDuration();

        if (durationNode != null
                && durationNode.isArray()) {

            raceResult.setQ1TimeSeconds(
                    extractArrayDouble(durationNode, 0)
            );

            raceResult.setQ2TimeSeconds(
                    extractArrayDouble(durationNode, 1)
            );

            raceResult.setQ3TimeSeconds(
                    extractArrayDouble(durationNode, 2)
            );
        }

        JsonNode gapNode =
                resultDto.getGapToLeader();

        if (gapNode != null
                && gapNode.isArray()) {

            raceResult.setQ1GapToLeaderSeconds(
                    extractArrayDouble(gapNode, 0)
            );

            raceResult.setQ2GapToLeaderSeconds(
                    extractArrayDouble(gapNode, 1)
            );

            raceResult.setQ3GapToLeaderSeconds(
                    extractArrayDouble(gapNode, 2)
            );
        }
    }

    private Double extractDouble(JsonNode node) {

        if (node == null || node.isNull()) {
            return null;
        }

        if (node.isNumber()) {
            return node.doubleValue();
        }

        if (node.isTextual()) {

            try {
                return Double.parseDouble(
                        node.asText().trim()
                );
            } catch (NumberFormatException exception) {
                return null;
            }
        }

        return null;
    }

    private Double extractArrayDouble(
            JsonNode arrayNode,
            int index
    ) {

        if (arrayNode == null
                || !arrayNode.isArray()
                || index >= arrayNode.size()) {

            return null;
        }

        return extractDouble(
                arrayNode.get(index)
        );
    }


    /**
     * Maps a RaceResult entity into a response DTO.
     *
     * This keeps JPA entities away from the REST API layer.
     */
    public RaceResultResponse toResponse(
            RaceResult raceResult) {

        Session session = raceResult.getSession();
        Driver driver = raceResult.getDriver();
        Constructor constructor = raceResult.getConstructor();

        RaceResultResponse response =
                RaceResultResponse.builder()
                        .id(raceResult.getId())

                        .sessionId(session.getId())
                        .sessionName(session.getSessionName())
                        .sessionType(session.getSessionType())

                        .driverId(driver.getId())
                        .driverName(driver.getFullName())
                        .driverNumber(driver.getDriverNumber())

                        .constructorId(constructor.getId())
                        .constructorName(constructor.getName())

                        .position(raceResult.getPosition())
                        .gridPosition(raceResult.getGridPosition())
                        .points(raceResult.getPoints())

                        .dnf(raceResult.getDnf())
                        .dns(raceResult.getDns())
                        .dsq(raceResult.getDsq())

                        .durationSeconds(
                                raceResult.getDurationSeconds()
                        )

                        .gapToLeaderSeconds(
                                raceResult.getGapToLeaderSeconds()
                        )

                        .gapToLeaderText(
                                raceResult.getGapToLeaderText()
                        )

                        .numberOfLaps(
                                raceResult.getNumberOfLaps()
                        )

                        .fastestLapTimeSeconds(
                                raceResult
                                        .getFastestLapTimeSeconds()
                        )

                        .fastestLapNumber(
                                raceResult.getFastestLapNumber()
                        )

                        .q1TimeSeconds(
                                raceResult.getQ1TimeSeconds()
                        )
                        .q1GapToLeaderSeconds(
                                raceResult.getQ1GapToLeaderSeconds()
                        )

                        .q2TimeSeconds(
                                raceResult.getQ2TimeSeconds()
                        )
                        .q2GapToLeaderSeconds(
                                raceResult.getQ2GapToLeaderSeconds()
                        )

                        .q3TimeSeconds(
                                raceResult.getQ3TimeSeconds()
                        )
                        .q3GapToLeaderSeconds(
                                raceResult.getQ3GapToLeaderSeconds()
                        )

                        .active(raceResult.getActive())
                        .build();

        if (session.getRace() != null) {
            response.setRaceId(
                    session.getRace().getId()
            );

            response.setRaceName(
                    session.getRace().getName()
            );
        }

        return response;
    }

    public void mapFastestLap(
            RaceResult raceResult,
            List<OpenF1LapDto> lapDtos,
            Integer driverNumber
    ) {

        if (lapDtos == null
                || lapDtos.isEmpty()
                || driverNumber == null) {

            return;
        }

        OpenF1LapDto fastestLap = lapDtos.stream()
                .filter(dto ->
                        dto.getDriverNumber() != null
                                && dto.getDriverNumber()
                                .equals(driverNumber)
                )
                .filter(dto -> dto.getLapDuration() != null)
                .filter(dto -> dto.getLapNumber() != null)
                .min(
                        Comparator.comparing(
                                OpenF1LapDto::getLapDuration
                        )
                )
                .orElse(null);

        if (fastestLap == null) {
            return;
        }

        raceResult.setFastestLapTimeSeconds(
                fastestLap.getLapDuration()
        );

        raceResult.setFastestLapNumber(
                fastestLap.getLapNumber()
        );
    }
}