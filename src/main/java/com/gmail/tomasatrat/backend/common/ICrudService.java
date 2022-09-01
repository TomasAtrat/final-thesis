package com.gmail.tomasatrat.backend.common;

import com.gmail.tomasatrat.backend.data.entity.Branch;

import java.util.List;
import java.util.Optional;

public interface ICrudService {
    <T> List<T> findAll();

    void addItem(IDataEntity item);

    <T> Optional<T> findByID(Long id);

    void delete(IDataEntity item);
}
