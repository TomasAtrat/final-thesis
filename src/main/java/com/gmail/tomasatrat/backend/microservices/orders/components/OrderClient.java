package com.gmail.tomasatrat.backend.microservices.orders.components;

import com.gmail.tomasatrat.backend.common.wrappers.ErrorList;
import com.gmail.tomasatrat.backend.common.wrappers.ListOfOrderWrapper;
import com.gmail.tomasatrat.backend.data.entity.OrderInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.gmail.tomasatrat.backend.common.AppConstants.ORDERS_API;
import static com.gmail.tomasatrat.backend.common.AppConstants.WEB_SERVICE1;

@Component
public class OrderClient
{
    private final RestTemplate restTemplate;
    private final String BASE_URL = WEB_SERVICE1 + ORDERS_API;

    public OrderClient() {
        this.restTemplate = new RestTemplate();
    }

    public List<OrderInfo> getOrders() {
        final String url = BASE_URL + "/";
        ListOfOrderWrapper ordersWrapper = restTemplate.getForObject(url, ListOfOrderWrapper.class);
        return ordersWrapper.getOrders();
    }

    public OrderInfo addOrder(OrderInfo order) {
        final String url = BASE_URL + "/";
        restTemplate.postForObject(url, order, ErrorList.class);
        return order;
    }
}
