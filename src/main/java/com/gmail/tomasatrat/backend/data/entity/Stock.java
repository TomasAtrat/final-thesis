package com.gmail.tomasatrat.backend.data.entity;

import com.gmail.tomasatrat.backend.common.IDataEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "stock")
public class Stock extends AbstractEntity implements IDataEntity {
    @Column(name = "add_date")
    private Date addDate;

    @Column(name = "qt_reserve")
    private Long qtReserve;

    @Column(name = "qt_stock")
    private Long qtStock;

    @Column(name = "update_date")
    private Date updateDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "barcode_barcode")
    private Barcode barcodeBarcode;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

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

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Barcode getBarcodeBarcode() {
        return barcodeBarcode;
    }

    public void setBarcodeBarcode(Barcode barcodeBarcode) {
        this.barcodeBarcode = barcodeBarcode;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

}