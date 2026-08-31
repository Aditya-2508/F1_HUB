package com.aditya.f1hub.controller;

import com.aditya.f1hub.dto.ApiResponse;
import com.aditya.f1hub.dto.search.SearchResponseDto;
import com.aditya.f1hub.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<ApiResponse<SearchResponseDto>> search(
            @RequestParam String query) {

        SearchResponseDto response = searchService.search(query);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Search results retrieved successfully.",
                        response
                )
        );
    }
}