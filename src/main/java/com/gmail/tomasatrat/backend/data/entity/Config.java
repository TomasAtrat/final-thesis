package com.gmail.tomasatrat.backend.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "config")
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "module_to_show")
    private String moduleToShow;

    public String getModuleToShow() {
        return moduleToShow;
    }
}
