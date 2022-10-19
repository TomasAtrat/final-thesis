package com.gmail.tomasatrat.backend.microservices.purchase.services;

import com.gmail.tomasatrat.backend.data.entity.VProductsSoldByCategory;
import com.gmail.tomasatrat.backend.data.entity.VProductsSoldByHour;
import com.gmail.tomasatrat.backend.repositories.VProductsSoldByCategoryRepository;
import com.gmail.tomasatrat.backend.repositories.VProductsSoldByHourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseService {

    private VProductsSoldByHourRepository vProductsSoldByHourRepository;
    private VProductsSoldByCategoryRepository vProductsSoldByCategoryRepository;

    @Autowired
    public PurchaseService(VProductsSoldByHourRepository vProductsSoldByHourRepository,
                           VProductsSoldByCategoryRepository vProductsSoldByCategoryRepository) {
        this.vProductsSoldByHourRepository = vProductsSoldByHourRepository;
        this.vProductsSoldByCategoryRepository = vProductsSoldByCategoryRepository;
    }

    public List<VProductsSoldByHour> getHoursWithMoreSellsByBranch(Long branchId){
        return vProductsSoldByHourRepository.findAllByBranchId(branchId);
    }

    public List<VProductsSoldByCategory> getProductsSoldByCategoryAndBranch(Long branchId){
        return vProductsSoldByCategoryRepository.findAllByBranchId(branchId);
    }
}
