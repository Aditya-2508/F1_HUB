package com.aditya.f1hub.service.analytics;

import com.aditya.f1hub.dto.analytics.DriverComparisonResponseDto;

import java.util.List;

public interface DriverComparisonService {

    List<DriverComparisonResponseDto> compareDrivers(List<Long> driverIds);
}