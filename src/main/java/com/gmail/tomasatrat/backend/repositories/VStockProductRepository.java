package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.VStockProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface VStockProductRepository extends JpaRepository<VStockProduct, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM v_stock_product")
    Collection<VStockProduct> findStockProduct();
}
