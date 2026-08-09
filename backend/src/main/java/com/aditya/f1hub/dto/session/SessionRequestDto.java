package com.aditya.f1hub.dto.session;

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
public class SessionRequestDto {

    @NotBlank(message = "External session ID is required.")
    @Size(max = 100)
    private String externalSessionId;

    @NotBlank(message = "Session name is required.")
    @Size(max = 100)
    private String sessionName;

    @NotBlank(message = "Session type is required.")
    @Size(max = 100)
    private String sessionType;

    @NotNull(message = "Session start time is required.")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Boolean cancelled = false;

    private Boolean active = true;

    @NotNull(message = "Race ID is required.")
    private Long raceId;
}