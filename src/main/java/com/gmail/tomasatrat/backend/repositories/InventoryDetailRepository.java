package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.InventoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryDetailRepository extends JpaRepository<InventoryDetail, Long> {
}