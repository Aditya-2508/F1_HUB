package com.aditya.f1hub.service;

import com.aditya.f1hub.dto.result.RaceResultRequest;
import com.aditya.f1hub.dto.result.RaceResultResponse;
import com.aditya.f1hub.dto.result.RaceResultSyncResponse;

import java.util.List;

public interface RaceResultService {

    /**
     * Creates a new race result.
     */
    RaceResultResponse createResult(RaceResultRequest request);

    /**
     * Retrieves a race result by its ID.
     */
    RaceResultResponse getResultById(Long id);

    /**
     * Retrieves all race results.
     */
    List<RaceResultResponse> getAllResults();

    /**
     * Retrieves all results for a specific session.
     */
    List<RaceResultResponse> getResultsBySessionId(Long sessionId);

    /**
     * Retrieves all results for a specific driver.
     */
    List<RaceResultResponse> getResultsByDriverId(Long driverId);

    /**
     * Retrieves all results for a specific constructor.
     */
    List<RaceResultResponse> getResultsByConstructorId(Long constructorId);

    /**
     * Updates an existing race result.
     */
    RaceResultResponse updateResult(
            Long id,
            RaceResultRequest request
    );

    /**
     * Deletes a race result by its ID.
     */
    void deleteResult(Long id);

    /**
     * Synchronizes session results from OpenF1 for a given session.
     */
    RaceResultSyncResponse synchronizeResults(Long sessionId);
}