package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Circuit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CircuitRepository extends
        JpaRepository<Circuit, Long>,
        JpaSpecificationExecutor<Circuit> {

    /**
     * Find Circuit by External Circuit ID
     */
    Optional<Circuit> findByExternalCircuitId(String externalCircuitId);

    /**
     * Check if External Circuit ID already exists
     */
    boolean existsByExternalCircuitId(String externalCircuitId);

}