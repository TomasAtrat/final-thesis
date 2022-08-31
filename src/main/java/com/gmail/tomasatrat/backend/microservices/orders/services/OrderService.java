package com.gmail.tomasatrat.backend.microservices.orders.services;

import com.gmail.tomasatrat.backend.data.OrderInfo;
import com.gmail.tomasatrat.backend.microservices.orders.components.OrderClient;
import org.springframework.stereotype.Service;
import org.vaadin.crudui.crud.CrudListener;

import java.util.Collection;

@Service
public class OrderService implements CrudListener<OrderInfo> {

    private OrderClient orderClient;

    public OrderService() {
        orderClient = new OrderClient();
    }

    @Override
    public Collection<OrderInfo> findAll() {
        return orderClient.getOrders();
    }

    @Override
    public OrderInfo add(OrderInfo orderInfo) {
        return orderClient.addOrder(orderInfo);
    }

    @Override
    public OrderInfo update(OrderInfo orderInfo) {
        return null;
    }

    @Override
    public void delete(OrderInfo orderInfo) {

    }
}
