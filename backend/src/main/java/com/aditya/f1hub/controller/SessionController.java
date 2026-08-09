package com.aditya.f1hub.controller;

import com.aditya.f1hub.dto.ApiResponse;
import com.aditya.f1hub.dto.session.SessionRequestDto;
import com.aditya.f1hub.dto.session.SessionResponseDto;
import com.aditya.f1hub.integration.dto.SessionSyncResponseDto;
import com.aditya.f1hub.integration.service.SessionSyncService;
import com.aditya.f1hub.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final SessionSyncService sessionSyncService;

    /**
     * Create Session
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SessionResponseDto>> createSession(
            @Valid @RequestBody SessionRequestDto requestDto) {

        SessionResponseDto responseDto =
                sessionService.createSession(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Session created successfully.",
                        responseDto));
    }

    /**
     * Get All Sessions
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SessionResponseDto>>> getAllSessions() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Sessions retrieved successfully.",
                        sessionService.getAllSessions()));
    }

    /**
     * Get Session By ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SessionResponseDto>> getSessionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Session retrieved successfully.",
                        sessionService.getSessionById(id)));
    }

    /**
     * Update Session
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SessionResponseDto>> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody SessionRequestDto requestDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Session updated successfully.",
                        sessionService.updateSession(
                                id,
                                requestDto)));
    }

    /**
     * Delete Session
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(
            @PathVariable Long id) {

        sessionService.deleteSession(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Session deleted successfully.",
                        null));
    }

    /**
     * Search Sessions
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SessionResponseDto>>> searchSessions(

            @RequestParam(required = false)
            String sessionName,

            @RequestParam(required = false)
            String sessionType,

            @RequestParam(required = false)
            Long raceId,

            @RequestParam(required = false)
            Boolean active,

            @RequestParam(required = false)
            Boolean cancelled) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Sessions retrieved successfully.",
                        sessionService.searchSessions(
                                sessionName,
                                sessionType,
                                raceId,
                                active,
                                cancelled)));
    }

    /**
     * Synchronize Sessions from OpenF1
     * for a specific season year.
     */
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<SessionSyncResponseDto>> syncSessions(
            @RequestParam Integer year) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Session synchronization completed successfully.",
                        sessionSyncService.synchronizeSessions(year)
                )
        );
    }
}