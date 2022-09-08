package com.example.demoAAD.controller;

import com.example.demoAAD.dto.LoginDto;
import com.example.demoAAD.dto.ResponseDto;
import com.example.demoAAD.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        ResponseDto response = loginService.loginUser(loginDto);
        return ResponseEntity.ok(response);
    }

}
