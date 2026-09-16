package com.aditya.f1hub.controller.analytics;

import com.aditya.f1hub.dto.analytics.ConstructorComparisonResponseDto;
import com.aditya.f1hub.service.analytics.ConstructorComparisonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConstructorComparisonControllerTest {

    @Mock
    private ConstructorComparisonService constructorComparisonService;

    @InjectMocks
    private ConstructorComparisonController constructorComparisonController;

    private ConstructorComparisonResponseDto firstConstructor;
    private ConstructorComparisonResponseDto secondConstructor;

    @BeforeEach
    void setUp() {

        firstConstructor = ConstructorComparisonResponseDto.builder()
                .constructorId(1L)
                .constructorName("Constructor A")
                .constructorFullName("Constructor A Racing")
                .nationality("British")
                .countryCode("GBR")
                .championships(5)
                .wins(80)
                .podiums(150)
                .careerPoints(2500.5)
                .build();

        secondConstructor = ConstructorComparisonResponseDto.builder()
                .constructorId(2L)
                .constructorName("Constructor B")
                .constructorFullName("Constructor B Racing")
                .nationality("Italian")
                .countryCode("ITA")
                .championships(3)
                .wins(50)
                .podiums(100)
                .careerPoints(1800.0)
                .build();
    }

    @Test
    void shouldCompareConstructors() {

        List<Long> constructorIds = List.of(1L, 2L);

        List<ConstructorComparisonResponseDto> comparison =
                List.of(firstConstructor, secondConstructor);

        when(constructorComparisonService.compareConstructors(constructorIds))
                .thenReturn(comparison);

        ResponseEntity<List<ConstructorComparisonResponseDto>> response =
                constructorComparisonController.compareConstructors(constructorIds);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertSame(
                comparison,
                response.getBody()
        );

        assertEquals(
                2,
                response.getBody().size()
        );

        assertEquals(
                1L,
                response.getBody().get(0).getConstructorId()
        );

        assertEquals(
                2L,
                response.getBody().get(1).getConstructorId()
        );

        verify(constructorComparisonService)
                .compareConstructors(constructorIds);
    }

    @Test
    void shouldReturnEmptyComparisonResult() {

        List<Long> constructorIds = List.of(1L, 2L);

        when(constructorComparisonService.compareConstructors(constructorIds))
                .thenReturn(List.of());

        ResponseEntity<List<ConstructorComparisonResponseDto>> response =
                constructorComparisonController.compareConstructors(constructorIds);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                List.of(),
                response.getBody()
        );

        verify(constructorComparisonService)
                .compareConstructors(constructorIds);
    }

    @Test
    void shouldPassConstructorIdsToService() {

        List<Long> constructorIds = List.of(5L, 10L, 15L);

        when(constructorComparisonService.compareConstructors(constructorIds))
                .thenReturn(List.of(
                        firstConstructor,
                        secondConstructor
                ));

        constructorComparisonController.compareConstructors(constructorIds);

        verify(constructorComparisonService)
                .compareConstructors(constructorIds);
    }
}