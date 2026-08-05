package com.aditya.f1hub.dto.circuit;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CircuitRequestDto {

    @NotBlank(message = "External circuit ID is required.")
    @Size(max = 100)
    private String externalCircuitId;

    @NotBlank(message = "Circuit name is required.")
    @Size(max = 150)
    private String circuitName;

    @NotBlank(message = "Location is required.")
    @Size(max = 100)
    private String location;

    @NotBlank(message = "Country is required.")
    @Size(max = 100)
    private String country;

    @Size(max = 10)
    private String countryCode;

    @DecimalMin(value = "0.0", message = "Circuit length must be greater than zero.")
    private Double circuitLength;

    @Min(value = 1, message = "Number of turns must be at least 1.")
    private Integer numberOfTurns;

    @Size(max = 100)
    private String lapRecord;

    @Size(max = 150)
    private String lapRecordHolder;

    private Integer firstGrandPrix;

    private Double latitude;

    private Double longitude;

    @Size(max = 500)
    private String imageUrl;

    private Boolean active = true;

}