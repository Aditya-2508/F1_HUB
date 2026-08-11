package com.aditya.f1hub.service;

import com.aditya.f1hub.dto.common.PageResponse;
import com.aditya.f1hub.dto.constructor.ConstructorRequestDto;
import com.aditya.f1hub.dto.constructor.ConstructorResponseDto;

import java.util.List;

public interface ConstructorService {

    ConstructorResponseDto createConstructor(ConstructorRequestDto requestDto);

    List<ConstructorResponseDto> getAllConstructors();

    ConstructorResponseDto getConstructorById(Long id);

    ConstructorResponseDto updateConstructor(Long id, ConstructorRequestDto requestDto);

    void deleteConstructor(Long id);

    PageResponse<ConstructorResponseDto> searchConstructors(
            String name,
            String nationality,
            Boolean active,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );

    //int synchronizeConstructors(Long sessionId);

}