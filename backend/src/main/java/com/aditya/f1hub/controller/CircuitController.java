package com.aditya.f1hub.controller;

import com.aditya.f1hub.dto.ApiResponse;
import com.aditya.f1hub.dto.circuit.CircuitRequestDto;
import com.aditya.f1hub.dto.circuit.CircuitResponseDto;
import com.aditya.f1hub.integration.dto.CircuitSyncResponseDto;
import com.aditya.f1hub.integration.service.CircuitSyncService;
import com.aditya.f1hub.service.CircuitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/circuits")
@RequiredArgsConstructor
public class CircuitController {

    private final CircuitService circuitService;
    private final CircuitSyncService circuitSyncService;

    /**
     * Create Circuit
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CircuitResponseDto>> createCircuit(
            @Valid @RequestBody CircuitRequestDto requestDto) {

        CircuitResponseDto responseDto =
                circuitService.createCircuit(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Circuit created successfully.",
                        responseDto));
    }

    /**
     * Get All Circuits
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CircuitResponseDto>>> getAllCircuits() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Circuits retrieved successfully.",
                        circuitService.getAllCircuits()));
    }

    /**
     * Get Circuit By ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CircuitResponseDto>> getCircuitById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Circuit retrieved successfully.",
                        circuitService.getCircuitById(id)));
    }

    /**
     * Update Circuit
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CircuitResponseDto>> updateCircuit(
            @PathVariable Long id,
            @Valid @RequestBody CircuitRequestDto requestDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Circuit updated successfully.",
                        circuitService.updateCircuit(id, requestDto)));
    }

    /**
     * Delete Circuit
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCircuit(
            @PathVariable Long id) {

        circuitService.deleteCircuit(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Circuit deleted successfully.",
                        null));
    }

    /**
     * Search Circuits
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CircuitResponseDto>>> searchCircuits(

            @RequestParam(required = false) String circuitName,

            @RequestParam(required = false) String country,

            @RequestParam(required = false) Boolean active) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Circuits retrieved successfully.",
                        circuitService.searchCircuits(
                                circuitName,
                                country,
                                active)));
    }



    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<CircuitSyncResponseDto>> syncCircuits() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Circuit synchronization completed successfully.",
                        circuitSyncService.synchronizeCircuits()
                )
        );
    }

}