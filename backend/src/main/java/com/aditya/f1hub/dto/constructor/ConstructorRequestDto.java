package com.aditya.f1hub.dto.constructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConstructorRequestDto {

    @NotBlank(message = "External constructor ID is required.")
    @Size(max = 100)
    private String externalConstructorId;

    @NotBlank(message = "Constructor name is required.")
    @Size(max = 100)
    private String name;

    @Size(max = 150)
    private String fullName;

    @NotBlank(message = "Nationality is required.")
    @Size(max = 100)
    private String nationality;

    @Size(max = 10)
    private String countryCode;

    @Size(max = 20)
    private String teamColour;

    @Size(max = 500)
    private String logoUrl;

    private Boolean active = true;

}