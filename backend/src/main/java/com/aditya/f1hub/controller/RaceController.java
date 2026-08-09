package com.aditya.f1hub.controller;

import com.aditya.f1hub.dto.ApiResponse;
import com.aditya.f1hub.dto.race.RaceRequestDto;
import com.aditya.f1hub.dto.race.RaceResponseDto;
import com.aditya.f1hub.integration.dto.RaceSyncResponseDto;
import com.aditya.f1hub.integration.service.RaceSyncService;
import com.aditya.f1hub.service.RaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/races")
@RequiredArgsConstructor
public class RaceController {

    private final RaceService raceService;
    private final RaceSyncService raceSyncService;

    /**
     * Create Race
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RaceResponseDto>> createRace(
            @Valid @RequestBody RaceRequestDto requestDto) {

        RaceResponseDto responseDto =
                raceService.createRace(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Race created successfully.",
                        responseDto));
    }

    /**
     * Get All Races
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RaceResponseDto>>> getAllRaces() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Races retrieved successfully.",
                        raceService.getAllRaces()));
    }

    /**
     * Get Race By ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RaceResponseDto>> getRaceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Race retrieved successfully.",
                        raceService.getRaceById(id)));
    }

    /**
     * Update Race
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RaceResponseDto>> updateRace(
            @PathVariable Long id,
            @Valid @RequestBody RaceRequestDto requestDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Race updated successfully.",
                        raceService.updateRace(id, requestDto)));
    }

    /**
     * Delete Race
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRace(
            @PathVariable Long id) {

        raceService.deleteRace(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Race deleted successfully.",
                        null));
    }

    /**
     * Search Races
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RaceResponseDto>>> searchRaces(

            @RequestParam(required = false) String name,

            @RequestParam(required = false) Long seasonId,

            @RequestParam(required = false) Long circuitId,

            @RequestParam(required = false) String countryName,

            @RequestParam(required = false) Boolean active,

            @RequestParam(required = false) Boolean cancelled) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Races retrieved successfully.",
                        raceService.searchRaces(
                                name,
                                seasonId,
                                circuitId,
                                countryName,
                                active,
                                cancelled)));
    }

    /**
     * Synchronize Races from OpenF1
     */
    /**
     * Synchronize Races from OpenF1 for a specific season year.
     */
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<RaceSyncResponseDto>> syncRaces(
            @RequestParam Integer year) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Race synchronization completed successfully.",
                        raceSyncService.synchronizeRaces(year)
                )
        );
    }
}