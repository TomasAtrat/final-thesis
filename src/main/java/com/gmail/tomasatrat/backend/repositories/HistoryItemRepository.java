package com.gmail.tomasatrat.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gmail.tomasatrat.backend.data.entity.HistoryItem;

public interface HistoryItemRepository extends JpaRepository<HistoryItem, Long> {
}
