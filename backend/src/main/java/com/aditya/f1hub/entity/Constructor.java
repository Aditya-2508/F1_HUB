package com.aditya.f1hub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
        name = "constructors",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_constructor_external_id",
                        columnNames = "external_constructor_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Constructor extends BaseEntity {

    @Column(name = "external_constructor_id", nullable = false, unique = true)
    @NotBlank(message = "External constructor ID is required.")
    @Size(max = 100)
    private String externalConstructorId;

    @Column(nullable = false)
    @NotBlank(message = "Constructor name is required.")
    @Size(max = 100)
    private String name;

    @Column(name = "full_name")
    @Size(max = 150)
    private String fullName;

    @Column(nullable = false)
    @NotBlank(message = "Nationality is required.")
    @Size(max = 100)
    private String nationality;

    @Column(name = "country_code")
    @Size(max = 10)
    private String countryCode;

    @Column(name = "team_colour")
    @Size(max = 20)
    private String teamColour;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

}