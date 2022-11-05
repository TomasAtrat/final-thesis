package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.VTop20MostTestedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VTop20MostTestedProductRepository extends JpaRepository<VTop20MostTestedProduct, String> {
    List<VTop20MostTestedProduct> findTop20ByBranchId(Long id);
}