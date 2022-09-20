package com.gmail.tomasatrat.backend.microservices.reader.services;

import com.gmail.tomasatrat.backend.common.ICrudService;
import com.gmail.tomasatrat.backend.common.IDataEntity;
import com.gmail.tomasatrat.backend.data.entity.Reader;
import com.gmail.tomasatrat.backend.repositories.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReaderService implements ICrudService {

    private ReaderRepository readerRepository;

    @Autowired
    public ReaderService(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    @Override
    public List<Reader> findAll() {
        return this.readerRepository.findAll();
    }

    @Override
    public void addItem(IDataEntity item) {
        Reader newReader = (Reader) item;
        newReader.setFlActive(true);
        this.readerRepository.save(newReader);
    }

    @Override
    public Optional<Reader> findByID(Long id) {
        return this.readerRepository.findById(id);
    }

    @Override
    public void delete(IDataEntity item) {
        this.readerRepository.delete((Reader) item);
    }
}
