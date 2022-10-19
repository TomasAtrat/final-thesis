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
@Table(name = "v_products_sold_by_category")
public class VProductsSoldByCategory {
    @Id
    @Column(name = "uuid", length = 36)
    private String uuid;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "sold_qty", nullable = false)
    private Long soldQty;

    @Column(name = "category")
    private String category;

    @Column(name = "amount_earned")
    private Double amountEarned;

    @Column(name = "branch_id")
    private Long branchId;

    public String getUuid() {
        return uuid;
    }

    public String getCode() {
        return code;
    }

    public Long getSoldQty() {
        return soldQty;
    }

    public String getCategory() {
        return category;
    }

    public Double getAmountEarned() {
        return amountEarned;
    }

    public Long getBranchId() {
        return branchId;
    }

    protected VProductsSoldByCategory() {
    }
}