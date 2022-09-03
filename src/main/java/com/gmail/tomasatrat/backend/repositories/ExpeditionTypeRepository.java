package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.ExpeditionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpeditionTypeRepository extends JpaRepository<ExpeditionType, Long> {
}