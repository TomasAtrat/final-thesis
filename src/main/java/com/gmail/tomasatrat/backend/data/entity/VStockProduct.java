package com.gmail.tomasatrat.backend.data.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Mapping for DB view
 */
@Entity
@Immutable
@Table(name = "v_stock_product")
public class VStockProduct {
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "qt_stock", nullable = false, precision = 41)
    private BigDecimal qtStock;

    @Column(name = "resupply_quantity")
    private Integer resupplyQuantity;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getQtStock() {
        return qtStock;
    }

    public Integer getResupplyQuantity() {
        return resupplyQuantity;
    }

    protected VStockProduct() {
    }
}