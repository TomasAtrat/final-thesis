package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.VProductsSoldByHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VProductsSoldByHourRepository extends JpaRepository<VProductsSoldByHour, String> {
    List<VProductsSoldByHour> findAllByBranchId(Long branchId);
}