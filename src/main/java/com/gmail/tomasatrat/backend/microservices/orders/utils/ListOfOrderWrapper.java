package com.gmail.tomasatrat.backend.microservices.orders.utils;

import com.gmail.tomasatrat.backend.data.OrderInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ListOfOrderWrapper {
    List<OrderInfo> orders;
}
