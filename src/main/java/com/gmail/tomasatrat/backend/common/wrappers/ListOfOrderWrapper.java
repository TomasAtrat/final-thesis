package com.gmail.tomasatrat.backend.common.wrappers;

import com.gmail.tomasatrat.backend.data.entity.OrderInfo;
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
