package com.rnab.rnab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateCategoryGroupRequest {
    @NotBlank(message = "Group name is required")
    @Size(min = 3, max = 20, message = "Group name must be between 3 and 20 characters")
    private String groupName;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
