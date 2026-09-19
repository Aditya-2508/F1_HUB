package com.aditya.f1hub.service.analytics.impl;

import com.aditya.f1hub.entity.Constructor;
import com.aditya.f1hub.entity.ConstructorStanding;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.entity.DriverStanding;
import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.RaceResult;
import com.aditya.f1hub.entity.Season;
import com.aditya.f1hub.entity.Session;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.repository.ConstructorStandingRepository;
import com.aditya.f1hub.repository.DriverStandingRepository;
import com.aditya.f1hub.repository.RaceRepository;
import com.aditya.f1hub.repository.RaceResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeasonStatisticsServiceImplTest {

    @Mock
    private RaceRepository raceRepository;

    @Mock
    private RaceResultRepository raceResultRepository;

    @Mock
    private DriverStandingRepository driverStandingRepository;

    @Mock
    private ConstructorStandingRepository constructorStandingRepository;

    @InjectMocks
    private SeasonStatisticsServiceImpl seasonStatisticsService;

    private Season season;
    private Driver driver;
    private Constructor constructor;

    @BeforeEach
    void setUp() {

        season = new Season();
        season.setId(1L);
        season.setYear(2026);

        driver = new Driver();
        driver.setId(1L);
        driver.setFullName("Max Verstappen");

        constructor = new Constructor();
        constructor.setId(1L);
        constructor.setName("Red Bull Racing");
    }

    @Test
    void shouldReturnSeasonStatistics() {

        Long seasonId = 1L;

        Race completedRace = new Race();
        completedRace.setId(1L);
        completedRace.setRoundNumber(1);
        completedRace.setCancelled(false);

        Race cancelledRace = new Race();
        cancelledRace.setId(2L);
        cancelledRace.setRoundNumber(2);
        cancelledRace.setCancelled(true);

        DriverStanding driverStanding = new DriverStanding();
        driverStanding.setSeason(season);
        driverStanding.setDriver(driver);
        driverStanding.setPosition(1);
        driverStanding.setPoints(25.0);
        driverStanding.setWins(1);

        ConstructorStanding constructorStanding =
                new ConstructorStanding();
        constructorStanding.setSeason(season);
        constructorStanding.setConstructor(constructor);
        constructorStanding.setPosition(1);
        constructorStanding.setPoints(25.0);
        constructorStanding.setWins(1);

        Session raceSession = new Session();
        raceSession.setSessionType("Race");
        raceSession.setSessionName("Race");
        raceSession.setRace(completedRace);

        RaceResult result = new RaceResult();
        result.setDriver(driver);
        result.setConstructor(constructor);
        result.setSession(raceSession);
        result.setPosition(1);
        result.setPoints(25.0);
        result.setDnf(false);
        result.setDns(false);
        result.setDsq(false);

        when(raceRepository
                .findBySeasonIdAndRoundNumberIsNotNullOrderByRoundNumberAsc(seasonId))
                .thenReturn(List.of(completedRace, cancelledRace));

        when(driverStandingRepository
                .findBySeasonIdOrderByPositionAsc(seasonId))
                .thenReturn(List.of(driverStanding));

        when(constructorStandingRepository
                .findBySeasonIdOrderByPositionAsc(seasonId))
                .thenReturn(List.of(constructorStanding));

        when(raceResultRepository.findAllBySeasonId(seasonId))
                .thenReturn(List.of(result));

        var response =
                seasonStatisticsService.getSeasonStatistics(seasonId);

        assertThat(response).isNotNull();
        assertThat(response.getSeasonId()).isEqualTo(1L);
        assertThat(response.getSeasonYear()).isEqualTo(2026);

        assertThat(response.getTotalRaces()).isEqualTo(2);
        assertThat(response.getCompletedRaces()).isEqualTo(1);
        assertThat(response.getCancelledRaces()).isEqualTo(1);

        assertThat(response.getTotalDrivers()).isEqualTo(1);
        assertThat(response.getTotalConstructors()).isEqualTo(1);

        assertThat(response.getTotalPoints()).isEqualTo(25.0);

        assertThat(response.getDriverStatistics()).hasSize(1);

        var driverStats = response.getDriverStatistics().get(0);

        assertThat(driverStats.getDriverId()).isEqualTo(1L);
        assertThat(driverStats.getDriverName())
                .isEqualTo("Max Verstappen");
        assertThat(driverStats.getPosition()).isEqualTo(1);
        assertThat(driverStats.getPoints()).isEqualTo(25.0);
        assertThat(driverStats.getWins()).isEqualTo(1);
        assertThat(driverStats.getPodiums()).isEqualTo(1);
        assertThat(driverStats.getDnfs()).isEqualTo(0);

        assertThat(response.getConstructorStatistics()).hasSize(1);

        var constructorStats =
                response.getConstructorStatistics().get(0);

        assertThat(constructorStats.getConstructorId()).isEqualTo(1L);
        assertThat(constructorStats.getConstructorName())
                .isEqualTo("Red Bull Racing");
        assertThat(constructorStats.getPosition()).isEqualTo(1);
        assertThat(constructorStats.getPoints()).isEqualTo(25.0);
        assertThat(constructorStats.getWins()).isEqualTo(1);
        assertThat(constructorStats.getPodiums()).isEqualTo(1);
    }

    @Test
    void shouldThrowExceptionWhenSeasonHasNoChampionshipRaces() {

        Long seasonId = 999L;

        when(raceRepository
                .findBySeasonIdAndRoundNumberIsNotNullOrderByRoundNumberAsc(seasonId))
                .thenReturn(List.of());

        assertThatThrownBy(() ->
                seasonStatisticsService.getSeasonStatistics(seasonId)
        )
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldCountDriverDnf() {

        Long seasonId = 1L;

        Race race = new Race();
        race.setId(1L);
        race.setRoundNumber(1);
        race.setCancelled(false);

        DriverStanding standing = new DriverStanding();
        standing.setSeason(season);
        standing.setDriver(driver);
        standing.setPosition(1);
        standing.setPoints(0.0);
        standing.setWins(0);

        Session session = new Session();
        session.setSessionType("Race");
        session.setSessionName("Race");

        RaceResult dnfResult = new RaceResult();
        dnfResult.setDriver(driver);
        dnfResult.setConstructor(constructor);
        dnfResult.setSession(session);
        dnfResult.setPosition(null);
        dnfResult.setPoints(0.0);
        dnfResult.setDnf(true);
        dnfResult.setDns(false);
        dnfResult.setDsq(false);

        when(raceRepository
                .findBySeasonIdAndRoundNumberIsNotNullOrderByRoundNumberAsc(seasonId))
                .thenReturn(List.of(race));

        when(driverStandingRepository
                .findBySeasonIdOrderByPositionAsc(seasonId))
                .thenReturn(List.of(standing));

        when(constructorStandingRepository
                .findBySeasonIdOrderByPositionAsc(seasonId))
                .thenReturn(List.of());

        when(raceResultRepository.findAllBySeasonId(seasonId))
                .thenReturn(List.of(dnfResult));

        var response =
                seasonStatisticsService.getSeasonStatistics(seasonId);

        assertThat(response.getDriverStatistics().get(0).getDnfs())
                .isEqualTo(1);
    }
}