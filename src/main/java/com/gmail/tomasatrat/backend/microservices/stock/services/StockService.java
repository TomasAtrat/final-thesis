package com.gmail.tomasatrat.backend.microservices.stock.services;

import com.gmail.tomasatrat.backend.common.ICrudService;
import com.gmail.tomasatrat.backend.common.IDataEntity;
import com.gmail.tomasatrat.backend.data.entity.Reader;
import com.gmail.tomasatrat.backend.data.entity.Stock;
import com.gmail.tomasatrat.backend.repositories.ModuleRepository;
import com.gmail.tomasatrat.backend.repositories.ReaderRepository;
import com.gmail.tomasatrat.backend.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockService implements ICrudService {

    private StockRepository stockRepository;

    @Autowired
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public List<Stock> findAll() {
        return this.stockRepository.findAll();
    }

    @Override
    public Stock addItem(IDataEntity item) {
        return this.stockRepository.save((Stock) item);
    }

    @Override
    public Optional<Stock> findByID(Long id) {
        return this.stockRepository.findById(id);
    }

    @Override
    public void delete(IDataEntity item) {
        this.stockRepository.delete((Stock) item);
    }
}
