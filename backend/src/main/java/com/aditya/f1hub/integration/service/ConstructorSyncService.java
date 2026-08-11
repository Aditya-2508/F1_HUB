package com.aditya.f1hub.integration.service;

import com.aditya.f1hub.entity.Constructor;
import com.aditya.f1hub.entity.Session;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.integration.client.OpenF1Client;
import com.aditya.f1hub.integration.dto.ConstructorSyncResponseDto;
import com.aditya.f1hub.integration.dto.OpenF1DriverDto;
import com.aditya.f1hub.integration.mapper.OpenF1ConstructorMapper;
import com.aditya.f1hub.repository.ConstructorRepository;
import com.aditya.f1hub.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConstructorSyncService {

    private final OpenF1Client openF1Client;

    private final OpenF1ConstructorMapper mapper;

    private final ConstructorRepository constructorRepository;

    private final SessionRepository sessionRepository;

    /**
     * Synchronizes constructors using all available OpenF1 driver data.
     *
     * This preserves the existing constructor synchronization behavior.
     */
    public ConstructorSyncResponseDto synchronizeConstructors() {

        List<OpenF1DriverDto> drivers =
                openF1Client.getDrivers();

        return synchronizeFromDrivers(drivers);
    }

    /**
     * Synchronizes constructors for a specific F1Hub session.
     *
     * The supplied sessionId is the internal F1Hub Session ID.
     * It is resolved to the external OpenF1 session key before
     * calling the OpenF1 API.
     */
    public ConstructorSyncResponseDto synchronizeConstructors(
            Long sessionId) {

        Session session =
                sessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Session",
                                        "id",
                                        sessionId
                                ));

        Long externalSessionId;

        try {

            externalSessionId =
                    Long.valueOf(
                            session.getExternalSessionId()
                    );

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "Invalid external session ID: "
                            + session.getExternalSessionId()
            );
        }

        List<OpenF1DriverDto> drivers =
                openF1Client.getDrivers(externalSessionId);

        return synchronizeFromDrivers(drivers);
    }

    /**
     * Performs the common constructor synchronization logic.
     *
     * This method keeps the two synchronization entry points
     * consistent and avoids duplicate synchronization code.
     */
    private ConstructorSyncResponseDto synchronizeFromDrivers(
            List<OpenF1DriverDto> drivers) {

        Set<String> processedTeams = new HashSet<>();

        int inserted = 0;
        int existing = 0;
        int failed = 0;

        for (OpenF1DriverDto dto : drivers) {

            if (dto.getTeamName() == null
                    || dto.getTeamName().isBlank()) {

                continue;
            }

            String teamName =
                    dto.getTeamName().trim();

            if (!processedTeams.add(teamName)) {
                continue;
            }

            try {

                Constructor constructor =
                        mapper.toEntity(dto);

                if (constructorRepository
                        .existsByExternalConstructorId(
                                constructor
                                        .getExternalConstructorId())) {

                    existing++;
                    continue;
                }

                constructorRepository.save(constructor);

                inserted++;

                log.info(
                        "Constructor synchronized successfully: {}",
                        teamName
                );

            } catch (Exception exception) {

                failed++;

                log.error(
                        "Failed to synchronize constructor: {}",
                        teamName,
                        exception
                );
            }
        }

        return ConstructorSyncResponseDto.builder()
                .totalFetched(processedTeams.size())
                .newConstructors(inserted)
                .existingConstructors(existing)
                .failedConstructors(failed)
                .build();
    }
}