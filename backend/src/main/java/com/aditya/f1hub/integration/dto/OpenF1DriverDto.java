package com.aditya.f1hub.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OpenF1DriverDto {

    @JsonProperty("driver_number")
    private Integer driverNumber;

    @JsonProperty("broadcast_name")
    private String broadcastName;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("name_acronym")
    private String abbreviation;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("team_colour")
    private String teamColour;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("headshot_url")
    private String headshotUrl;

    @JsonProperty("country_code")
    private String countryCode;

}