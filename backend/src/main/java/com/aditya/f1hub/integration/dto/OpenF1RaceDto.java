package com.aditya.f1hub.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpenF1RaceDto {

    @JsonProperty("meeting_key")
    private String meetingKey;

    @JsonProperty("meeting_name")
    private String meetingName;

    @JsonProperty("meeting_official_name")
    private String meetingOfficialName;

    private Integer year;

    @JsonProperty("circuit_key")
    private String circuitKey;

    @JsonProperty("date_start")
    private String dateStart;

    @JsonProperty("date_end")
    private String dateEnd;

    private String location;

    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("gmt_offset")
    private String gmtOffset;

    @JsonProperty("is_cancelled")
    private Boolean cancelled;
}