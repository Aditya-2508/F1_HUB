package com.aditya.f1hub.controller;

import com.aditya.f1hub.dto.search.SearchResponseDto;
import com.aditya.f1hub.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    private SearchResponseDto searchResponse;

    @BeforeEach
    void setUp() {

        searchResponse = SearchResponseDto.builder()
                .drivers(List.of())
                .constructors(List.of())
                .circuits(List.of())
                .races(List.of())
                .seasons(List.of())
                .build();
    }

    @Test
    void shouldReturnSearchResults() {

        when(searchService.search("hamilton"))
                .thenReturn(searchResponse);

        ResponseEntity<?> response =
                searchController.search("hamilton");

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        verify(searchService)
                .search("hamilton");
    }

    @Test
    void shouldReturnEmptySearchResults() {

        when(searchService.search("xyz-no-match"))
                .thenReturn(searchResponse);

        ResponseEntity<?> response =
                searchController.search("xyz-no-match");

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        verify(searchService)
                .search("xyz-no-match");
    }
}