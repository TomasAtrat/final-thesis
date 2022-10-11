package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.Preparation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreparationRepository extends JpaRepository<Preparation, Long> {
    List<Preparation> findAllByIsFinishedFalse();

    List<Preparation> findAllByIsFinishedTrueAndIsShippedFalse();


}