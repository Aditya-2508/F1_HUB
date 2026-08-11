package com.aditya.f1hub.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceResultResponse {

    private Long id;

    private Long sessionId;
    private String sessionName;
    private String sessionType;

    private Long raceId;
    private String raceName;

    private Long driverId;
    private String driverName;
    private Integer driverNumber;

    private Long constructorId;
    private String constructorName;

    private Integer position;
    private Integer gridPosition;

    private Double points;

    private Boolean dnf;
    private Boolean dns;
    private Boolean dsq;

    private Double durationSeconds;

    private Double gapToLeaderSeconds;
    private String gapToLeaderText;

    private Integer numberOfLaps;

    private Double fastestLapTimeSeconds;
    private Integer fastestLapNumber;

    private Double q1TimeSeconds;
    private Double q1GapToLeaderSeconds;

    private Double q2TimeSeconds;
    private Double q2GapToLeaderSeconds;

    private Double q3TimeSeconds;
    private Double q3GapToLeaderSeconds;

    private Boolean active;
}