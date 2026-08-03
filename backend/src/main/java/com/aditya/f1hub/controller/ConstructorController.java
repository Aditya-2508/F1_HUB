package com.aditya.f1hub.controller;

import com.aditya.f1hub.dto.ApiResponse;
import com.aditya.f1hub.dto.common.PageResponse;
import com.aditya.f1hub.dto.constructor.ConstructorRequestDto;
import com.aditya.f1hub.dto.constructor.ConstructorResponseDto;
import com.aditya.f1hub.service.ConstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aditya.f1hub.integration.dto.ConstructorSyncResponseDto;
import com.aditya.f1hub.integration.service.ConstructorSyncService;



@RestController
@RequestMapping("/api/constructors")
@RequiredArgsConstructor
public class ConstructorController {

    private final ConstructorSyncService constructorSyncService;
    private final ConstructorService constructorService;

    @PostMapping
    public ResponseEntity<ApiResponse<ConstructorResponseDto>> createConstructor(
            @Valid @RequestBody ConstructorRequestDto requestDto) {

        ConstructorResponseDto responseDto =
                constructorService.createConstructor(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Constructor created successfully.",
                        responseDto
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<java.util.List<ConstructorResponseDto>>> getAllConstructors() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Constructors retrieved successfully.",
                        constructorService.getAllConstructors()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConstructorResponseDto>> getConstructorById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Constructor retrieved successfully.",
                        constructorService.getConstructorById(id)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ConstructorResponseDto>> updateConstructor(
            @PathVariable Long id,
            @Valid @RequestBody ConstructorRequestDto requestDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Constructor updated successfully.",
                        constructorService.updateConstructor(id, requestDto)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteConstructor(
            @PathVariable Long id) {

        constructorService.deleteConstructor(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Constructor deleted successfully.",
                        null
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ConstructorResponseDto>>> searchConstructors(

            @RequestParam(required = false) String name,

            @RequestParam(required = false) String nationality,

            @RequestParam(required = false) Boolean active,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "name") String sortBy,

            @RequestParam(defaultValue = "asc") String sortDirection) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Constructors retrieved successfully.",
                        constructorService.searchConstructors(
                                name,
                                nationality,
                                active,
                                page,
                                size,
                                sortBy,
                                sortDirection
                        )
                )
        );
    }

    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<ConstructorSyncResponseDto>> synchronizeConstructors() {

        ConstructorSyncResponseDto response =
                constructorSyncService.synchronizeConstructors();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Constructor synchronization completed successfully.",
                        response
                )
        );
    }

}