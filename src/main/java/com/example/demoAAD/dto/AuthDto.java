package com.example.demoAAD.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Jimena
 */
@Data
@AllArgsConstructor
public class AuthDto {
    private String id;
    private String username;
    
    @JsonIgnore
    private String idToken;
    
    @JsonIgnore
    private String accessToken;
    
    private List<String> groups;
    private List<String> roles;
}
