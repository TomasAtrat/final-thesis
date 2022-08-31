package com.gmail.tomasatrat.backend.data;

import com.gmail.tomasatrat.backend.data.Branch;
import com.gmail.tomasatrat.backend.data.Customer;
import com.gmail.tomasatrat.backend.data.ExpeditionType;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderInfo {
    private Long id;
    private Date deliveryDate;
    private Date addrowDate;
    private String address;
    private Boolean acceptsPartialExpedition;
    private String description1;
    private String description2;
    private String description3;
    private String description4;
    private ExpeditionType expedition;
    private Branch branch;
    private Customer customer;
}
