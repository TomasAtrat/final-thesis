package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.InventoryProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryProblemRepository extends JpaRepository<InventoryProblem, Long> {
    List<InventoryProblem> findByAcceptedIsFalse();
}