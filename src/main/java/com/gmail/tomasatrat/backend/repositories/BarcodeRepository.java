package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.Barcode;
import com.gmail.tomasatrat.backend.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarcodeRepository extends JpaRepository<Barcode, String> {
    List<Barcode> findByProductCode(Product product);
}