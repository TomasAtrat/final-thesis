package com.gmail.tomasatrat.backend.service;

import com.gmail.tomasatrat.backend.common.ICrudService;
import com.gmail.tomasatrat.backend.common.IDataEntity;
import com.gmail.tomasatrat.backend.data.entity.Branch;
import com.gmail.tomasatrat.backend.repositories.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchService implements ICrudService {

    private BranchRepository branchRepository;

    @Autowired
    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }


    @Override
    public List<Branch> findAll() {
        return this.branchRepository.findAll();
    }

    @Override
    public IDataEntity addItem(IDataEntity item) {
        return this.branchRepository.save((Branch) item);
    }

    @Override
    public Optional<Branch> findByID(Long id) {
        return this.branchRepository.findById(id);
    }

    @Override
    public void delete(IDataEntity item) {
        this.branchRepository.delete((Branch) item);
    }
}
