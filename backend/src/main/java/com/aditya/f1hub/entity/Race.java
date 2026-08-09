package com.aditya.f1hub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "races",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_race_external_meeting_id",
                        columnNames = "external_meeting_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_race_season_id",
                        columnList = "season_id"
                ),
                @Index(
                        name = "idx_race_circuit_id",
                        columnList = "circuit_id"
                ),
                @Index(
                        name = "idx_race_name",
                        columnList = "name"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Race extends BaseEntity {

    @Column(name = "external_meeting_id", nullable = false)
    @NotBlank(message = "External meeting ID is required.")
    @Size(max = 100)
    private String externalMeetingId;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Race name is required.")
    @Size(max = 150)
    private String name;

    @Column(name = "official_name")
    @Size(max = 250)
    private String officialName;

    @Column(name = "round_number")
    private Integer roundNumber;

    @Column(name = "weekend_start", nullable = false)
    @NotNull(message = "Weekend start date is required.")
    private LocalDateTime weekendStart;

    @Column(name = "weekend_end")
    private LocalDateTime weekendEnd;

    @Column(name = "location")
    @Size(max = 100)
    private String location;

    @Column(name = "country_name")
    @Size(max = 100)
    private String countryName;

    @Column(name = "country_code")
    @Size(max = 10)
    private String countryCode;

    @Column(name = "gmt_offset")
    @Size(max = 10)
    private String gmtOffset;

    @Builder.Default
    @Column(name = "cancelled", nullable = false)
    private Boolean cancelled = false;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "season_id",
            nullable = false
    )
    @NotNull(message = "Season is required.")
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "circuit_id",
            nullable = false
    )
    @NotNull(message = "Circuit is required.")
    private Circuit circuit;
}