package com.example.demoAAD.service;

import com.example.demoAAD.dto.LoginDto;
import com.example.demoAAD.dto.ResponseDto;
import com.example.demoAAD.util.Constants;
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

    @Value("${AZURE_CLIENT_ID}")
    private String AZURE_CLIENT_ID;

    public ResponseDto loginUser(LoginDto loginDto) {
        ResponseDto response = new ResponseDto(1, Constants.MESSAGE_RESULT_OK, null);
        String url = loginMicrosoftOnlineUrl + "/" + AZURE_TENANT_ID;
        try {
            ExecutorService service = Executors.newFixedThreadPool(1);
            AuthenticationContext context
                    = new AuthenticationContext(url, true, service);
            Future<AuthenticationResult> resultFuture = context.acquireToken(
                    graphMicrosoftUrl, AZURE_CLIENT_ID, loginDto.getUsername(), loginDto.getPassword(),
                    null);
            AuthenticationResult result = resultFuture.get();
            response.setResult(result);
        } catch (Exception e) {
            response = new ResponseDto(-1, e.getMessage(), null);
        }
        return response;
    }

}
