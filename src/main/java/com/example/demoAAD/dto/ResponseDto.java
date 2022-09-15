package com.example.demoAAD.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto {
    private int status;
    private String message;

    private AuthDto result;
}
