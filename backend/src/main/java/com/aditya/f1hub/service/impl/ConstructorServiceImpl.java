package com.aditya.f1hub.service.impl;

import com.aditya.f1hub.dto.common.PageResponse;
import com.aditya.f1hub.dto.constructor.ConstructorRequestDto;
import com.aditya.f1hub.dto.constructor.ConstructorResponseDto;
import com.aditya.f1hub.entity.Constructor;
import com.aditya.f1hub.exception.ResourceAlreadyExistsException;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.mapper.ConstructorMapper;
import com.aditya.f1hub.repository.ConstructorRepository;
import com.aditya.f1hub.service.ConstructorService;
import com.aditya.f1hub.specification.ConstructorSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConstructorServiceImpl implements ConstructorService {

    private final ConstructorRepository constructorRepository;

    private final ConstructorMapper constructorMapper;

    @Override
    public ConstructorResponseDto createConstructor(ConstructorRequestDto requestDto) {

        if (constructorRepository.existsByExternalConstructorId(
                requestDto.getExternalConstructorId())) {

            throw new ResourceAlreadyExistsException(
                    "Constructor",
                    "externalConstructorId",
                    requestDto.getExternalConstructorId()
            );
        }

        Constructor constructor =
                constructorMapper.toEntity(requestDto);

        Constructor savedConstructor =
                constructorRepository.save(constructor);

        return constructorMapper.toResponseDto(savedConstructor);
    }

    @Override
    public List<ConstructorResponseDto> getAllConstructors() {

        return constructorRepository.findAll()
                .stream()
                .map(constructorMapper::toResponseDto)
                .toList();
    }

    @Override
    public ConstructorResponseDto getConstructorById(Long id) {

        Constructor constructor = constructorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Constructor",
                                "id",
                                id
                        ));

        return constructorMapper.toResponseDto(constructor);
    }

    @Override
    public ConstructorResponseDto updateConstructor(
            Long id,
            ConstructorRequestDto requestDto) {

        Constructor constructor = constructorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Constructor",
                                "id",
                                id
                        ));

        if (!constructor.getExternalConstructorId()
                .equals(requestDto.getExternalConstructorId())
                && constructorRepository.existsByExternalConstructorId(
                requestDto.getExternalConstructorId())) {

            throw new ResourceAlreadyExistsException(
                    "Constructor",
                    "externalConstructorId",
                    requestDto.getExternalConstructorId()
            );
        }

        constructorMapper.updateEntityFromDto(
                requestDto,
                constructor
        );

        Constructor updatedConstructor =
                constructorRepository.save(constructor);

        return constructorMapper.toResponseDto(updatedConstructor);
    }

    @Override
    public void deleteConstructor(Long id) {

        Constructor constructor = constructorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Constructor",
                                "id",
                                id
                        ));

        constructorRepository.delete(constructor);
    }

    @Override
    public PageResponse<ConstructorResponseDto> searchConstructors(
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

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Specification<Constructor> specification =
                Specification.allOf(
                        ConstructorSpecification.hasName(name),
                        ConstructorSpecification.hasNationality(nationality),
                        ConstructorSpecification.isActive(active)
                );

        Page<Constructor> constructorPage =
                constructorRepository.findAll(
                        specification,
                        pageable
                );

        return PageResponse.<ConstructorResponseDto>builder()
                .content(
                        constructorPage.getContent()
                                .stream()
                                .map(constructorMapper::toResponseDto)
                                .toList()
                )
                .page(constructorPage.getNumber())
                .size(constructorPage.getSize())
                .totalElements(constructorPage.getTotalElements())
                .totalPages(constructorPage.getTotalPages())
                .first(constructorPage.isFirst())
                .last(constructorPage.isLast())
                .build();
    }

}