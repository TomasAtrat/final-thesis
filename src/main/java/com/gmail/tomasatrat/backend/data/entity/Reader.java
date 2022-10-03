package com.gmail.tomasatrat.backend.data.entity;

import com.gmail.tomasatrat.backend.common.IDataEntity;

import javax.persistence.*;

@Entity
@Table(name = "reader")
public class Reader extends AbstractEntity implements IDataEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "alias")
    private String alias;

    @Column(name = "flActive")
    private Boolean flActive;

    @Column(name = "antenaPower")
    private float antenaPower;

    @Column(name = "RSSI")
    private float RSSI;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id")
    private Module moduleId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getFlActive() {
        return flActive;
    }

    public void setFlActive(Boolean flActive) {
        this.flActive = flActive;
    }

    public float getAntenaPower() {
        return antenaPower;
    }

    public void setAntenaPower(float antenaPower) {
        this.antenaPower = antenaPower;
    }

    public float getRSSI() {
        return RSSI;
    }

    public void setRSSI(float RSSI) {
        this.RSSI = RSSI;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setModuleId(Module moduleId) {
        this.moduleId = moduleId;
    }

    public Module getModuleId() {
        return moduleId;
    }
}
