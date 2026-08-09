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
        name = "sessions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_session_external_session_id",
                        columnNames = "external_session_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_session_race_id",
                        columnList = "race_id"
                ),
                @Index(
                        name = "idx_session_type",
                        columnList = "session_type"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session extends BaseEntity {

    @Column(name = "external_session_id", nullable = false)
    @NotBlank(message = "External session ID is required.")
    @Size(max = 100)
    private String externalSessionId;

    @Column(name = "session_name", nullable = false)
    @NotBlank(message = "Session name is required.")
    @Size(max = 100)
    private String sessionName;

    @Column(name = "session_type", nullable = false)
    @NotBlank(message = "Session type is required.")
    @Size(max = 100)
    private String sessionType;

    @Column(name = "start_time", nullable = false)
    @NotNull(message = "Session start time is required.")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Builder.Default
    @Column(name = "cancelled", nullable = false)
    private Boolean cancelled = false;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "race_id",
            nullable = false
    )
    @NotNull(message = "Race is required.")
    private Race race;
}