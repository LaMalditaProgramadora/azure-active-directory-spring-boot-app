package com.example.demoAAD.service;

import com.example.demoAAD.dto.GroupDto;
import com.example.demoAAD.dto.ResponseGroupDto;
import com.example.demoAAD.helpers.Config;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Jimena
 */
@Service
public class GroupService {

    private final String graphMicrosoftMemberOfUrl = "https://graph.microsoft.com/v1.0/users/";

    @Autowired
    private RestTemplate restTemplate;

    public List<String> getGroups(String idUser, String accessToken) {
        List<String> groupsString = new ArrayList<String>();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        ResponseEntity<ResponseGroupDto> responseGroupDto
                = restTemplate.exchange(
                        graphMicrosoftMemberOfUrl + idUser + "/appRoleAssignments",
                        HttpMethod.GET,
                        new HttpEntity(headers),
                        ResponseGroupDto.class);
        groupsString = filterGroups(responseGroupDto.getBody().getValue());
        return groupsString;
    }

    private List<String> filterGroups(List<GroupDto> groups) {
        List<String> groupsString = new ArrayList<>();
        groups.forEach((group) -> {
            if (isAppGroup(group.getResourceId(), group.getPrincipalType())
                    && !groupsString.contains(group.getPrincipalDisplayName())) {
                groupsString.add(group.getPrincipalDisplayName());
            }
        });
        return groupsString;
    }

    private boolean isAppGroup(String resourceId, String principalType) {
        return resourceId.equals(Config.OBJECT_ID) && principalType.equals("Group");
    }

}
