package com.example.demoAAD.service;

import com.example.demoAAD.dto.*;
import com.example.demoAAD.helpers.Config;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    public AuthDto loginUser(LoginDto loginDto) throws Exception {
        AuthenticationResult result = getAuthResult(loginDto);
        String idUser = result.getUserInfo().getUniqueId();
        AuthDto authDto = new AuthDto(idUser, loginDto.getUsername(), result.getIdToken(), result.getAccessToken(), null, null);
        return authDto;
    }

    public AuthenticationResult getAuthResult(LoginDto loginDto) throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(1);
        AuthenticationContext context
                = new AuthenticationContext(Config.AUTHORITY, true, service);
        Future<AuthenticationResult> resultFuture = context.acquireToken(
                Config.GRAPH_URL, Config.CLIENT_ID, loginDto.getUsername(), loginDto.getPassword(),
                null);
        return resultFuture.get();
    }
    
}
