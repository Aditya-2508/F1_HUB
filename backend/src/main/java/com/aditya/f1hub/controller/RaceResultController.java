package com.aditya.f1hub.controller;

import com.aditya.f1hub.dto.result.RaceResultRequest;
import com.aditya.f1hub.dto.result.RaceResultResponse;
import com.aditya.f1hub.dto.result.RaceResultSyncResponse;
import com.aditya.f1hub.service.RaceResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class RaceResultController {

    private final RaceResultService raceResultService;

    @PostMapping
    public ResponseEntity<RaceResultResponse> createResult(
            @Valid @RequestBody RaceResultRequest request) {

        RaceResultResponse response =
                raceResultService.createResult(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<RaceResultResponse>> getAllResults() {

        return ResponseEntity.ok(
                raceResultService.getAllResults()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RaceResultResponse> getResultById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                raceResultService.getResultById(id)
        );
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<RaceResultResponse>> getResultsBySessionId(
            @PathVariable Long sessionId) {

        return ResponseEntity.ok(
                raceResultService.getResultsBySessionId(
                        sessionId
                )
        );
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<RaceResultResponse>> getResultsByDriverId(
            @PathVariable Long driverId) {

        return ResponseEntity.ok(
                raceResultService.getResultsByDriverId(
                        driverId
                )
        );
    }

    @GetMapping("/constructor/{constructorId}")
    public ResponseEntity<List<RaceResultResponse>> getResultsByConstructorId(
            @PathVariable Long constructorId) {

        return ResponseEntity.ok(
                raceResultService.getResultsByConstructorId(
                        constructorId
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<RaceResultResponse> updateResult(
            @PathVariable Long id,
            @Valid @RequestBody RaceResultRequest request) {

        return ResponseEntity.ok(
                raceResultService.updateResult(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(
            @PathVariable Long id) {

        raceResultService.deleteResult(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sync/{sessionId}")
    public ResponseEntity<RaceResultSyncResponse> synchronizeResults(
            @PathVariable Long sessionId) {

        RaceResultSyncResponse response =
                raceResultService.synchronizeResults(
                        sessionId
                );

        return ResponseEntity.ok(response);
    }
}