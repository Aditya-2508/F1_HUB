package com.aditya.f1hub.service.analytics.impl;

import com.aditya.f1hub.dto.analytics.ConstructorComparisonResponseDto;
import com.aditya.f1hub.entity.Constructor;
import com.aditya.f1hub.exception.BadRequestException;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.repository.ConstructorRepository;
import com.aditya.f1hub.repository.ConstructorStandingRepository;
import com.aditya.f1hub.repository.ConstructorStandingStatisticsProjection;
import com.aditya.f1hub.repository.RaceResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConstructorComparisonServiceImplTest {

    @Mock
    private ConstructorRepository constructorRepository;

    @Mock
    private ConstructorStandingRepository constructorStandingRepository;

    @Mock
    private RaceResultRepository raceResultRepository;

    @InjectMocks
    private ConstructorComparisonServiceImpl constructorComparisonService;

    @Test
    void shouldCompareTwoConstructorsSuccessfully() {

        Constructor constructor1 = org.mockito.Mockito.mock(Constructor.class);
        Constructor constructor2 = org.mockito.Mockito.mock(Constructor.class);

        when(constructor1.getId())
                .thenReturn(1L);

        when(constructor1.getName())
                .thenReturn("Red Bull Racing");

        when(constructor1.getFullName())
                .thenReturn("Oracle Red Bull Racing");

        when(constructor1.getNationality())
                .thenReturn("Austrian");

        when(constructor1.getCountryCode())
                .thenReturn("AUT");

        when(constructor2.getId())
                .thenReturn(2L);

        when(constructor2.getName())
                .thenReturn("Ferrari");

        when(constructor2.getFullName())
                .thenReturn("Scuderia Ferrari");

        when(constructor2.getNationality())
                .thenReturn("Italian");

        when(constructor2.getCountryCode())
                .thenReturn("ITA");

        ConstructorStandingStatisticsProjection constructor1Statistics =
                mockStandingStatistics(
                        6L,
                        8L,
                        450.5
                );

        ConstructorStandingStatisticsProjection constructor2Statistics =
                mockStandingStatistics(
                        16L,
                        248L,
                        5000.0
                );

        when(constructorRepository.findById(1L))
                .thenReturn(Optional.of(constructor1));

        when(constructorRepository.findById(2L))
                .thenReturn(Optional.of(constructor2));

        when(constructorStandingRepository
                .findConstructorStandingStatistics(1L))
                .thenReturn(constructor1Statistics);

        when(constructorStandingRepository
                .findConstructorStandingStatistics(2L))
                .thenReturn(constructor2Statistics);

        when(raceResultRepository.countConstructorPodiums(1L))
                .thenReturn(25L);

        when(raceResultRepository.countConstructorPodiums(2L))
                .thenReturn(800L);

        List<ConstructorComparisonResponseDto> result =
                constructorComparisonService.compareConstructors(
                        List.of(1L, 2L)
                );

        assertThat(result)
                .hasSize(2);

        ConstructorComparisonResponseDto first =
                result.get(0);

        assertThat(first.getConstructorId())
                .isEqualTo(1L);

        assertThat(first.getConstructorName())
                .isEqualTo("Red Bull Racing");

        assertThat(first.getConstructorFullName())
                .isEqualTo("Oracle Red Bull Racing");

        assertThat(first.getNationality())
                .isEqualTo("Austrian");

        assertThat(first.getCountryCode())
                .isEqualTo("AUT");

        assertThat(first.getChampionships())
                .isEqualTo(6);

        assertThat(first.getWins())
                .isEqualTo(8);

        assertThat(first.getPodiums())
                .isEqualTo(25);

        assertThat(first.getCareerPoints())
                .isEqualTo(450.5);

        ConstructorComparisonResponseDto second =
                result.get(1);

        assertThat(second.getConstructorId())
                .isEqualTo(2L);

        assertThat(second.getConstructorName())
                .isEqualTo("Ferrari");

        assertThat(second.getConstructorFullName())
                .isEqualTo("Scuderia Ferrari");

        assertThat(second.getNationality())
                .isEqualTo("Italian");

        assertThat(second.getCountryCode())
                .isEqualTo("ITA");

        assertThat(second.getChampionships())
                .isEqualTo(16);

        assertThat(second.getWins())
                .isEqualTo(248);

        assertThat(second.getPodiums())
                .isEqualTo(800);

        assertThat(second.getCareerPoints())
                .isEqualTo(5000.0);
    }

    @Test
    void shouldRejectNullConstructorIds() {

        List<Long> constructorIds =
                Arrays.asList(1L, null);

        assertThatThrownBy(() ->
                constructorComparisonService.compareConstructors(
                        constructorIds
                )
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(
                        "Constructor IDs must not contain null values."
                );

        verify(constructorRepository, never())
                .findById(anyLong());
    }

    @Test
    void shouldRejectLessThanTwoConstructors() {

        assertThatThrownBy(() ->
                constructorComparisonService.compareConstructors(
                        List.of(1L)
                )
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(
                        "At least two constructors are required for comparison."
                );

        verify(constructorRepository, never())
                .findById(anyLong());
    }

    @Test
    void shouldRejectEmptyConstructorIds() {

        assertThatThrownBy(() ->
                constructorComparisonService.compareConstructors(
                        List.of()
                )
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(
                        "At least two constructors are required for comparison."
                );

        verify(constructorRepository, never())
                .findById(anyLong());
    }

    @Test
    void shouldRejectDuplicateConstructorIds() {

        assertThatThrownBy(() ->
                constructorComparisonService.compareConstructors(
                        List.of(1L, 1L)
                )
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(
                        "Duplicate constructor IDs are not allowed."
                );

        verify(constructorRepository, never())
                .findById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenConstructorDoesNotExist() {

        when(constructorRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                constructorComparisonService.compareConstructors(
                        List.of(999L, 1L)
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Constructor not found with id : '999'"
                );

        verify(constructorStandingRepository, never())
                .findConstructorStandingStatistics(999L);

        verify(raceResultRepository, never())
                .countConstructorPodiums(999L);
    }

    @Test
    void shouldReturnConstructorsInRequestedOrder() {

        Constructor constructor1 =
                org.mockito.Mockito.mock(Constructor.class);

        Constructor constructor2 =
                org.mockito.Mockito.mock(Constructor.class);

        when(constructor1.getId())
                .thenReturn(1L);

        when(constructor1.getName())
                .thenReturn("Red Bull Racing");

        when(constructor1.getFullName())
                .thenReturn("Oracle Red Bull Racing");

        when(constructor1.getNationality())
                .thenReturn("Austrian");

        when(constructor1.getCountryCode())
                .thenReturn("AUT");

        when(constructor2.getId())
                .thenReturn(2L);

        when(constructor2.getName())
                .thenReturn("Ferrari");

        when(constructor2.getFullName())
                .thenReturn("Scuderia Ferrari");

        when(constructor2.getNationality())
                .thenReturn("Italian");

        when(constructor2.getCountryCode())
                .thenReturn("ITA");

        ConstructorStandingStatisticsProjection constructor1Statistics =
                mockStandingStatistics(
                        6L,
                        8L,
                        450.5
                );

        ConstructorStandingStatisticsProjection constructor2Statistics =
                mockStandingStatistics(
                        16L,
                        248L,
                        5000.0
                );

        when(constructorRepository.findById(1L))
                .thenReturn(Optional.of(constructor1));

        when(constructorRepository.findById(2L))
                .thenReturn(Optional.of(constructor2));

        when(constructorStandingRepository
                .findConstructorStandingStatistics(1L))
                .thenReturn(constructor1Statistics);

        when(constructorStandingRepository
                .findConstructorStandingStatistics(2L))
                .thenReturn(constructor2Statistics);

        when(raceResultRepository.countConstructorPodiums(1L))
                .thenReturn(25L);

        when(raceResultRepository.countConstructorPodiums(2L))
                .thenReturn(800L);

        List<ConstructorComparisonResponseDto> result =
                constructorComparisonService.compareConstructors(
                        List.of(2L, 1L)
                );

        assertThat(result)
                .hasSize(2);

        assertThat(result.get(0).getConstructorId())
                .isEqualTo(2L);

        assertThat(result.get(0).getConstructorName())
                .isEqualTo("Ferrari");

        assertThat(result.get(1).getConstructorId())
                .isEqualTo(1L);

        assertThat(result.get(1).getConstructorName())
                .isEqualTo("Red Bull Racing");
    }

    private ConstructorStandingStatisticsProjection mockStandingStatistics(
            long championships,
            long wins,
            double points
    ) {

        ConstructorStandingStatisticsProjection projection =
                org.mockito.Mockito.mock(
                        ConstructorStandingStatisticsProjection.class
                );

        when(projection.getChampionships())
                .thenReturn(championships);

        when(projection.getWins())
                .thenReturn(wins);

        when(projection.getPoints())
                .thenReturn(points);

        return projection;
    }
}