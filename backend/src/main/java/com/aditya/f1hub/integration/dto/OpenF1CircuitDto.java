package com.aditya.f1hub.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenF1CircuitDto {

    @JsonProperty("circuit_key")
    private Integer circuitKey;

    @JsonProperty("circuit_short_name")
    private String circuitShortName;

    @JsonProperty("location")
    private String location;

    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("circuit_image")
    private String circuitImage;
}