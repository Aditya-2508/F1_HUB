package com.aditya.f1hub.integration.service;

import com.aditya.f1hub.entity.Constructor;
import com.aditya.f1hub.integration.client.OpenF1Client;
import com.aditya.f1hub.integration.dto.ConstructorSyncResponseDto;
import com.aditya.f1hub.integration.dto.OpenF1DriverDto;
import com.aditya.f1hub.integration.mapper.OpenF1ConstructorMapper;
import com.aditya.f1hub.repository.ConstructorRepository;
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

    public ConstructorSyncResponseDto synchronizeConstructors() {

        List<OpenF1DriverDto> drivers = openF1Client.getDrivers();

        Set<String> processedTeams = new HashSet<>();

        int inserted = 0;
        int existing = 0;
        int failed = 0;

        for (OpenF1DriverDto dto : drivers) {

            if (dto.getTeamName() == null || dto.getTeamName().isBlank()) {
                continue;
            }

            if (!processedTeams.add(dto.getTeamName())) {
                continue;
            }

            try {

                Constructor constructor = mapper.toEntity(dto);

                if (constructorRepository.existsByExternalConstructorId(
                        constructor.getExternalConstructorId())) {

                    existing++;
                    continue;
                }

                constructorRepository.save(constructor);

                inserted++;

            } catch (Exception ex) {

                failed++;

                log.error(
                        "Failed to synchronize constructor: {}",
                        dto.getTeamName(),
                        ex
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