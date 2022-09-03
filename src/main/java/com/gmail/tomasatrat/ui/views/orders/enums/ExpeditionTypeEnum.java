package com.gmail.tomasatrat.ui.views.orders.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExpeditionTypeEnum {
    BOPIS(1),
    SEND_TO_ADDRESS(2);

    private int value;
}

