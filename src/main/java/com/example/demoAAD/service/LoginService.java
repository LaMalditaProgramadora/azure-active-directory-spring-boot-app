package com.example.demoAAD.service;

import com.example.demoAAD.dto.*;
import com.example.demoAAD.util.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LoginService {

    private final String loginMicrosoftOnlineUrl = "https://login.microsoftonline.com";
    private final String graphMicrosoftUrl = "https://graph.microsoft.com";
    private final String graphMicrosoftMemberOfUrl = "https://graph.microsoft.com/v1.0/users/";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${AZURE_TENANT_ID}")
    private String AZURE_TENANT_ID;

    @Value("${AZURE_CLIENT_ID}")
    private String AZURE_CLIENT_ID;

    public ResponseDto loginUser(LoginDto loginDto) {
        ResponseDto response = new ResponseDto(1, Constants.MESSAGE_RESULT_OK, null);
        try {
            AuthenticationResult result = getAuthResult(loginDto);
            String idUser = result.getUserInfo().getUniqueId();
            String accessToken = result.getAccessToken();
            AuthDto authDto = new AuthDto(idUser, loginDto.getUsername(), getGroups(idUser, accessToken), getRoles(result.getIdToken()));
            response.setResult(authDto);
        } catch (Exception e) {
            response = new ResponseDto(-1, e.getMessage(), null);
        }
        return response;
    }

    public AuthenticationResult getAuthResult(LoginDto loginDto) throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(1);
        AuthenticationContext context
                = new AuthenticationContext(loginMicrosoftOnlineUrl + "/" + AZURE_TENANT_ID, true, service);
        System.out.println(AZURE_CLIENT_ID);
        Future<AuthenticationResult> resultFuture = context.acquireToken(
                graphMicrosoftUrl, AZURE_CLIENT_ID, loginDto.getUsername(), loginDto.getPassword(),
                null);
        return resultFuture.get();

    }

    public List<String> getRoles(String idToken) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] chunks = idToken.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));
        JsonObject jsonObject = new JsonParser().parse(payload).getAsJsonObject();
        JsonArray rolesJson = jsonObject.get("roles").getAsJsonArray();
        List<String> roles = new ArrayList<>();
        rolesJson.forEach((t) -> {
            roles.add(t.getAsString());
        });
        return roles;
    }

    public List<GroupDto> getGroups(String idUser, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        ResponseEntity<ResponseGroupDto> responseGroupDto
                = restTemplate.exchange(
                        graphMicrosoftMemberOfUrl + idUser + "/memberOf",
                        HttpMethod.GET,
                        new HttpEntity(headers),
                        ResponseGroupDto.class);
        List<GroupDto> groups = responseGroupDto.getBody().getValue();
        groups.removeIf(p -> !p.getDataType().equals("#microsoft.graph.directoryRole"));
        return groups;
    }

}
