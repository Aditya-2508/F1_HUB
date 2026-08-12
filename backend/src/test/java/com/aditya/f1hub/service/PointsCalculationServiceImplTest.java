package com.aditya.f1hub.service;

import com.aditya.f1hub.entity.RaceResult;
import com.aditya.f1hub.entity.Session;
import com.aditya.f1hub.service.impl.PointsCalculationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PointsCalculationServiceImplTest {

    private PointsCalculationService pointsCalculationService;

    @BeforeEach
    void setUp() {
        pointsCalculationService =
                new PointsCalculationServiceImpl();
    }

    @Test
    void shouldAwardTwentyFivePointsForRaceWinner() {

        RaceResult result =
                createResult("Race", 1);

        assertEquals(
                25.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldAwardEighteenPointsForRaceSecondPlace() {

        RaceResult result =
                createResult("Race", 2);

        assertEquals(
                18.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldAwardOnePointForRaceTenthPlace() {

        RaceResult result =
                createResult("Race", 10);

        assertEquals(
                1.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldAwardZeroPointsForRaceEleventhPlace() {

        RaceResult result =
                createResult("Race", 11);

        assertEquals(
                0.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldAwardEightPointsForSprintWinner() {

        RaceResult result =
                createResult("Sprint", 1);

        assertEquals(
                8.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldAwardSevenPointsForSprintSecondPlace() {

        RaceResult result =
                createResult("Sprint", 2);

        assertEquals(
                7.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldAwardOnePointForSprintEighthPlace() {

        RaceResult result =
                createResult("Sprint", 8);

        assertEquals(
                1.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldAwardZeroPointsForSprintNinthPlace() {

        RaceResult result =
                createResult("Sprint", 9);

        assertEquals(
                0.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldAwardZeroPointsForPractice() {

        RaceResult result =
                createResult("Practice", 1);

        assertEquals(
                0.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldAwardZeroPointsForQualifying() {

        RaceResult result =
                createResult("Qualifying", 1);

        assertEquals(
                0.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldAwardZeroPointsForDns() {

        RaceResult result =
                createResult("Race", 1);

        result.setDns(true);

        assertEquals(
                0.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldAwardZeroPointsForDsq() {

        RaceResult result =
                createResult("Race", 1);

        result.setDsq(true);

        assertEquals(
                0.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldAwardZeroPointsForNullPosition() {

        RaceResult result =
                createResult("Race", null);

        assertEquals(
                0.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldAwardZeroPointsForInvalidPosition() {

        RaceResult result =
                createResult("Race", 0);

        assertEquals(
                0.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldReturnZeroForNullRaceResult() {

        assertEquals(
                0.0,
                pointsCalculationService.calculatePoints(null)
        );
    }

    @Test
    void shouldTreatDnfAsPotentiallyScoringResult() {

        RaceResult result =
                createResult("Race", 2);

        result.setDnf(true);

        assertEquals(
                18.0,
                pointsCalculationService.calculatePoints(result)
        );
    }

    @Test
    void shouldIdentifyRaceAsChampionshipResult() {

        RaceResult result =
                createResult("Race", 1);

        assertTrue(
                pointsCalculationService
                        .isChampionshipResult(result)
        );
    }

    @Test
    void shouldIdentifySprintAsChampionshipResult() {

        RaceResult result =
                createResult("Sprint", 1);

        assertTrue(
                pointsCalculationService
                        .isChampionshipResult(result)
        );
    }

    @Test
    void shouldNotIdentifyPracticeAsChampionshipResult() {

        RaceResult result =
                createResult("Practice", 1);

        assertFalse(
                pointsCalculationService
                        .isChampionshipResult(result)
        );
    }

    @Test
    void shouldNotIdentifyQualifyingAsChampionshipResult() {

        RaceResult result =
                createResult("Qualifying", 1);

        assertFalse(
                pointsCalculationService
                        .isChampionshipResult(result)
        );
    }

    @Test
    void shouldIdentifyRaceWinner() {

        RaceResult result =
                createResult("Race", 1);

        assertTrue(
                pointsCalculationService
                        .isRaceWin(result)
        );
    }

    @Test
    void shouldNotIdentifyRaceSecondPlaceAsWinner() {

        RaceResult result =
                createResult("Race", 2);

        assertFalse(
                pointsCalculationService
                        .isRaceWin(result)
        );
    }

    @Test
    void shouldNotIdentifySprintWinnerAsRaceWinner() {

        RaceResult result =
                createResult("Sprint", 1);

        assertFalse(
                pointsCalculationService
                        .isRaceWin(result)
        );
    }

    @Test
    void shouldNotIdentifyQualifyingWinnerAsRaceWinner() {

        RaceResult result =
                createResult("Qualifying", 1);

        assertFalse(
                pointsCalculationService
                        .isRaceWin(result)
        );
    }

    private RaceResult createResult(
            String sessionType,
            Integer position
    ) {

        Session session = Session.builder()
                .sessionType(sessionType)
                .build();

        return RaceResult.builder()
                .session(session)
                .position(position)
                .dnf(false)
                .dns(false)
                .dsq(false)
                .build();
    }
}