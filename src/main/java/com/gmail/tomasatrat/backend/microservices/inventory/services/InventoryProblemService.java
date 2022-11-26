package com.gmail.tomasatrat.backend.microservices.inventory.services;

import com.gmail.tomasatrat.backend.data.entity.Branch;
import com.gmail.tomasatrat.backend.data.entity.InventoryDetail;
import com.gmail.tomasatrat.backend.data.entity.InventoryProblem;
import com.gmail.tomasatrat.backend.data.entity.Stock;
import com.gmail.tomasatrat.backend.repositories.InventoryDetailRepository;
import com.gmail.tomasatrat.backend.repositories.InventoryProblemRepository;
import com.gmail.tomasatrat.backend.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryProblemService {
    private InventoryProblemRepository inventoryProblemRepository;
    private InventoryDetailRepository inventoryDetailRepository;
    private StockRepository stockRepository;

    @Autowired
    InventoryProblemService(InventoryProblemRepository inventoryProblemRepository,
                            InventoryDetailRepository inventoryDetailRepository,
                            StockRepository stockRepository){
        this.inventoryProblemRepository = inventoryProblemRepository;
        this.inventoryDetailRepository = inventoryDetailRepository;
        this.stockRepository = stockRepository;
    }

    public List<InventoryProblem> getNotAcceptedProblems(){
        return this.inventoryProblemRepository.findByAcceptedIsFalse();
    }

    public void acceptDifference(InventoryProblem inventoryProblem){
        inventoryProblem.setAccepted(true);
        this.inventoryProblemRepository.save(inventoryProblem);
    }

    public void updateInventoryDetailAcceptedQty(InventoryDetail detail) {
        detail.setAcceptedQty(Math.abs(detail.getSupposedQty() - detail.getReadQty()));
        this.inventoryDetailRepository.save(detail);
    }

    public void updateStockAfterInventoryProblemAccepted(InventoryDetail detail) {
        var branch = new Branch();
        branch.setId(detail.getInventory().getUserAssigned().getIdBranch());
        Stock stock = this.stockRepository.findByBarcodeBarcodeAndBranch(detail.getBarcode(), branch);
        stock.setQtStock((long) (detail.getReadQty() + detail.getAcceptedQty()));
        this.stockRepository.save(stock);
    }
}
