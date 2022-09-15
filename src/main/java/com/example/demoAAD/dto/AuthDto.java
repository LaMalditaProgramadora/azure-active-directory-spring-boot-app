package com.example.demoAAD.dto;

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
    private List<GroupDto> groups;
    private List<String> roles;
}
