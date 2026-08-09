package com.aditya.f1hub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "seasons")
public class Season extends BaseEntity {

    @Column(name = "year", nullable = false, unique = true)
    private Integer year;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}