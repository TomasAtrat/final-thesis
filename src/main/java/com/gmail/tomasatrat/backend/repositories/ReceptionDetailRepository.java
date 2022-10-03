package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.ReceptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceptionDetailRepository extends JpaRepository<ReceptionDetail, Long> {
}