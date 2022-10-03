package com.gmail.tomasatrat.backend.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "reception_detail")
public class ReceptionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "scheduled_qty")
    private Integer scheduledQty;

    @Column(name = "received_qty")
    private Integer receivedQty;

    @Column(name = "accepted_qty")
    private Integer acceptedQty;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reception_list_id")
    private ReceptionList receptionList;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "barcode")
    private Barcode barcode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScheduledQty() {
        return scheduledQty;
    }

    public void setScheduledQty(Integer scheduledQty) {
        this.scheduledQty = scheduledQty;
    }

    public Integer getReceivedQty() {
        return receivedQty;
    }

    public void setReceivedQty(Integer receivedQty) {
        this.receivedQty = receivedQty;
    }

    public Integer getAcceptedQty() {
        return acceptedQty;
    }

    public void setAcceptedQty(Integer acceptedQty) {
        this.acceptedQty = acceptedQty;
    }

    public ReceptionList getReceptionList() {
        return receptionList;
    }

    public void setReceptionList(ReceptionList receptionList) {
        this.receptionList = receptionList;
    }

    public Barcode getBarcode() {
        return barcode;
    }

    public void setBarcode(Barcode barcode) {
        this.barcode = barcode;
    }

}