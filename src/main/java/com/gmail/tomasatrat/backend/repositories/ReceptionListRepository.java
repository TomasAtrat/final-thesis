package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.ReceptionList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceptionListRepository extends JpaRepository<ReceptionList, Long> {
}