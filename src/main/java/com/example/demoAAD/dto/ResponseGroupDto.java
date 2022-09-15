package com.example.demoAAD.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Jimena
 */
@Data
public class ResponseGroupDto {
    
    @JsonProperty("@odata.context")
    private String dataContext;
    
    private List<GroupDto> value;
    
    
    
}
