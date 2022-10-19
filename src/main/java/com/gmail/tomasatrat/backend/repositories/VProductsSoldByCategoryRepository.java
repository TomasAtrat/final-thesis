package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.VProductsSoldByCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VProductsSoldByCategoryRepository extends JpaRepository<VProductsSoldByCategory, String> {
    List<VProductsSoldByCategory> findAllByBranchId(Long branchId);
}