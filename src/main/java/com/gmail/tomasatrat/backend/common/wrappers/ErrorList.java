package com.gmail.tomasatrat.backend.common.wrappers;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class ErrorList {
    public List<String> errors;
}
