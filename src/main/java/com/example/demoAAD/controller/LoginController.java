package com.example.demoAAD.controller;

import com.example.demoAAD.dto.AuthDto;
import com.example.demoAAD.dto.LoginDto;
import com.example.demoAAD.helpers.AuthException;
import com.example.demoAAD.helpers.AuthHelper;
import com.example.demoAAD.helpers.IdentityContextAdapterServlet;
import com.example.demoAAD.service.GroupService;
import com.example.demoAAD.service.LoginService;
import com.example.demoAAD.service.RolService;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
            authDto = rolService.getRoles(authDto);
            authDto.setGroups(groupService.getGroups(authDto.getId(), authDto.getAccessToken()));
            return ResponseEntity.ok(authDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/loginMFA")
    public ResponseEntity<?> loginMFA(final HttpServletRequest req, final HttpServletResponse resp)  {
        try {
            AuthHelper.signIn(new IdentityContextAdapterServlet(req, resp));
            return ResponseEntity.ok("Autenticación en marcha");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/auth/redirect")
    public ResponseEntity<?> redirect(final HttpServletRequest req, final HttpServletResponse resp){
        try {
            AuthDto authDto = AuthHelper.processAADCallback(new IdentityContextAdapterServlet(req, resp));
            authDto = rolService.getRoles(authDto);
            authDto.setGroups(groupService.getGroups(authDto.getId(), authDto.getAccessToken()));
            return ResponseEntity.ok(authDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
