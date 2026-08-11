package com.aditya.f1hub.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
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
public class OpenF1SessionResultDto {

    @JsonProperty("session_key")
    private Long sessionKey;

    @JsonProperty("meeting_key")
    private Long meetingKey;

    @JsonProperty("driver_number")
    private Integer driverNumber;

    private Integer position;

    private Boolean dnf;

    private Boolean dns;

    private Boolean dsq;

    /*
     * OpenF1 may return:
     *
     * - a numeric value for race/practice
     * - an array of values for qualifying
     */
    private JsonNode duration;

    /*
     * OpenF1 may return:
     *
     * - a numeric value
     * - a textual value such as "+1 LAP"
     * - an array for qualifying
     */
    @JsonProperty("gap_to_leader")
    private JsonNode gapToLeader;

    @JsonProperty("number_of_laps")
    private Integer numberOfLaps;
}