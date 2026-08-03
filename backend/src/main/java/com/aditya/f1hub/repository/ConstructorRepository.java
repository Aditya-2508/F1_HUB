package com.aditya.f1hub.repository;

import com.aditya.f1hub.entity.Constructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ConstructorRepository extends JpaRepository<Constructor, Long>,
        JpaSpecificationExecutor<Constructor> {

    Optional<Constructor> findByExternalConstructorId(String externalConstructorId);

    Optional<Constructor> findByName(String name);

    boolean existsByExternalConstructorId(String externalConstructorId);

}