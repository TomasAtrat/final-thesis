package com.gmail.tomasatrat.backend.microservices.orders.services;

import com.gmail.tomasatrat.backend.common.ICrudService;
import com.gmail.tomasatrat.backend.common.IDataEntity;
import com.gmail.tomasatrat.backend.data.Customer;
import com.gmail.tomasatrat.backend.data.entity.OrderInfo;
import com.gmail.tomasatrat.backend.microservices.orders.components.OrderClient;
import com.gmail.tomasatrat.backend.repositories.OrderInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements ICrudService {

    private final OrderClient orderClient;
    private OrderInfoRepository orderInfoRepository;

    @Autowired
    public OrderService(OrderInfoRepository orderInfoRepository) {
        this.orderInfoRepository = orderInfoRepository;
        orderClient = new OrderClient();
    }

    @Override
    public List<OrderInfo> findAll() {
        return this.orderInfoRepository.findAll();
    }

    @Override
    public void addItem(IDataEntity item) {
        this.orderClient.addOrder((OrderInfo) item);
    }

    @Override
    public Optional<OrderInfo> findByID(Long id) {
        return this.orderInfoRepository.findById(id);
    }

    @Override
    public void delete(IDataEntity item) {
        this.orderInfoRepository.delete((OrderInfo) item);
    }

    public Customer getCustomer(String value) {
        return null;
    }
}
