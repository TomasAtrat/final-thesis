package com.gmail.tomasatrat.backend.data.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Mapping for DB view
 */
@Entity
@Immutable
@Table(name = "v_top20_most_tested_products")
public class VTop20MostTestedProduct {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "product_code", nullable = false)
    private String productCode;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "qty_tested", nullable = false)
    private Long qtyTested;

    public String getId() {
        return id;
    }

    public String getProductCode() {
        return productCode;
    }

    public Long getBranchId() {
        return branchId;
    }

    public Long getQtyTested() {
        return qtyTested;
    }

    protected VTop20MostTestedProduct() {
    }
}