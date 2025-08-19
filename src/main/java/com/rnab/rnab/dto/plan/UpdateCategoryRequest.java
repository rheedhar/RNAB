package com.rnab.rnab.dto.plan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateCategoryRequest {
    @NotBlank(message = "Category name is required")
    @Size(min = 3, max = 20, message = "Category name must be between 3 and 20 characters")
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
