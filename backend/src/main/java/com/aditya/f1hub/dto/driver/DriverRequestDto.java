package com.aditya.f1hub.dto.driver;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestDto {

    @NotBlank(message = "External driver ID is required.")
    private String externalDriverId;

    @NotBlank(message = "First name is required.")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "Full name is required.")
    @Size(max = 120)
    private String fullName;

    @NotNull(message = "Driver number is required.")
    @Min(0)
    private Integer driverNumber;

    @NotBlank(message = "Abbreviation is required.")
    @Size(min = 3, max = 3)
    private String abbreviation;

    @NotBlank(message = "Nationality is required.")
    private String nationality;

    @NotNull(message = "Date of birth is required.")
    @Past
    private LocalDate dateOfBirth;

    private String profileImageUrl;

    @Min(0)
    private Integer permanentNumber;

    @Builder.Default
    private Boolean active = true;
}