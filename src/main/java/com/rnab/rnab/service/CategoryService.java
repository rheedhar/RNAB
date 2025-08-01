package com.rnab.rnab.service;

import com.rnab.rnab.model.Category;
import com.rnab.rnab.model.CategoryGroup;
import com.rnab.rnab.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CategoryService {
    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void createCategory(Category category) {

    }

    public void changeCategoryGroup(Category category, CategoryGroup categoryGroup) {

    }

    public void addCategoryTogroup(Category category, CategoryGroup categoryGroup) {

    }

    public void assignMoneyToReadyToAssign(Category category, BigDecimal amount) {

    }
}
