package com.gmail.tomasatrat.backend.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "inventory_detail")
public class InventoryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "supposed_qty")
    private Integer supposedQty;

    @Column(name = "read_qty")
    private Integer readQty;

    @Column(name = "accepted_qty")
    private Integer acceptedQty;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "barcode")
    private Barcode barcode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSupposedQty() {
        return supposedQty;
    }

    public void setSupposedQty(Integer supposedQty) {
        this.supposedQty = supposedQty;
    }

    public Integer getReadQty() {
        return readQty;
    }

    public void setReadQty(Integer readQty) {
        this.readQty = readQty;
    }

    public Integer getAcceptedQty() {
        return acceptedQty;
    }

    public void setAcceptedQty(Integer acceptedQty) {
        this.acceptedQty = acceptedQty;
    }

    public Barcode getBarcode() {
        return barcode;
    }

    public void setBarcode(Barcode barcode) {
        this.barcode = barcode;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

}