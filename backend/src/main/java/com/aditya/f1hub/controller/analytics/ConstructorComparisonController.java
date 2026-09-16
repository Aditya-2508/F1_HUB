package com.aditya.f1hub.controller.analytics;

import com.aditya.f1hub.dto.analytics.ConstructorComparisonResponseDto;
import com.aditya.f1hub.service.analytics.ConstructorComparisonService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics/constructors")
@RequiredArgsConstructor
public class ConstructorComparisonController {

    private final ConstructorComparisonService constructorComparisonService;

    @GetMapping("/compare")
    public ResponseEntity<List<ConstructorComparisonResponseDto>> compareConstructors(
            @RequestParam
            @NotEmpty(message = "At least one constructor ID is required")
            List<Long> constructorIds
    ) {
        return ResponseEntity.ok(
                constructorComparisonService.compareConstructors(constructorIds)
        );
    }
}