package com.gmail.tomasatrat.backend.microservices.preparation.services;

import com.gmail.tomasatrat.backend.data.entity.Preparation;
import com.gmail.tomasatrat.backend.data.entity.PreparationDetail;
import com.gmail.tomasatrat.backend.repositories.PreparationDetailRepository;
import com.gmail.tomasatrat.backend.repositories.PreparationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreparationService {

    private PreparationRepository preparationRepository;
    private PreparationDetailRepository preparationDetailRepository;

    public PreparationService(PreparationRepository preparationRepository,
                              PreparationDetailRepository preparationDetailRepository){
        this.preparationRepository = preparationRepository;
        this.preparationDetailRepository = preparationDetailRepository;
    }

    public List<Preparation> getAllPreparationsNotFinished(){
        return this.preparationRepository.findAllByIsFinishedTrueAndIsShippedFalse();
    }

    public void updatePreparation(Preparation preparation){
        preparationRepository.save(preparation);
    }

    public List<PreparationDetail> getDetailsByPreparation(Preparation preparation){
        return preparationDetailRepository.findAllByPreparation(preparation);
    }
}
