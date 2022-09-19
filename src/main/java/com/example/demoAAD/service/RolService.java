package com.example.demoAAD.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author Jimena
 */
@Service
public class RolService {

    public List<String> getRoles(String idToken) {
        JsonObject jsonObject = getDecodePayload(idToken);
        JsonArray rolesJson = getRolesFromPayload(jsonObject);
        List<String> roles = new ArrayList<>();
        rolesJson.forEach((t) -> {
            roles.add(t.getAsString());
        });
        return roles;
    }

    private JsonObject getDecodePayload(String idToken) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] chunks = idToken.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));
        return new JsonParser().parse(payload).getAsJsonObject();
    }

    private JsonArray getRolesFromPayload(JsonObject jsonObject) {
        JsonArray jsonRoles = new JsonArray();
        if (jsonObject.get("roles") != null) {
            jsonRoles = jsonObject.get("roles").getAsJsonArray();
        }
        return jsonRoles;
    }
}
