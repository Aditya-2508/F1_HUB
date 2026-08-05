package com.aditya.f1hub.service.impl;

import com.aditya.f1hub.dto.circuit.CircuitRequestDto;
import com.aditya.f1hub.dto.circuit.CircuitResponseDto;
import com.aditya.f1hub.entity.Circuit;
import com.aditya.f1hub.exception.ResourceAlreadyExistsException;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.mapper.CircuitMapper;
import com.aditya.f1hub.repository.CircuitRepository;
import com.aditya.f1hub.service.CircuitService;
import com.aditya.f1hub.specification.CircuitSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CircuitServiceImpl implements CircuitService {

    private final CircuitRepository circuitRepository;
    private final CircuitMapper circuitMapper;

    @Override
    @Transactional
    public CircuitResponseDto createCircuit(CircuitRequestDto requestDto) {

        if (circuitRepository.existsByExternalCircuitId(
                requestDto.getExternalCircuitId())) {

            throw new ResourceAlreadyExistsException(
                    "Circuit",
                    "externalCircuitId",
                    requestDto.getExternalCircuitId()
            );
        }

        Circuit circuit = circuitMapper.toEntity(requestDto);

        Circuit savedCircuit = circuitRepository.save(circuit);

        log.info("Circuit created successfully: {}",
                savedCircuit.getCircuitName());

        return circuitMapper.toResponseDto(savedCircuit);
    }

    @Override
    public List<CircuitResponseDto> getAllCircuits() {

        return circuitRepository.findAll()
                .stream()
                .map(circuitMapper::toResponseDto)
                .toList();
    }

    @Override
    public CircuitResponseDto getCircuitById(Long id) {

        Circuit circuit = circuitRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Circuit",
                                "id",
                                id
                        ));

        return circuitMapper.toResponseDto(circuit);
    }

    @Override
    @Transactional
    public CircuitResponseDto updateCircuit(
            Long id,
            CircuitRequestDto requestDto) {

        Circuit circuit = circuitRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Circuit",
                                "id",
                                id
                        ));

        circuitMapper.updateEntityFromDto(requestDto, circuit);

        Circuit updatedCircuit = circuitRepository.save(circuit);

        log.info("Circuit updated successfully: {}",
                updatedCircuit.getCircuitName());

        return circuitMapper.toResponseDto(updatedCircuit);
    }

    @Override
    @Transactional
    public void deleteCircuit(Long id) {

        Circuit circuit = circuitRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Circuit",
                                "id",
                                id
                        ));

        circuitRepository.delete(circuit);

        log.info("Circuit deleted successfully: {}",
                circuit.getCircuitName());
    }

    @Override
    public List<CircuitResponseDto> searchCircuits(
            String circuitName,
            String country,
            Boolean active) {

        Specification<Circuit> specification =
                Specification.where(
                                CircuitSpecification.hasCircuitName(circuitName))
                        .and(CircuitSpecification.hasCountry(country))
                        .and(CircuitSpecification.isActive(active));

        return circuitRepository.findAll(specification)
                .stream()
                .map(circuitMapper::toResponseDto)
                .toList();
    }
}