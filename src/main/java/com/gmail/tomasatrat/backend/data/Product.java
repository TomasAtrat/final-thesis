package com.gmail.tomasatrat.backend.data;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Product {
    private String description;
    private String category;
    private String model;
    private String brand;
    private BigDecimal price;
    private byte[] picture;
    private Integer minQuantity;
    private Integer resupplyQuantity;
}
