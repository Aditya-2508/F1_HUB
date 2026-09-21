package com.aditya.f1hub.integration.service;

import com.aditya.f1hub.integration.dto.SeasonDataSyncResponseDto;

public interface SeasonDataSyncOrchestrator {

    SeasonDataSyncResponseDto synchronizeSeason(Integer year);
}