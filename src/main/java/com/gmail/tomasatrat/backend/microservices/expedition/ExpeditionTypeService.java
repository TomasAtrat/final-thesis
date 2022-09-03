package com.gmail.tomasatrat.backend.microservices.expedition;

import com.gmail.tomasatrat.backend.data.entity.ExpeditionType;
import com.gmail.tomasatrat.backend.repositories.ExpeditionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpeditionTypeService {
    private ExpeditionTypeRepository expeditionTypeRepository;

    @Autowired
    public ExpeditionTypeService(ExpeditionTypeRepository expeditionTypeRepository){
        this.expeditionTypeRepository = expeditionTypeRepository;
    }

    public Optional<ExpeditionType> getExpeditionType(Long id){
        return this.expeditionTypeRepository.findById(id);
    }

    public List<ExpeditionType> getExpeditionTypes(){
        return this.expeditionTypeRepository.findAll();
    }
}
