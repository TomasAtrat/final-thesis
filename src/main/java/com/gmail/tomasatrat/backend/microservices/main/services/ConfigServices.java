package com.gmail.tomasatrat.backend.microservices.main.services;

import com.gmail.tomasatrat.backend.data.entity.Config;
import com.gmail.tomasatrat.backend.repositories.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigServices {

    private final ConfigRepository configRepository;

    @Autowired
    public ConfigServices(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public List<Config> findAll() {
        return this.configRepository.findAll();
    }

}
