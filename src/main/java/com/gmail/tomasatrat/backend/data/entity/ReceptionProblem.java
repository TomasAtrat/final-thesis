package com.gmail.tomasatrat.backend.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "reception_problem")
public class ReceptionProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "difference")
    private Integer difference;

    @Column(name = "description")
    private String description;

    @Column(name = "accepted")
    private Boolean accepted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "detail_id")
    private ReceptionDetail detail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getDifference() {
        return difference;
    }

    public void setDifference(Integer difference) {
        this.difference = difference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public ReceptionDetail getDetail() {
        return detail;
    }

    public void setDetail(ReceptionDetail detail) {
        this.detail = detail;
    }

}