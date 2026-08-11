package com.aditya.f1hub.dto.result;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class RaceResultRequest {

    @NotNull(message = "Session ID is required.")
    @Min(value = 1, message = "Session ID must be greater than zero.")
    private Long sessionId;

    @NotNull(message = "Driver ID is required.")
    @Min(value = 1, message = "Driver ID must be greater than zero.")
    private Long driverId;

    @NotNull(message = "Constructor ID is required.")
    @Min(value = 1, message = "Constructor ID must be greater than zero.")
    private Long constructorId;

    @Min(value = 1, message = "Position must be greater than zero.")
    private Integer position;

    @Min(value = 1, message = "Grid position must be greater than zero.")
    private Integer gridPosition;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Points cannot be negative."
    )
    private Double points;

    private Boolean dnf;

    private Boolean dns;

    private Boolean dsq;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Duration cannot be negative."
    )
    private Double durationSeconds;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Gap to leader cannot be negative."
    )
    private Double gapToLeaderSeconds;

    private String gapToLeaderText;

    @Min(value = 0, message = "Number of laps cannot be negative.")
    private Integer numberOfLaps;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Fastest lap time cannot be negative."
    )
    private Double fastestLapTimeSeconds;

    @Min(value = 1, message = "Fastest lap number must be greater than zero.")
    private Integer fastestLapNumber;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Q1 time cannot be negative."
    )
    private Double q1TimeSeconds;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Q1 gap to leader cannot be negative."
    )
    private Double q1GapToLeaderSeconds;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Q2 time cannot be negative."
    )
    private Double q2TimeSeconds;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Q2 gap to leader cannot be negative."
    )
    private Double q2GapToLeaderSeconds;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Q3 time cannot be negative."
    )
    private Double q3TimeSeconds;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Q3 gap to leader cannot be negative."
    )
    private Double q3GapToLeaderSeconds;

    private Boolean active;
}