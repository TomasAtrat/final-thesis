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
@Table(name = "v_products_sold_by_hour")
public class VProductsSoldByHour {
    @Id
    @Column(name = "uuid", length = 36)
    private String uuid;

    @Column(name = "average")
    private Double average;

    @Column(name = "time", length = 13)
    private String time;

    @Column(name = "branch_id")
    private Long branchId;

    public String getUuid() {
        return uuid;
    }

    public Double getAverage() {
        return average;
    }

    public String getTime() {
        return time;
    }

    public Long getBranchId() {
        return branchId;
    }

    protected VProductsSoldByHour() {
    }
}