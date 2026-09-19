package com.aditya.f1hub.service.analytics.impl;

import com.aditya.f1hub.dto.analytics.RaceAnalyticsResponseDto;
import com.aditya.f1hub.entity.Constructor;
import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.RaceResult;
import com.aditya.f1hub.entity.Season;
import com.aditya.f1hub.entity.Session;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.repository.RaceRepository;
import com.aditya.f1hub.repository.RaceResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaceAnalyticsServiceImplTest {

    @Mock
    private RaceRepository raceRepository;

    @Mock
    private RaceResultRepository raceResultRepository;

    @InjectMocks
    private RaceAnalyticsServiceImpl raceAnalyticsService;

    private Race race;

    private Driver driver1;
    private Driver driver2;
    private Driver driver3;
    private Driver driver4;

    private Constructor constructor1;
    private Constructor constructor2;

    private Session raceSession;

    @BeforeEach
    void setUp() {

        Season season = new Season();
        season.setId(1L);
        season.setYear(2026);

        race = new Race();
        race.setId(11L);
        race.setName("Australian Grand Prix");
        race.setRoundNumber(1);
        race.setSeason(season);

        driver1 = Driver.builder()
                .fullName("Driver One")
                .build();
        driver1.setId(1L);

        driver2 = Driver.builder()
                .fullName("Driver Two")
                .build();
        driver2.setId(2L);

        driver3 = Driver.builder()
                .fullName("Driver Three")
                .build();
        driver3.setId(3L);

        driver4 = Driver.builder()
                .fullName("Driver Four")
                .build();
        driver4.setId(4L);

        constructor1 = Constructor.builder()
                .name("Team One")
                .build();
        constructor1.setId(1L);

        constructor2 = Constructor.builder()
                .name("Team Two")
                .build();
        constructor2.setId(2L);

        raceSession = Session.builder()
                .externalSessionId("session-11")
                .sessionName("Race")
                .sessionType("Race")
                .startTime(LocalDateTime.of(2026, 3, 8, 5, 0))
                .race(race)
                .build();
        raceSession.setId(101L);
    }

    @Test
    void shouldReturnRaceAnalyticsSuccessfully() {

        RaceResult first = buildResult(
                driver1,
                constructor1,
                1,
                1,
                25.0,
                false,
                false,
                false,
                58,
                90.0,
                0.0,
                null,
                90.0,
                50
        );

        RaceResult second = buildResult(
                driver2,
                constructor2,
                2,
                4,
                18.0,
                false,
                false,
                false,
                58,
                91.0,
                1.0,
                null,
                91.0,
                48
        );

        RaceResult third = buildResult(
                driver3,
                constructor1,
                3,
                3,
                15.0,
                false,
                false,
                false,
                58,
                92.0,
                2.0,
                null,
                92.0,
                45
        );

        RaceResult fourth = buildResult(
                driver4,
                constructor2,
                10,
                8,
                1.0,
                false,
                false,
                false,
                58,
                95.0,
                5.0,
                null,
                93.0,
                40
        );

        when(raceRepository.findById(11L))
                .thenReturn(Optional.of(race));

        when(raceResultRepository.findRaceResultsByRaceId(11L))
                .thenReturn(List.of(first, second, third, fourth));

        RaceAnalyticsResponseDto response =
                raceAnalyticsService.getRaceAnalytics(11L);

        assertThat(response).isNotNull();
        assertThat(response.getRaceId()).isEqualTo(11L);
        assertThat(response.getRaceName())
                .isEqualTo("Australian Grand Prix");
        assertThat(response.getSeasonId()).isEqualTo(1L);
        assertThat(response.getSeasonYear()).isEqualTo(2026);
        assertThat(response.getRoundNumber()).isEqualTo(1);

        assertThat(response.getStatistics()).isNotNull();
        assertThat(response.getStatistics().getTotalDrivers())
                .isEqualTo(4);

        assertThat(response.getPodium()).hasSize(3);
        assertThat(response.getDriverPerformances())
                .hasSize(4);

        assertThat(response.getFastestLap()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenRaceDoesNotExist() {

        when(raceRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                raceAnalyticsService.getRaceAnalytics(999L)
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verify(raceResultRepository, never())
                .findRaceResultsByRaceId(anyLong());
    }

    @Test
    void shouldCalculateRaceStatisticsCorrectly() {

        RaceResult finished = buildResult(
                driver1,
                constructor1,
                1,
                1,
                25.0,
                false,
                false,
                false,
                58,
                90.0,
                0.0,
                null,
                90.0,
                50
        );

        RaceResult dnf = buildResult(
                driver2,
                constructor2,
                15,
                10,
                0.0,
                true,
                false,
                false,
                40,
                null,
                null,
                null,
                95.0,
                30
        );

        RaceResult dns = buildResult(
                driver3,
                constructor1,
                null,
                null,
                0.0,
                false,
                true,
                false,
                0,
                null,
                null,
                null,
                null,
                null
        );

        RaceResult dsq = buildResult(
                driver4,
                constructor2,
                5,
                5,
                0.0,
                false,
                false,
                true,
                58,
                null,
                null,
                null,
                94.0,
                42
        );

        when(raceRepository.findById(11L))
                .thenReturn(Optional.of(race));

        when(raceResultRepository.findRaceResultsByRaceId(11L))
                .thenReturn(List.of(
                        finished,
                        dnf,
                        dns,
                        dsq
                ));

        RaceAnalyticsResponseDto response =
                raceAnalyticsService.getRaceAnalytics(11L);

        assertThat(response.getStatistics().getTotalDrivers())
                .isEqualTo(4);
        assertThat(response.getStatistics().getFinishers())
                .isEqualTo(1);
        assertThat(response.getStatistics().getDnfs())
                .isEqualTo(1);
        assertThat(response.getStatistics().getDns())
                .isEqualTo(1);
        assertThat(response.getStatistics().getDsqs())
                .isEqualTo(1);
        assertThat(response.getStatistics().getPointsScorers())
                .isEqualTo(1);
        assertThat(response.getStatistics().getTotalPoints())
                .isEqualTo(25.0);
    }

    @Test
    void shouldCalculatePodiumCorrectly() {

        RaceResult first = buildResult(
                driver1,
                constructor1,
                1,
                1,
                25.0,
                false,
                false,
                false,
                58,
                90.0,
                0.0,
                null,
                90.0,
                50
        );

        RaceResult second = buildResult(
                driver2,
                constructor2,
                2,
                2,
                18.0,
                false,
                false,
                false,
                58,
                91.0,
                1.0,
                null,
                91.0,
                48
        );

        RaceResult third = buildResult(
                driver3,
                constructor1,
                3,
                3,
                15.0,
                false,
                false,
                false,
                58,
                92.0,
                2.0,
                null,
                92.0,
                45
        );

        RaceResult fourth = buildResult(
                driver4,
                constructor2,
                4,
                4,
                12.0,
                false,
                false,
                false,
                58,
                93.0,
                3.0,
                null,
                93.0,
                40
        );

        when(raceRepository.findById(11L))
                .thenReturn(Optional.of(race));

        when(raceResultRepository.findRaceResultsByRaceId(11L))
                .thenReturn(List.of(
                        fourth,
                        third,
                        first,
                        second
                ));

        RaceAnalyticsResponseDto response =
                raceAnalyticsService.getRaceAnalytics(11L);

        assertThat(response.getPodium()).hasSize(3);

        assertThat(response.getPodium().get(0).getPosition())
                .isEqualTo(1);

        assertThat(response.getPodium().get(0).getDriverId())
                .isEqualTo(1L);

        assertThat(response.getPodium().get(1).getPosition())
                .isEqualTo(2);

        assertThat(response.getPodium().get(2).getPosition())
                .isEqualTo(3);
    }

    @Test
    void shouldCalculatePositionChangeCorrectly() {

        RaceResult result = buildResult(
                driver1,
                constructor1,
                5,
                10,
                10.0,
                false,
                false,
                false,
                58,
                90.0,
                2.0,
                null,
                90.0,
                50
        );

        when(raceRepository.findById(11L))
                .thenReturn(Optional.of(race));

        when(raceResultRepository.findRaceResultsByRaceId(11L))
                .thenReturn(List.of(result));

        RaceAnalyticsResponseDto response =
                raceAnalyticsService.getRaceAnalytics(11L);

        assertThat(response.getDriverPerformances().get(0)
                .getPositionChange())
                .isEqualTo(5);
    }

    @Test
    void shouldDetermineDriverStatusesCorrectly() {

        RaceResult dnf = buildResult(
                driver1,
                constructor1,
                15,
                10,
                0.0,
                true,
                false,
                false,
                30,
                null,
                null,
                null,
                null,
                null
        );

        RaceResult dns = buildResult(
                driver2,
                constructor2,
                null,
                null,
                0.0,
                false,
                true,
                false,
                0,
                null,
                null,
                null,
                null,
                null
        );

        RaceResult dsq = buildResult(
                driver3,
                constructor1,
                10,
                8,
                0.0,
                false,
                false,
                true,
                58,
                null,
                null,
                null,
                null,
                null
        );

        RaceResult finished = buildResult(
                driver4,
                constructor2,
                4,
                4,
                12.0,
                false,
                false,
                false,
                58,
                93.0,
                3.0,
                null,
                93.0,
                40
        );

        when(raceRepository.findById(11L))
                .thenReturn(Optional.of(race));

        when(raceResultRepository.findRaceResultsByRaceId(11L))
                .thenReturn(List.of(
                        dnf,
                        dns,
                        dsq,
                        finished
                ));

        RaceAnalyticsResponseDto response =
                raceAnalyticsService.getRaceAnalytics(11L);

        assertThat(response.getDriverPerformances())
                .extracting("status")
                .containsExactly(
                        "DNF",
                        "DNS",
                        "DSQ",
                        "FINISHED"
                );
    }

    @Test
    void shouldIdentifyFastestLapCorrectly() {

        RaceResult slower = buildResult(
                driver1,
                constructor1,
                2,
                2,
                18.0,
                false,
                false,
                false,
                58,
                91.0,
                1.0,
                null,
                91.0,
                48
        );

        RaceResult fastest = buildResult(
                driver2,
                constructor2,
                1,
                1,
                25.0,
                false,
                false,
                false,
                58,
                90.0,
                0.0,
                null,
                88.5,
                47
        );

        when(raceRepository.findById(11L))
                .thenReturn(Optional.of(race));

        when(raceResultRepository.findRaceResultsByRaceId(11L))
                .thenReturn(List.of(slower, fastest));

        RaceAnalyticsResponseDto response =
                raceAnalyticsService.getRaceAnalytics(11L);

        assertThat(response.getFastestLap()).isNotNull();
        assertThat(response.getFastestLap().getDriverId())
                .isEqualTo(2L);
        assertThat(response.getFastestLap()
                .getFastestLapTimeSeconds())
                .isEqualTo(88.5);
        assertThat(response.getFastestLap()
                .getFastestLapNumber())
                .isEqualTo(47);
    }

    private RaceResult buildResult(
            Driver driver,
            Constructor constructor,
            Integer position,
            Integer gridPosition,
            Double points,
            boolean dnf,
            boolean dns,
            boolean dsq,
            Integer laps,
            Double duration,
            Double gapSeconds,
            String gapText,
            Double fastestLap,
            Integer fastestLapNumber
    ) {
        return RaceResult.builder()
                .session(raceSession)
                .driver(driver)
                .constructor(constructor)
                .position(position)
                .gridPosition(gridPosition)
                .points(points)
                .dnf(dnf)
                .dns(dns)
                .dsq(dsq)
                .numberOfLaps(laps)
                .durationSeconds(duration)
                .gapToLeaderSeconds(gapSeconds)
                .gapToLeaderText(gapText)
                .fastestLapTimeSeconds(fastestLap)
                .fastestLapNumber(fastestLapNumber)
                .build();
    }
}