package com.example.demoAAD.controller;

import com.example.demoAAD.dto.AuthDto;
import com.example.demoAAD.dto.LoginDto;
import com.example.demoAAD.service.GroupService;
import com.example.demoAAD.service.LoginService;
import com.example.demoAAD.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private RolService rolService;

    @Autowired
    private GroupService groupService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        
        try {
            AuthDto authDto = loginService.loginUser(loginDto);
            authDto.setRoles(rolService.getRoles(authDto.getIdToken()));
            authDto.setGroups(groupService.getGroups(authDto.getId(), authDto.getAccessToken()));
            return ResponseEntity.ok(authDto);
        } catch (Exception e) {
            e.printStackTrace();
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
