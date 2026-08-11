package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long>,
        JpaSpecificationExecutor<Driver> {

    Optional<Driver> findByExternalDriverId(String externalDriverId);

    Optional<Driver> findByAbbreviation(String abbreviation);

    Optional<Driver> findByDriverNumber(Integer driverNumber);

    boolean existsByExternalDriverId(String externalDriverId);
}