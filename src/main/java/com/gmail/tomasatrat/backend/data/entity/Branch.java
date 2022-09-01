package com.gmail.tomasatrat.backend.data.entity;

import com.gmail.tomasatrat.backend.common.IDataEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "branch")
public class Branch implements IDataEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "description", nullable = false, length = 128)
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}