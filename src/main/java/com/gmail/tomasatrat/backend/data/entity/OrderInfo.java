package com.gmail.tomasatrat.backend.data.entity;

import com.gmail.tomasatrat.backend.common.IDataEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "order_info")
public class OrderInfo extends AbstractEntity implements IDataEntity {

    @Column(name = "delivery_date")
    private Date deliveryDate;

    @Column(name = "addrow_date")
    private Date addrowDate;

    @Column(name = "address")
    private String address;

    @Column(name = "accepts_partial_expedition")
    private Boolean acceptsPartialExpedition;

    @Column(name = "description_1")
    private String description1;

    @Column(name = "description_2")
    private String description2;

    @Column(name = "description_3")
    private String description3;

    @Column(name = "description_4")
    private String description4;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "expedition_id", nullable = false)
    private ExpeditionType expedition;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getAddrowDate() {
        return addrowDate;
    }

    public void setAddrowDate(Date addrowDate) {
        this.addrowDate = addrowDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getAcceptsPartialExpedition() {
        return acceptsPartialExpedition;
    }

    public void setAcceptsPartialExpedition(Boolean acceptsPartialExpedition) {
        this.acceptsPartialExpedition = acceptsPartialExpedition;
    }

    public String getDescription1() {
        return description1;
    }

    public void setDescription1(String description1) {
        this.description1 = description1;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getDescription3() {
        return description3;
    }

    public void setDescription3(String description3) {
        this.description3 = description3;
    }

    public String getDescription4() {
        return description4;
    }

    public void setDescription4(String description4) {
        this.description4 = description4;
    }

    public ExpeditionType getExpedition() {
        return expedition;
    }

    public void setExpedition(ExpeditionType expedition) {
        this.expedition = expedition;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}