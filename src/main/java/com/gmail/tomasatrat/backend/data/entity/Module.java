package com.gmail.tomasatrat.backend.data.entity;

import com.gmail.tomasatrat.backend.common.IDataEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "module")
public class Module extends AbstractEntity implements IDataEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "flActive")
    private Boolean flActive;
}
