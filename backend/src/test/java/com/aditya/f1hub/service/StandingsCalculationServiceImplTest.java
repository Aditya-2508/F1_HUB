package com.aditya.f1hub.service;

import com.aditya.f1hub.entity.Constructor;
import com.aditya.f1hub.entity.ConstructorStanding;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.entity.DriverStanding;
import com.aditya.f1hub.entity.RaceResult;
import com.aditya.f1hub.entity.Season;
import com.aditya.f1hub.entity.Session;
import com.aditya.f1hub.repository.ConstructorStandingRepository;
import com.aditya.f1hub.repository.DriverStandingRepository;
import com.aditya.f1hub.repository.RaceResultRepository;
import com.aditya.f1hub.repository.SeasonRepository;
import com.aditya.f1hub.service.impl.StandingsCalculationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StandingsCalculationServiceImplTest {

    @Mock
    private SeasonRepository seasonRepository;

    @Mock
    private RaceResultRepository raceResultRepository;

    @Mock
    private DriverStandingRepository driverStandingRepository;

    @Mock
    private ConstructorStandingRepository constructorStandingRepository;

    @Mock
    private PointsCalculationService pointsCalculationService;

    @InjectMocks
    private StandingsCalculationServiceImpl standingsCalculationService;

    private Season season;

    private Driver driverA;
    private Driver driverB;

    private Constructor constructorA;
    private Constructor constructorB;

    @BeforeEach
    void setUp() {

        season = new Season();
        season.setId(1L);
        season.setYear(2026);

        driverA = Driver.builder()
                .fullName("Driver A")
                .abbreviation("DRA")
                .build();

        driverA.setId(1L);

        driverB = Driver.builder()
                .fullName("Driver B")
                .abbreviation("DRB")
                .build();

        driverB.setId(2L);

        constructorA = new Constructor();
        constructorA.setId(1L);
        constructorA.setName("Constructor A");

        constructorB = new Constructor();
        constructorB.setId(2L);
        constructorB.setName("Constructor B");
    }

    @Test
    void shouldCalculateDriverStandingsCorrectly() {

        RaceResult raceOneDriverA =
                createResult(
                        driverA,
                        constructorA,
                        "Race",
                        1
                );

        RaceResult sprintDriverA =
                createResult(
                        driverA,
                        constructorA,
                        "Sprint",
                        1
                );

        RaceResult raceTwoDriverA =
                createResult(
                        driverA,
                        constructorA,
                        "Race",
                        2
                );

        RaceResult raceOneDriverB =
                createResult(
                        driverB,
                        constructorB,
                        "Race",
                        2
                );

        List<RaceResult> results = List.of(
                raceOneDriverA,
                sprintDriverA,
                raceTwoDriverA,
                raceOneDriverB
        );

        when(seasonRepository.findById(1L))
                .thenReturn(Optional.of(season));

        when(raceResultRepository.findAllBySeasonId(1L))
                .thenReturn(results);

        when(pointsCalculationService.isChampionshipResult(
                any(RaceResult.class)
        )).thenReturn(true);

        when(pointsCalculationService.calculatePoints(
                raceOneDriverA
        )).thenReturn(25.0);

        when(pointsCalculationService.calculatePoints(
                sprintDriverA
        )).thenReturn(8.0);

        when(pointsCalculationService.calculatePoints(
                raceTwoDriverA
        )).thenReturn(18.0);

        when(pointsCalculationService.calculatePoints(
                raceOneDriverB
        )).thenReturn(18.0);

        when(pointsCalculationService.isRaceWin(
                raceOneDriverA
        )).thenReturn(true);

        when(pointsCalculationService.isRaceWin(
                sprintDriverA
        )).thenReturn(false);

        when(pointsCalculationService.isRaceWin(
                raceTwoDriverA
        )).thenReturn(false);

        when(pointsCalculationService.isRaceWin(
                raceOneDriverB
        )).thenReturn(false);

        standingsCalculationService.calculateStandings(1L);

        ArgumentCaptor<List<DriverStanding>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(driverStandingRepository)
                .saveAll(captor.capture());

        List<DriverStanding> standings =
                captor.getValue();

        assertEquals(2, standings.size());

        assertEquals(
                driverA,
                standings.get(0).getDriver()
        );

        assertEquals(
                1,
                standings.get(0).getPosition()
        );

        assertEquals(
                51.0,
                standings.get(0).getPoints()
        );

        assertEquals(
                1,
                standings.get(0).getWins()
        );

        assertEquals(
                driverB,
                standings.get(1).getDriver()
        );

        assertEquals(
                2,
                standings.get(1).getPosition()
        );

        assertEquals(
                18.0,
                standings.get(1).getPoints()
        );
    }

    @Test
    void shouldCalculateConstructorStandingsCorrectly() {

        RaceResult driverARace =
                createResult(
                        driverA,
                        constructorA,
                        "Race",
                        1
                );

        RaceResult driverBRace =
                createResult(
                        driverB,
                        constructorA,
                        "Race",
                        2
                );

        List<RaceResult> results = List.of(
                driverARace,
                driverBRace
        );

        when(seasonRepository.findById(1L))
                .thenReturn(Optional.of(season));

        when(raceResultRepository.findAllBySeasonId(1L))
                .thenReturn(results);

        when(pointsCalculationService.isChampionshipResult(
                any(RaceResult.class)
        )).thenReturn(true);

        when(pointsCalculationService.calculatePoints(
                driverARace
        )).thenReturn(25.0);

        when(pointsCalculationService.calculatePoints(
                driverBRace
        )).thenReturn(18.0);

        when(pointsCalculationService.isRaceWin(
                driverARace
        )).thenReturn(true);

        when(pointsCalculationService.isRaceWin(
                driverBRace
        )).thenReturn(false);

        standingsCalculationService.calculateStandings(1L);

        ArgumentCaptor<List<ConstructorStanding>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(constructorStandingRepository)
                .saveAll(captor.capture());

        List<ConstructorStanding> standings =
                captor.getValue();

        assertEquals(1, standings.size());

        assertEquals(
                constructorA,
                standings.get(0).getConstructor()
        );

        assertEquals(
                43.0,
                standings.get(0).getPoints()
        );

        assertEquals(
                1,
                standings.get(0).getWins()
        );
    }

    @Test
    void shouldDeleteExistingStandingsBeforeRecalculation() {

        when(seasonRepository.findById(1L))
                .thenReturn(Optional.of(season));

        when(raceResultRepository.findAllBySeasonId(1L))
                .thenReturn(List.of());

        standingsCalculationService.calculateStandings(1L);

        verify(driverStandingRepository)
                .deleteBySeasonId(1L);

        verify(constructorStandingRepository)
                .deleteBySeasonId(1L);

        verify(driverStandingRepository)
                .saveAll(anyList());

        verify(constructorStandingRepository)
                .saveAll(anyList());
    }

    @Test
    void shouldThrowExceptionWhenSeasonDoesNotExist() {

        when(seasonRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> standingsCalculationService
                        .calculateStandings(999L)
        );
    }

    private RaceResult createResult(
            Driver driver,
            Constructor constructor,
            String sessionType,
            Integer position
    ) {

        Session session = Session.builder()
                .sessionType(sessionType)
                .build();

        return RaceResult.builder()
                .driver(driver)
                .constructor(constructor)
                .session(session)
                .position(position)
                .dnf(false)
                .dns(false)
                .dsq(false)
                .build();
    }
}