package com.gmail.tomasatrat.backend.data.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Immutable
@Table(name = "v_stock_product")
public class VStockProduct {

    @Id
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "qt_stock", nullable = false)
    private Long qtStock;

    @Column(name = "resupply_quantity", nullable = false)
    private Integer resupplyQuantity;

    @Column(name = "description", nullable = false)
    private String description;

    public Long getQtStock() {
        return qtStock;
    }

    public void setQtStock(Long qtStock) {
        this.qtStock = qtStock;
    }

    public Integer getResupplyQuantity() {
        return resupplyQuantity;
    }

    public void setResupplyQuantity(Integer resupplyQuantity) {
        this.resupplyQuantity = resupplyQuantity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
