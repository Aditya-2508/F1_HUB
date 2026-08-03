package com.aditya.f1hub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "drivers")
//@EntityListeners(AuditingEntityListener.class)
public class Driver extends BaseEntity{



    @Column(name = "external_driver_id", unique = true, nullable = false, length = 100)
    @NotBlank(message = "External driver ID is required.")
    private String externalDriverId;

    @Column(name = "first_name", nullable = false, length = 50)
    @NotBlank(message = "First name is required.")
    @Size(max = 50, message = "First name cannot exceed 50 characters.")
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    @NotBlank(message = "Last name is required.")
    @Size(max = 50, message = "Last name cannot exceed 50 characters.")
    private String lastName;

    @Column(name = "full_name", nullable = false, length = 120)
    @NotBlank(message = "Full name is required.")
    @Size(max = 120, message = "Full name cannot exceed 120 characters.")
    private String fullName;

    @Column(name = "driver_number", nullable = false)
    @NotNull(message = "Driver number is required.")
    @Min(value = 0, message = "Driver number cannot be negative.")
    private Integer driverNumber;

    @Column(name = "abbreviation", nullable = false, length = 3)
    @NotBlank(message = "Driver abbreviation is required.")
    @Size(min = 3, max = 3, message = "Abbreviation must contain exactly 3 characters.")
    private String abbreviation;

    @Column(name = "nationality", nullable = false, length = 50)
    @NotBlank(message = "Nationality is required.")
    @Size(max = 50, message = "Nationality cannot exceed 50 characters.")
    private String nationality;

    @Column(name = "date_of_birth", nullable = false)
    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be in the past.")
    private LocalDate dateOfBirth;

    @Column(name = "profile_image_url", length = 500)
    @Size(max = 500, message = "Profile image URL cannot exceed 500 characters.")
    private String profileImageUrl;

    @Column(name = "permanent_number")
    @Min(value = 0, message = "Permanent number cannot be negative.")
    private Integer permanentNumber;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}