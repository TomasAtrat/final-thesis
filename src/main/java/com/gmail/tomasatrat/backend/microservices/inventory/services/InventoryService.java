package com.gmail.tomasatrat.backend.microservices.inventory.services;

import com.gmail.tomasatrat.backend.common.ICrudService;
import com.gmail.tomasatrat.backend.common.IDataEntity;
import com.gmail.tomasatrat.backend.data.entity.Barcode;
import com.gmail.tomasatrat.backend.data.entity.Inventory;
import com.gmail.tomasatrat.backend.data.entity.InventoryDetail;
import com.gmail.tomasatrat.backend.data.entity.Stock;
import com.gmail.tomasatrat.backend.repositories.BarcodeRepository;
import com.gmail.tomasatrat.backend.repositories.InventoryDetailRepository;
import com.gmail.tomasatrat.backend.repositories.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService implements ICrudService {
    private final InventoryRepository inventoryRepository;
    private final InventoryDetailRepository inventoryDetailRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, InventoryDetailRepository inventoryDetailRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryDetailRepository = inventoryDetailRepository;
    }

    @Override
    public List<Inventory> findAll() {
        return this.inventoryRepository.findAll();
    }

    public void addItem(IDataEntity item, List<Stock> details) {
        Inventory newInventory = (Inventory) item;
        newInventory.setAddDate(new Date(System.currentTimeMillis()));
        newInventory.setActive(true);

        this.inventoryRepository.save(newInventory);

        for (Stock detail : details) {
            InventoryDetail inventoryDetail = new InventoryDetail();
            inventoryDetail.setInventory(newInventory);
            inventoryDetail.setSupposedQty(Math.toIntExact(detail.getQtStock()));
            inventoryDetail.setBarcode(detail.getBarcodeBarcode());

            this.inventoryDetailRepository.save(inventoryDetail);
        }
    }

    @Override
    public IDataEntity addItem(IDataEntity item) {
        return null;
    }

    @Override
    public <T> Optional<T> findByID(Long id) {
        return Optional.empty();
    }

    @Override
    public void delete(IDataEntity item) {
        this.inventoryRepository.delete((Inventory) item);
    }
}
