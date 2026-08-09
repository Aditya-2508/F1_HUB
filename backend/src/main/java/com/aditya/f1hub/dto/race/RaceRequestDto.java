package com.aditya.f1hub.dto.race;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RaceRequestDto {

    @NotBlank(message = "External meeting ID is required.")
    @Size(max = 100)
    private String externalMeetingId;

    @NotBlank(message = "Race name is required.")
    @Size(max = 150)
    private String name;

    @Size(max = 250)
    private String officialName;

    @Min(value = 1, message = "Round number must be at least 1.")
    private Integer roundNumber;

    @NotNull(message = "Weekend start date is required.")
    private LocalDateTime weekendStart;

    private LocalDateTime weekendEnd;

    @Size(max = 100)
    private String location;

    @Size(max = 100)
    private String countryName;

    @Size(max = 10)
    private String countryCode;

    @Size(max = 10)
    private String gmtOffset;

    private Boolean cancelled = false;

    private Boolean active = true;

    @NotNull(message = "Season ID is required.")
    private Long seasonId;

    @NotNull(message = "Circuit ID is required.")
    private Long circuitId;
}