package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.Preparation;
import com.gmail.tomasatrat.backend.data.entity.PreparationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreparationDetailRepository extends JpaRepository<PreparationDetail, Long> {
    List<PreparationDetail> findAllByPreparation(Preparation preparation);
}