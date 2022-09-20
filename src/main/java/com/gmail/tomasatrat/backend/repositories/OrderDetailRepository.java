package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.OrderDetail;
import com.gmail.tomasatrat.backend.data.entity.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderInfo(OrderInfo info);
}