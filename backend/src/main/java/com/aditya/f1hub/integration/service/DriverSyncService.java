package com.aditya.f1hub.integration.service;

import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.integration.client.OpenF1Client;
import com.aditya.f1hub.integration.dto.DriverSyncResponseDto;
import com.aditya.f1hub.integration.dto.OpenF1DriverDto;
import com.aditya.f1hub.integration.mapper.OpenF1DriverMapper;
import com.aditya.f1hub.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverSyncService {

    private final OpenF1Client openF1Client;

    private final OpenF1DriverMapper mapper;

    private final DriverRepository driverRepository;

    public DriverSyncResponseDto synchronizeDrivers() {

        List<OpenF1DriverDto> drivers =
                openF1Client.getDrivers();

        int inserted = 0;

        int existing = 0;

        int failed = 0;

        for (OpenF1DriverDto dto : drivers) {

            try {

                Driver driver = mapper.toEntity(dto);

                if (driverRepository.existsByExternalDriverId(
                        driver.getExternalDriverId())) {

                    existing++;

                    continue;
                }

                driverRepository.save(driver);

                inserted++;

            } catch (Exception exception) {

                failed++;

            }

        }

        return DriverSyncResponseDto.builder()
                .totalFetched(drivers.size())
                .newDrivers(inserted)
                .existingDrivers(existing)
                .failedDrivers(failed)
                .build();
    }

}