package com.gmail.tomasatrat.backend.microservices.reader.services;

import com.gmail.tomasatrat.backend.common.ICrudService;
import com.gmail.tomasatrat.backend.common.IDataEntity;
import com.gmail.tomasatrat.backend.data.entity.Module;
import com.gmail.tomasatrat.backend.data.entity.Reader;
import com.gmail.tomasatrat.backend.data.entity.User;
import com.gmail.tomasatrat.backend.repositories.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReaderService implements ICrudService {

    private ReaderRepository readerRepository;
    private ModuleRepository moduleRepository;

    @Autowired
    public ReaderService(ReaderRepository readerRepository, ModuleRepository moduleRepository) {
        this.readerRepository = readerRepository;
        this.moduleRepository = moduleRepository;
    }

    @Override
    public List<Reader> findAll() {
        return this.readerRepository.findAll();
    }

    @Override
    public Reader addItem(IDataEntity item) {
        Reader newReader = (Reader) item;
        newReader.setFlActive(true);
        return this.readerRepository.save(newReader);
    }

    @Override
    public Optional<Reader> findByID(Long id) {
        return this.readerRepository.findById(id);
    }

    @Override
    public void delete(IDataEntity item) {
        this.readerRepository.delete((Reader) item);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void toggleStatus(Reader reader) {
        reader.setFlActive(!reader.getFlActive());
        this.readerRepository.save(reader);
    }

    public List<Module> findAllByFlActiveIsTrue() {
        return this.moduleRepository.findAllByFlActiveIsTrue();
    }
}
