package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.Inventory;
import com.gmail.tomasatrat.backend.data.entity.InventoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryDetailRepository extends JpaRepository<InventoryDetail, Long> {
    List<InventoryDetail> findAllByInventory(Inventory inventory);
}