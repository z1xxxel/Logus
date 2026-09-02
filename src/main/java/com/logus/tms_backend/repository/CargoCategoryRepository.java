package com.logus.tms_backend.repository;

import com.logus.tms_backend.model.CargoCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CargoCategoryRepository extends JpaRepository<CargoCategory, Long> {

    Optional<CargoCategory> findByName(String name);

    boolean existsByName(String name);
}