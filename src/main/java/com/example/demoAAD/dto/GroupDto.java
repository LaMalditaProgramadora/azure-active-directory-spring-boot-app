package com.example.demoAAD.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author Jimena
 */
@Data
public class GroupDto {
    
    @JsonProperty("@odata.type")
    private String dataType;
    
    private String id;
    private String displayName;
}
