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
        name = "constructor_standings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_constructor_standing_season_constructor",
                        columnNames = {"season_id", "constructor_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_constructor_standing_season_id",
                        columnList = "season_id"
                ),
                @Index(
                        name = "idx_constructor_standing_constructor_id",
                        columnList = "constructor_id"
                ),
                @Index(
                        name = "idx_constructor_standing_position",
                        columnList = "position"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstructorStanding extends BaseEntity {

    /*
     * Championship season to which this standing belongs.
     *
     * Historical standings are supported because the same
     * constructor can have one standing for each season.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "season_id",
            nullable = false
    )
    @NotNull(message = "Season is required.")
    private Season season;

    /*
     * Constructor associated with this championship standing.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "constructor_id",
            nullable = false
    )
    @NotNull(message = "Constructor is required.")
    private Constructor constructor;

    /*
     * Final/current championship position for the season.
     */
    @Column(name = "position", nullable = false)
    @NotNull(message = "Championship position is required.")
    @Min(value = 1, message = "Championship position must be greater than zero.")
    private Integer position;

    /*
     * Total championship points accumulated during the season.
     *
     * Points are F1Hub-owned/derived business data.
     */
    @Column(name = "points", nullable = false)
    @NotNull(message = "Championship points are required.")
    @Min(value = 0, message = "Championship points cannot be negative.")
    private Double points;

    /*
     * Number of championship race wins during the season.
     */
    @Column(name = "wins", nullable = false)
    @NotNull(message = "Number of wins is required.")
    @Min(value = 0, message = "Number of wins cannot be negative.")
    private Integer wins;
}