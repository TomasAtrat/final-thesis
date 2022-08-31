package com.gmail.tomasatrat.backend.data;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Customer {
    private String id;
    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;
}
