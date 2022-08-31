package com.gmail.tomasatrat.backend.data;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Branch {
    private Long id;
    private String description;
}
