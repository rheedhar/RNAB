package com.rnab.rnab.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Plan {

    private LocalDate planDate;
    List<Category> spendingCategories = new ArrayList<>();

    public void setPlanDate(LocalDate planDate) {
        this.planDate = planDate;
    }

    public LocalDate getPlanDate() {
        return planDate;
    }

    public void addCategory(Category category) {
        spendingCategories.add(category);
    }

    public List<Category> getAllCategories() {
        return spendingCategories;
    }

    public void removeCategory(Category category) {
        spendingCategories.remove(category);
    }
}
