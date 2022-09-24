package com.gmail.tomasatrat.backend.microservices.reception.services;

import com.gmail.tomasatrat.backend.data.entity.ReceptionDetail;
import com.gmail.tomasatrat.backend.data.entity.ReceptionProblem;
import com.gmail.tomasatrat.backend.repositories.ReceptionDetailRepository;
import com.gmail.tomasatrat.backend.repositories.ReceptionProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceptionService {
    private ReceptionProblemRepository receptionProblemRepository;
    private ReceptionDetailRepository receptionDetailRepository;

    @Autowired
    ReceptionService(ReceptionProblemRepository receptionProblemRepository,
                     ReceptionDetailRepository receptionDetailRepository){
        this.receptionProblemRepository = receptionProblemRepository;
        this.receptionDetailRepository = receptionDetailRepository;
    }

    public List<ReceptionProblem> getNotAcceptedProblems(){
        return this.receptionProblemRepository.findByAcceptedIsFalse();
    }

    public void acceptDifference(ReceptionProblem inventoryProblem){
        inventoryProblem.setAccepted(true);
        this.receptionProblemRepository.save(inventoryProblem);
    }

    public void updateReceptionDetailAcceptedQty(ReceptionDetail detail) {
        detail.setAcceptedQty(Math.abs(detail.getScheduledQty() - detail.getReceivedQty()));
        this.receptionDetailRepository.save(detail);
    }
}
