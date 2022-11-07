package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<Config, Integer> {

}
