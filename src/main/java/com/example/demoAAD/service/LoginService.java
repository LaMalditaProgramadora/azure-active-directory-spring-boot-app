package com.example.demoAAD.service;

import com.example.demoAAD.dto.LoginDto;
import com.example.demoAAD.dto.ResponseDto;
import com.example.demoAAD.util.Constants;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final String microsoftOnlineUrl = "https://login.microsoftonline.com";
    private final String graphWindowsUrl = "https://graph.windows.net";

    @Value("${AZURE_TENANT_ID}")
    private String AZURE_TENANT_ID;

    @Value("${AZURE_CLIENT_ID}")
    private String AZURE_CLIENT_ID;

    public ResponseDto createDefaultAzureCredential(LoginDto loginDto) {
        
        System.out.println(AZURE_TENANT_ID);
        System.out.println(AZURE_CLIENT_ID);
        ResponseDto response = new ResponseDto(1, Constants.MESSAGE_RESULT_OK, null);
        try {
            ExecutorService service = Executors.newFixedThreadPool(1);
            AuthenticationContext context
                    = new AuthenticationContext(microsoftOnlineUrl + "/" + AZURE_TENANT_ID, false, service);
            Future<AuthenticationResult> resultFuture = context.acquireToken(
                    graphWindowsUrl, AZURE_CLIENT_ID, loginDto.getUsername(), loginDto.getPassword(),
                    null);
            AuthenticationResult result = resultFuture.get();
            response.setResult(result);
        } catch (Exception e) {
            response = new ResponseDto(-1, e.getMessage(), null);
        }
        return response;
    }
}
