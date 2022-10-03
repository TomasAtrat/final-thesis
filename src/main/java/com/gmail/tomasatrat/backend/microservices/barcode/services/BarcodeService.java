package com.gmail.tomasatrat.backend.microservices.barcode.services;

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

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BarcodeService {

    private BarcodeRepository barcodeRepository;

    @Autowired
    public BarcodeService(BarcodeRepository barcodeRepository) {
        this.barcodeRepository = barcodeRepository;
    }

    public List<Barcode> findAll() {
        return this.barcodeRepository.findAll();
    }

    public Barcode addItem(IDataEntity item) {
        return this.barcodeRepository.save((Barcode) item);
    }

    public Optional<Barcode> findByID(String id) {
        return this.barcodeRepository.findById(id);
    }

    public void delete(IDataEntity item) {
        this.barcodeRepository.delete((Barcode) item);
    }
}
