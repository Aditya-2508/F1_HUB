package com.aditya.f1hub.dto.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponseDto {

    private Long id;

    private String externalDriverId;

    private String firstName;

    private String lastName;

    private String fullName;

    private Integer driverNumber;

    private String abbreviation;

    private String nationality;

    private LocalDate dateOfBirth;

    private String profileImageUrl;

    private Integer permanentNumber;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}