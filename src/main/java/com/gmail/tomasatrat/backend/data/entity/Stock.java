package com.gmail.tomasatrat.backend.data.entity;

import com.gmail.tomasatrat.backend.common.IDataEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "stock")
public class Stock extends AbstractEntity implements IDataEntity {

    @Column(name = "qtReserve")
    private Long qtReserve;

    @Column(name = "qtStock")
    private Long qtStock;

    @Column(name = "addDate")
    private Date addDate;

    @Column(name = "updateDate")
    private Date updateDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "barcode_barcode")
    private Barcode barcodeId;

    public Long getQtReserve() {
        return qtReserve;
    }

    public void setQtReserve(Long qtReserve) {
        this.qtReserve = qtReserve;
    }

    public Long getQtStock() {
        return qtStock;
    }

    public void setQtStock(Long qtStock) {
        this.qtStock = qtStock;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Barcode getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(Barcode barcodeId) {
        this.barcodeId = barcodeId;
    }
}
