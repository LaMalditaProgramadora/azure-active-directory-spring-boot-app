package com.example.demoAAD.dto;

import lombok.Data;

/**
 *
 * @author Jimena
 */
@Data
public class GroupDto {
    
    private String appRoleId;
    private String principalDisplayName;
    private String resourceId;
    private String principalType;
}
