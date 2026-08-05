package com.aditya.f1hub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "circuits",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_circuit_external_id",
                        columnNames = "external_circuit_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Circuit extends BaseEntity {

    @Column(name = "external_circuit_id", nullable = false)
    @NotBlank(message = "External circuit ID is required.")
    @Size(max = 100)
    private String externalCircuitId;

    @Column(name = "circuit_name", nullable = false)
    @NotBlank(message = "Circuit name is required.")
    @Size(max = 150)
    private String circuitName;

    @Column(nullable = false)
    @NotBlank(message = "Location is required.")
    @Size(max = 100)
    private String location;

    @Column(nullable = false)
    @NotBlank(message = "Country is required.")
    @Size(max = 100)
    private String country;

    @Column(name = "country_code")
    @Size(max = 10)
    private String countryCode;

    @Column(name = "circuit_length")
    @DecimalMin(value = "0.0", message = "Circuit length must be greater than zero.")
    private Double circuitLength;

    @Column(name = "number_of_turns")
    @Min(value = 1, message = "Number of turns must be at least 1.")
    private Integer numberOfTurns;

    @Column(name = "lap_record")
    @Size(max = 100)
    private String lapRecord;

    @Column(name = "lap_record_holder")
    @Size(max = 150)
    private String lapRecordHolder;

    @Column(name = "first_grand_prix")
    private Integer firstGrandPrix;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

}