package com.gmail.tomasatrat.backend.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "order_detail")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "ordered_quantity")
    private Integer orderedQuantity;

    @Column(name = "prepared_quantity")
    private Integer preparedQuantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "barcode")
    private Barcode barcode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_info_id")
    private OrderInfo orderInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(Integer orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public Integer getPreparedQuantity() {
        return preparedQuantity;
    }

    public void setPreparedQuantity(Integer preparedQuantity) {
        this.preparedQuantity = preparedQuantity;
    }

    public Barcode getBarcode() {
        return barcode;
    }

    public void setBarcode(Barcode barcode) {
        this.barcode = barcode;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

}