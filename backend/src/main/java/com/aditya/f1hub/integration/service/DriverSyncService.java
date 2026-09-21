package com.aditya.f1hub.integration.service;

import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.integration.client.OpenF1Client;
import com.aditya.f1hub.integration.dto.DriverSyncResponseDto;
import com.aditya.f1hub.integration.dto.OpenF1DriverDto;
import com.aditya.f1hub.integration.mapper.OpenF1DriverMapper;
import com.aditya.f1hub.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverSyncService {

    private final OpenF1Client openF1Client;
    private final OpenF1DriverMapper mapper;
    private final DriverRepository driverRepository;

    @Transactional
    public DriverSyncResponseDto synchronizeDrivers() {

        List<OpenF1DriverDto> drivers =
                openF1Client.getDrivers();

        int inserted = 0;
        int updated = 0;
        int failed = 0;

        for (OpenF1DriverDto dto : drivers) {

            try {

                Driver incomingDriver =
                        mapper.toEntity(dto);

                if (incomingDriver == null
                        || incomingDriver.getExternalDriverId() == null) {
                    failed++;
                    continue;
                }

                Optional<Driver> existingDriver =
                        driverRepository.findByExternalDriverId(
                                incomingDriver.getExternalDriverId()
                        );

                if (existingDriver.isPresent()) {

                    updateExistingDriver(
                            existingDriver.get(),
                            incomingDriver
                    );

                    driverRepository.save(
                            existingDriver.get()
                    );

                    updated++;

                } else {

                    driverRepository.save(
                            incomingDriver
                    );

                    inserted++;
                }

            } catch (Exception exception) {

                failed++;
            }
        }

        return DriverSyncResponseDto.builder()
                .totalFetched(drivers.size())
                .newDrivers(inserted)
                .existingDrivers(updated)
                .failedDrivers(failed)
                .build();
    }

    private void updateExistingDriver(
            Driver existingDriver,
            Driver incomingDriver
    ) {

        existingDriver.setDriverNumber(
                incomingDriver.getDriverNumber()
        );

        existingDriver.setFirstName(
                incomingDriver.getFirstName()
        );

        existingDriver.setLastName(
                incomingDriver.getLastName()
        );

        existingDriver.setFullName(
                incomingDriver.getFullName()
        );

        existingDriver.setAbbreviation(
                incomingDriver.getAbbreviation()
        );

        existingDriver.setNationality(
                incomingDriver.getNationality()
        );

        existingDriver.setProfileImageUrl(
                incomingDriver.getProfileImageUrl()
        );

        existingDriver.setActive(
                incomingDriver.getActive()
        );
    }
}