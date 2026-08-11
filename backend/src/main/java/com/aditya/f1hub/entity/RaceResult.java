package com.aditya.f1hub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "race_results",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_result_session_driver",
                        columnNames = {"session_id", "driver_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_result_session_id",
                        columnList = "session_id"
                ),
                @Index(
                        name = "idx_result_driver_id",
                        columnList = "driver_id"
                ),
                @Index(
                        name = "idx_result_constructor_id",
                        columnList = "constructor_id"
                ),
                @Index(
                        name = "idx_result_position",
                        columnList = "position"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceResult extends BaseEntity {

    /*
     * Session in which this result occurred.
     *
     * Examples:
     * - Practice 1
     * - Qualifying
     * - Sprint
     * - Race
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "session_id",
            nullable = false
    )
    @NotNull(message = "Session is required.")
    private Session session;

    /*
     * Driver associated with this result.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "driver_id",
            nullable = false
    )
    @NotNull(message = "Driver is required.")
    private Driver driver;

    /*
     * Constructor/team associated with this particular result.
     *
     * This is stored directly because a driver's constructor
     * can change over their career.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "constructor_id",
            nullable = false
    )
    @NotNull(message = "Constructor is required.")
    private Constructor constructor;

    /*
     * Final classification/position in the session.
     */
    @Column(name = "position")
    @Min(value = 1, message = "Position must be greater than zero.")
    private Integer position;

    /*
     * Starting grid position.
     */
    @Column(name = "grid_position")
    @Min(value = 1, message = "Grid position must be greater than zero.")
    private Integer gridPosition;

    /*
     * Championship/session points attributed to the driver.
     *
     * This is F1Hub-owned/derived data and is not assumed
     * to come directly from OpenF1 session_result.
     */
    @Column(name = "points")
    private Double points;

    /*
     * Driver did not finish the session.
     */
    @Builder.Default
    @Column(name = "dnf", nullable = false)
    private Boolean dnf = false;

    /*
     * Driver did not start the session.
     */
    @Builder.Default
    @Column(name = "dns", nullable = false)
    private Boolean dns = false;

    /*
     * Driver was disqualified.
     */
    @Builder.Default
    @Column(name = "dsq", nullable = false)
    private Boolean dsq = false;

    /*
     * Session duration in seconds.
     *
     * For a race this represents the driver's total race time.
     * For other session types the meaning depends on the
     * OpenF1 session type.
     */
    @Column(name = "duration_seconds")
    private Double durationSeconds;

    /*
     * Numeric gap to the session leader in seconds.
     *
     * OpenF1 may also return textual values such as "+1 LAP".
     * Those values are stored separately in gapToLeaderText.
     */
    @Column(name = "gap_to_leader_seconds")
    private Double gapToLeaderSeconds;

    /*
     * Textual gap representation when OpenF1 does not provide
     * a numeric gap, for example "+1 LAP".
     */
    @Column(name = "gap_to_leader_text", length = 50)
    private String gapToLeaderText;

    /*
     * Number of laps completed.
     */
    @Column(name = "number_of_laps")
    @Min(value = 0, message = "Number of laps cannot be negative.")
    private Integer numberOfLaps;

    /*
     * Fastest lap time in seconds.
     *
     * Derived from OpenF1 lap data.
     */
    @Column(name = "fastest_lap_time_seconds")
    private Double fastestLapTimeSeconds;

    /*
     * Lap number on which the fastest lap was recorded.
     */
    @Column(name = "fastest_lap_number")
    @Min(value = 1, message = "Fastest lap number must be greater than zero.")
    private Integer fastestLapNumber;

    /*
     * Qualifying Q1 best lap time in seconds.
     */
    @Column(name = "q1_time_seconds")
    private Double q1TimeSeconds;

    /*
     * Qualifying Q2 best lap time in seconds.
     */
    @Column(name = "q2_time_seconds")
    private Double q2TimeSeconds;

    /*
     * Qualifying Q3 best lap time in seconds.
     */
    @Column(name = "q3_time_seconds")
    private Double q3TimeSeconds;

    /*
     * Whether this result is currently active.
     */
    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;


    @Column(name = "q1_gap_to_leader_seconds")
    private Double q1GapToLeaderSeconds;

    @Column(name = "q2_gap_to_leader_seconds")
    private Double q2GapToLeaderSeconds;

    @Column(name = "q3_gap_to_leader_seconds")
    private Double q3GapToLeaderSeconds;
}