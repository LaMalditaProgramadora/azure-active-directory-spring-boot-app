package com.example.demoAAD.service;

import com.example.demoAAD.dto.*;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final String loginMicrosoftOnlineUrl = "https://login.microsoftonline.com";
    private final String graphMicrosoftUrl = "https://graph.microsoft.com";

    @Value("${AZURE_TENANT_ID}")
    private String AZURE_TENANT_ID;

    @Value("${AZURE_APP_ID}")
    private String AZURE_APP_ID;
    
    @Value("${AZURE_MFA:false}")
    private boolean AZURE_MFA;

    public AuthDto loginUser(LoginDto loginDto) throws Exception {
        AuthenticationResult result = getAuthResult(loginDto);
        String idUser = result.getUserInfo().getUniqueId();
        AuthDto authDto = new AuthDto(idUser, loginDto.getUsername(), result.getIdToken(), result.getAccessToken(), null, null);
        return authDto;
    }

    public AuthenticationResult getAuthResult(LoginDto loginDto) throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(1);
        AuthenticationContext context
                = new AuthenticationContext(loginMicrosoftOnlineUrl + "/" + AZURE_TENANT_ID, true, service);
        Future<AuthenticationResult> resultFuture = context.acquireToken(
                graphMicrosoftUrl, AZURE_APP_ID, loginDto.getUsername(), loginDto.getPassword(),
                null);
        return resultFuture.get();
    }
    
}
