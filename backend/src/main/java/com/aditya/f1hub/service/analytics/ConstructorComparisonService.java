package com.aditya.f1hub.service.analytics;

import com.aditya.f1hub.dto.analytics.ConstructorComparisonResponseDto;

import java.util.List;

public interface ConstructorComparisonService {

    List<ConstructorComparisonResponseDto> compareConstructors(
            List<Long> constructorIds
    );
}