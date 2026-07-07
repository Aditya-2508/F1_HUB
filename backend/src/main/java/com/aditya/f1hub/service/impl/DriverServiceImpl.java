package com.aditya.f1hub.service.impl;

import com.aditya.f1hub.dto.driver.DriverRequestDto;
import com.aditya.f1hub.dto.driver.DriverResponseDto;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.exception.ResourceAlreadyExistsException;
import com.aditya.f1hub.mapper.DriverMapper;
import com.aditya.f1hub.repository.DriverRepository;
import com.aditya.f1hub.service.DriverService;
import com.aditya.f1hub.specification.DriverSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import com.aditya.f1hub.exception.ResourceNotFoundException;

import com.aditya.f1hub.dto.common.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Override
    public DriverResponseDto createDriver(DriverRequestDto requestDto) {

        if (driverRepository.existsByExternalDriverId(requestDto.getExternalDriverId())) {
            throw new ResourceAlreadyExistsException(
                    "Driver",
                    "externalDriverId",
                    requestDto.getExternalDriverId()
            );
        }

        Driver driver = driverMapper.toEntity(requestDto);

        Driver savedDriver = driverRepository.save(driver);

        return driverMapper.toResponseDto(savedDriver);
    }

    @Override
    public List<DriverResponseDto> getAllDrivers() {

        return driverRepository.findAll()
                .stream()
                .map(driverMapper::toResponseDto)
                .toList();
    }

    @Override
    public DriverResponseDto getDriverById(Long id) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Driver", "id", id));

        return driverMapper.toResponseDto(driver);
    }

    @Override
    public DriverResponseDto updateDriver(Long id, DriverRequestDto requestDto) {

        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Driver", "id", id));

        if (!existingDriver.getExternalDriverId().equals(requestDto.getExternalDriverId())
                && driverRepository.existsByExternalDriverId(requestDto.getExternalDriverId())) {

            throw new ResourceAlreadyExistsException(
                    "Driver",
                    "externalDriverId",
                    requestDto.getExternalDriverId()
            );
        }

        driverMapper.updateEntityFromDto(requestDto, existingDriver);

        Driver updatedDriver = driverRepository.save(existingDriver);

        return driverMapper.toResponseDto(updatedDriver);
    }

    @Override
    public void deleteDriver(Long id) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Driver", "id", id));

        driverRepository.delete(driver);

    }

    @Override
    public PageResponse<DriverResponseDto> searchDrivers(
            String name,
            String nationality,
            Boolean active,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Driver> specification = Specification.allOf(
                DriverSpecification.hasName(name),
                DriverSpecification.hasNationality(nationality),
                DriverSpecification.isActive(active)
        );

        Page<Driver> driverPage =
                driverRepository.findAll(specification, pageable);

        return PageResponse.<DriverResponseDto>builder()
                .content(
                        driverPage.getContent()
                                .stream()
                                .map(driverMapper::toResponseDto)
                                .toList()
                )
                .page(driverPage.getNumber())
                .size(driverPage.getSize())
                .totalElements(driverPage.getTotalElements())
                .totalPages(driverPage.getTotalPages())
                .first(driverPage.isFirst())
                .last(driverPage.isLast())
                .build();
    }

}