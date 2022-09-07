package com.example.demoAAD.dto;

import com.microsoft.aad.adal4j.AuthenticationResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto {
    private int status;
    private String message;
    private AuthenticationResult result;
}
