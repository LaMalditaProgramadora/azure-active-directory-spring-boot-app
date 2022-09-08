package com.example.demoAAD.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto<K> {
    private int status;
    private String message;
    private K result;
}
