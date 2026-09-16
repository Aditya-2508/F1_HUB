package com.aditya.f1hub.service.analytics.impl;

import com.aditya.f1hub.dto.analytics.DriverPointsProgressionResponseDto;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.RaceResult;
import com.aditya.f1hub.entity.Season;
import com.aditya.f1hub.entity.Session;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.repository.DriverRepository;
import com.aditya.f1hub.repository.RaceResultRepository;
import com.aditya.f1hub.repository.SeasonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointsProgressionServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @Mock
    private RaceResultRepository raceResultRepository;

    @InjectMocks
    private PointsProgressionServiceImpl pointsProgressionService;

    @Test
    void shouldCalculateDriverPointsProgressionSuccessfully() {

        Driver driver = org.mockito.Mockito.mock(Driver.class);
        Season season = org.mockito.Mockito.mock(Season.class);

        Race race1 = mockRace(1L, 1, "Australian Grand Prix");
        Race race2 = mockRace(2L, 2, "Japanese Grand Prix");
        Race race3 = mockRace(3L, 3, "Bahrain Grand Prix");

        Session session1 = mockRaceSession(race1);
        Session session2 = mockRaceSession(race2);
        Session session3 = mockRaceSession(race3);

        RaceResult result1 = mockResult(driver, session1, 25.0);
        RaceResult result2 = mockResult(driver, session2, 18.0);
        RaceResult result3 = mockResult(driver, session3, 15.0);

        when(driver.getId()).thenReturn(1L);
        when(driver.getFullName()).thenReturn("Max Verstappen");

        when(season.getId()).thenReturn(1L);
        when(season.getYear()).thenReturn(2026);

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(driver));

        when(seasonRepository.findById(1L))
                .thenReturn(Optional.of(season));

        when(raceResultRepository.findAllBySeasonId(1L))
                .thenReturn(List.of(result1, result2, result3));

        DriverPointsProgressionResponseDto result =
                pointsProgressionService.getDriverPointsProgression(
                        1L,
                        1L
                );

        assertThat(result).isNotNull();
        assertThat(result.getDriverId()).isEqualTo(1L);
        assertThat(result.getDriverName()).isEqualTo("Max Verstappen");
        assertThat(result.getSeasonId()).isEqualTo(1L);
        assertThat(result.getSeasonYear()).isEqualTo(2026);

        assertThat(result.getPoints())
                .hasSize(3);

        assertThat(result.getPoints().get(0).getPoints())
                .isEqualTo(25.0);

        assertThat(result.getPoints().get(0).getCumulativePoints())
                .isEqualTo(25.0);

        assertThat(result.getPoints().get(1).getPoints())
                .isEqualTo(18.0);

        assertThat(result.getPoints().get(1).getCumulativePoints())
                .isEqualTo(43.0);

        assertThat(result.getPoints().get(2).getPoints())
                .isEqualTo(15.0);

        assertThat(result.getPoints().get(2).getCumulativePoints())
                .isEqualTo(58.0);
    }

    @Test
    void shouldIgnoreResultsBelongingToAnotherDriver() {

        Driver requestedDriver = org.mockito.Mockito.mock(Driver.class);
        Driver anotherDriver = org.mockito.Mockito.mock(Driver.class);

        Season season = org.mockito.Mockito.mock(Season.class);

        Race race1 = mockRace(
                1L,
                1,
                "Australian Grand Prix"
        );

        Session session1 = mockRaceSession(race1);

        Session session2 = org.mockito.Mockito.mock(Session.class);

        RaceResult requestedResult =
                org.mockito.Mockito.mock(RaceResult.class);

        RaceResult otherResult =
                org.mockito.Mockito.mock(RaceResult.class);

        when(requestedDriver.getId()).thenReturn(1L);
        when(requestedDriver.getFullName()).thenReturn("Driver A");

        when(anotherDriver.getId()).thenReturn(2L);

        when(requestedResult.getDriver())
                .thenReturn(requestedDriver);

        when(requestedResult.getSession())
                .thenReturn(session1);

        when(requestedResult.getPoints())
                .thenReturn(25.0);

        when(otherResult.getDriver())
                .thenReturn(anotherDriver);

        when(season.getId()).thenReturn(1L);
        when(season.getYear()).thenReturn(2026);

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(requestedDriver));

        when(seasonRepository.findById(1L))
                .thenReturn(Optional.of(season));

        when(raceResultRepository.findAllBySeasonId(1L))
                .thenReturn(List.of(
                        requestedResult,
                        otherResult
                ));

        DriverPointsProgressionResponseDto result =
                pointsProgressionService.getDriverPointsProgression(
                        1L,
                        1L
                );

        assertThat(result.getPoints())
                .hasSize(1);

        assertThat(result.getPoints().get(0).getPoints())
                .isEqualTo(25.0);

        assertThat(result.getPoints().get(0).getCumulativePoints())
                .isEqualTo(25.0);
    }

    @Test
    void shouldIgnoreNonRaceSessions() {

        Driver driver = org.mockito.Mockito.mock(Driver.class);
        Season season = org.mockito.Mockito.mock(Season.class);

        Race race1 = mockRace(
                1L,
                1,
                "Australian Grand Prix"
        );

        Session raceSession = mockRaceSession(race1);

        Session qualifyingSession =
                org.mockito.Mockito.mock(Session.class);

        when(qualifyingSession.getSessionType())
                .thenReturn("Qualifying");

        RaceResult raceResult =
                org.mockito.Mockito.mock(RaceResult.class);

        RaceResult qualifyingResult =
                org.mockito.Mockito.mock(RaceResult.class);

        when(driver.getId()).thenReturn(1L);
        when(driver.getFullName()).thenReturn("Driver A");

        when(raceResult.getDriver())
                .thenReturn(driver);

        when(raceResult.getSession())
                .thenReturn(raceSession);

        when(raceResult.getPoints())
                .thenReturn(25.0);

        when(qualifyingResult.getDriver())
                .thenReturn(driver);

        when(qualifyingResult.getSession())
                .thenReturn(qualifyingSession);

        when(season.getId()).thenReturn(1L);
        when(season.getYear()).thenReturn(2026);

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(driver));

        when(seasonRepository.findById(1L))
                .thenReturn(Optional.of(season));

        when(raceResultRepository.findAllBySeasonId(1L))
                .thenReturn(List.of(
                        raceResult,
                        qualifyingResult
                ));

        DriverPointsProgressionResponseDto result =
                pointsProgressionService.getDriverPointsProgression(
                        1L,
                        1L
                );

        assertThat(result.getPoints())
                .hasSize(1);

        assertThat(result.getPoints().get(0).getRaceName())
                .isEqualTo("Australian Grand Prix");
    }

    @Test
    void shouldTreatNullPointsAsZero() {

        Driver driver = org.mockito.Mockito.mock(Driver.class);
        Season season = org.mockito.Mockito.mock(Season.class);

        Race race = mockRace(1L, 1, "Australian Grand Prix");
        Session session = mockRaceSession(race);

        RaceResult result =
                mockResult(driver, session, null);

        when(driver.getId()).thenReturn(1L);
        when(driver.getFullName()).thenReturn("Driver A");

        when(season.getId()).thenReturn(1L);
        when(season.getYear()).thenReturn(2026);

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(driver));

        when(seasonRepository.findById(1L))
                .thenReturn(Optional.of(season));

        when(raceResultRepository.findAllBySeasonId(1L))
                .thenReturn(List.of(result));

        DriverPointsProgressionResponseDto response =
                pointsProgressionService.getDriverPointsProgression(
                        1L,
                        1L
                );

        assertThat(response.getPoints())
                .hasSize(1);

        assertThat(response.getPoints().get(0).getPoints())
                .isZero();

        assertThat(response.getPoints().get(0).getCumulativePoints())
                .isZero();
    }

    @Test
    void shouldReturnEmptyProgressionWhenDriverHasNoRaceResults() {

        Driver driver = org.mockito.Mockito.mock(Driver.class);
        Season season = org.mockito.Mockito.mock(Season.class);

        when(driver.getId()).thenReturn(1L);
        when(driver.getFullName()).thenReturn("Driver A");

        when(season.getId()).thenReturn(1L);
        when(season.getYear()).thenReturn(2026);

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(driver));

        when(seasonRepository.findById(1L))
                .thenReturn(Optional.of(season));

        when(raceResultRepository.findAllBySeasonId(1L))
                .thenReturn(List.of());

        DriverPointsProgressionResponseDto response =
                pointsProgressionService.getDriverPointsProgression(
                        1L,
                        1L
                );

        assertThat(response.getPoints())
                .isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenDriverDoesNotExist() {

        when(driverRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                pointsProgressionService.getDriverPointsProgression(
                        999L,
                        1L
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Driver not found with id : '999'");

        verify(seasonRepository, never())
                .findById(1L);

        verify(raceResultRepository, never())
                .findAllBySeasonId(1L);
    }

    @Test
    void shouldThrowExceptionWhenSeasonDoesNotExist() {

        Driver driver = org.mockito.Mockito.mock(Driver.class);

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(driver));

        when(seasonRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                pointsProgressionService.getDriverPointsProgression(
                        1L,
                        999L
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Season not found with id : '999'");

        verify(raceResultRepository, never())
                .findAllBySeasonId(999L);
    }

    private Race mockRace(
            Long id,
            Integer round,
            String name
    ) {
        Race race = org.mockito.Mockito.mock(Race.class);

        when(race.getId()).thenReturn(id);
        when(race.getRoundNumber()).thenReturn(round);
        when(race.getName()).thenReturn(name);

        return race;
    }

    private Session mockRaceSession(Race race) {
        Session session = org.mockito.Mockito.mock(Session.class);

        when(session.getRace()).thenReturn(race);
        when(session.getSessionType()).thenReturn("Race");

        return session;
    }

    private Session mockSession(
            Race race,
            String sessionType
    ) {
        Session session = org.mockito.Mockito.mock(Session.class);

        when(session.getRace()).thenReturn(race);
        when(session.getSessionType()).thenReturn(sessionType);

        return session;
    }

    private RaceResult mockResult(
            Driver driver,
            Session session,
            Double points
    ) {
        RaceResult result = org.mockito.Mockito.mock(RaceResult.class);

        when(result.getDriver()).thenReturn(driver);
        when(result.getSession()).thenReturn(session);
        when(result.getPoints()).thenReturn(points);

        return result;
    }
}