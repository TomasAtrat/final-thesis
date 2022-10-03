package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.ReceptionProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceptionProblemRepository extends JpaRepository<ReceptionProblem, Long> {
    List<ReceptionProblem> findByAcceptedIsFalse();
}