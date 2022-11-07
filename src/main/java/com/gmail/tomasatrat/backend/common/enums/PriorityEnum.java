package com.gmail.tomasatrat.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum PriorityEnum {
    HIGH("ALTA"),
    MEDIUM("MEDIA"),
    LOW("BAJA");

    private String value;

    public static PriorityEnum getPriorityByStringValue(String value){
        return Arrays.stream(PriorityEnum.values()).filter(i-> i.getValue().equals(value)).findFirst().orElseGet(null);
    }

    public static String[] getPriorities() {
        return new String[]{HIGH.value, MEDIUM.value, LOW.value};
    }
}
