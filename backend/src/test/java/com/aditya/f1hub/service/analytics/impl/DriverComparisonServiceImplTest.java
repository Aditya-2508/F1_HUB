package com.aditya.f1hub.service.analytics.impl;

import com.aditya.f1hub.dto.analytics.DriverComparisonResponseDto;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.exception.BadRequestException;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.repository.DriverCareerStatisticsProjection;
import com.aditya.f1hub.repository.DriverRepository;
import com.aditya.f1hub.repository.DriverStandingRepository;
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
class DriverComparisonServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private RaceResultRepository raceResultRepository;

    @Mock
    private DriverStandingRepository driverStandingRepository;

    @InjectMocks
    private DriverComparisonServiceImpl driverComparisonService;

    @Test
    void shouldCompareTwoDriversSuccessfully() {

        Driver driver1 = org.mockito.Mockito.mock(Driver.class);
        Driver driver2 = org.mockito.Mockito.mock(Driver.class);

        when(driver1.getId())
                .thenReturn(1L);

        when(driver1.getFullName())
                .thenReturn("Max Verstappen");

        when(driver1.getNationality())
                .thenReturn("Dutch");

        when(driver2.getId())
                .thenReturn(2L);

        when(driver2.getFullName())
                .thenReturn("Lando Norris");

        when(driver2.getNationality())
                .thenReturn("British");

        DriverCareerStatisticsProjection driver1Statistics =
                mockCareerStatistics(
                        5L,
                        10L,
                        3L,
                        250.5
                );

        DriverCareerStatisticsProjection driver2Statistics =
                mockCareerStatistics(
                        2L,
                        7L,
                        4L,
                        180.0
                );

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(driver1));

        when(driverRepository.findById(2L))
                .thenReturn(Optional.of(driver2));

        when(raceResultRepository.findDriverCareerStatistics(1L))
                .thenReturn(driver1Statistics);

        when(raceResultRepository.findDriverCareerStatistics(2L))
                .thenReturn(driver2Statistics);

        when(driverStandingRepository
                .countByDriverIdAndPosition(1L, 1))
                .thenReturn(1L);

        when(driverStandingRepository
                .countByDriverIdAndPosition(2L, 1))
                .thenReturn(0L);

        when(raceResultRepository.countDriverFastestLaps(1L))
                .thenReturn(8L);

        when(raceResultRepository.countDriverFastestLaps(2L))
                .thenReturn(5L);

        List<DriverComparisonResponseDto> result =
                driverComparisonService.compareDrivers(
                        List.of(1L, 2L)
                );

        assertThat(result)
                .hasSize(2);

        DriverComparisonResponseDto first =
                result.get(0);

        assertThat(first.getDriverId())
                .isEqualTo(1L);

        assertThat(first.getDriverName())
                .isEqualTo("Max Verstappen");

        assertThat(first.getNationality())
                .isEqualTo("Dutch");

        assertThat(first.getChampionships())
                .isEqualTo(1);

        assertThat(first.getWins())
                .isEqualTo(5);

        assertThat(first.getPodiums())
                .isEqualTo(10);

        assertThat(first.getPolePositions())
                .isEqualTo(3);

        assertThat(first.getFastestLaps())
                .isEqualTo(8);

        assertThat(first.getCareerPoints())
                .isEqualTo(250.5);

        DriverComparisonResponseDto second =
                result.get(1);

        assertThat(second.getDriverId())
                .isEqualTo(2L);

        assertThat(second.getDriverName())
                .isEqualTo("Lando Norris");

        assertThat(second.getNationality())
                .isEqualTo("British");

        assertThat(second.getChampionships())
                .isZero();

        assertThat(second.getWins())
                .isEqualTo(2);

        assertThat(second.getPodiums())
                .isEqualTo(7);

        assertThat(second.getPolePositions())
                .isEqualTo(4);

        assertThat(second.getFastestLaps())
                .isEqualTo(5);

        assertThat(second.getCareerPoints())
                .isEqualTo(180.0);
    }

    @Test
    void shouldRejectNullDriverIds() {

        List<Long> driverIds =
                Arrays.asList(1L, null);

        assertThatThrownBy(() ->
                driverComparisonService.compareDrivers(
                        driverIds
                )
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(
                        "Driver IDs must not contain null values."
                );

        verify(driverRepository, never())
                .findById(anyLong());
    }

    @Test
    void shouldRejectLessThanTwoDrivers() {

        assertThatThrownBy(() ->
                driverComparisonService.compareDrivers(
                        List.of(1L)
                )
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(
                        "At least two drivers are required for comparison."
                );

        verify(driverRepository, never())
                .findById(anyLong());
    }

    @Test
    void shouldRejectEmptyDriverIds() {

        assertThatThrownBy(() ->
                driverComparisonService.compareDrivers(
                        List.of()
                )
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(
                        "At least two drivers are required for comparison."
                );

        verify(driverRepository, never())
                .findById(anyLong());
    }

    @Test
    void shouldRejectDuplicateDriverIds() {

        assertThatThrownBy(() ->
                driverComparisonService.compareDrivers(
                        List.of(1L, 1L)
                )
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(
                        "Duplicate driver IDs are not allowed."
                );

        verify(driverRepository, never())
                .findById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenDriverDoesNotExist() {

        when(driverRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                driverComparisonService.compareDrivers(
                        List.of(999L, 1L)
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Driver not found with id : '999'"
                );

        verify(raceResultRepository, never())
                .findDriverCareerStatistics(999L);

        verify(driverStandingRepository, never())
                .countByDriverIdAndPosition(999L, 1);

        verify(raceResultRepository, never())
                .countDriverFastestLaps(999L);
    }

    @Test
    void shouldReturnDriversInRequestedOrder() {

        Driver driver1 = org.mockito.Mockito.mock(Driver.class);
        Driver driver2 = org.mockito.Mockito.mock(Driver.class);

        when(driver1.getId())
                .thenReturn(1L);

        when(driver1.getFullName())
                .thenReturn("Max Verstappen");

        when(driver1.getNationality())
                .thenReturn("Dutch");

        when(driver2.getId())
                .thenReturn(2L);

        when(driver2.getFullName())
                .thenReturn("Lando Norris");

        when(driver2.getNationality())
                .thenReturn("British");

        DriverCareerStatisticsProjection driver1Statistics =
                mockCareerStatistics(
                        3L,
                        6L,
                        2L,
                        120.0
                );

        DriverCareerStatisticsProjection driver2Statistics =
                mockCareerStatistics(
                        4L,
                        8L,
                        1L,
                        150.0
                );

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(driver1));

        when(driverRepository.findById(2L))
                .thenReturn(Optional.of(driver2));

        when(raceResultRepository.findDriverCareerStatistics(1L))
                .thenReturn(driver1Statistics);

        when(raceResultRepository.findDriverCareerStatistics(2L))
                .thenReturn(driver2Statistics);

        when(driverStandingRepository
                .countByDriverIdAndPosition(1L, 1))
                .thenReturn(0L);

        when(driverStandingRepository
                .countByDriverIdAndPosition(2L, 1))
                .thenReturn(1L);

        when(raceResultRepository.countDriverFastestLaps(1L))
                .thenReturn(2L);

        when(raceResultRepository.countDriverFastestLaps(2L))
                .thenReturn(4L);

        List<DriverComparisonResponseDto> result =
                driverComparisonService.compareDrivers(
                        List.of(2L, 1L)
                );

        assertThat(result)
                .hasSize(2);

        assertThat(result.get(0).getDriverId())
                .isEqualTo(2L);

        assertThat(result.get(0).getDriverName())
                .isEqualTo("Lando Norris");

        assertThat(result.get(1).getDriverId())
                .isEqualTo(1L);

        assertThat(result.get(1).getDriverName())
                .isEqualTo("Max Verstappen");
    }

    private DriverCareerStatisticsProjection mockCareerStatistics(
            long wins,
            long podiums,
            long polePositions,
            double careerPoints) {

        DriverCareerStatisticsProjection projection =
                org.mockito.Mockito.mock(
                        DriverCareerStatisticsProjection.class
                );

        when(projection.getRaceWins())
                .thenReturn(wins);

        when(projection.getRacePodiums())
                .thenReturn(podiums);

        when(projection.getQualifyingPoles())
                .thenReturn(polePositions);

        when(projection.getChampionshipPoints())
                .thenReturn(careerPoints);

        return projection;
    }
}