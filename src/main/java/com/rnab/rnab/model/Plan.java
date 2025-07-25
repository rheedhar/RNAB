package com.rnab.rnab.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @Column(nullable = false)
    private LocalDate planDate;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Category> spendingCategories = new ArrayList<>();

    public Long getPlanId() {
        return planId;
    }

    public void setPlanDate(LocalDate planDate) {
        this.planDate = planDate;
    }

    public LocalDate getPlanDate() {
        return planDate;
    }

    public List<Category> getSpendingCategories() {
        return spendingCategories;
    }

    public void addCategory(Category category) {
        spendingCategories.add(category);
        category.setPlan(this);
    }

    public void removeCategory(Category category) {
        spendingCategories.remove(category);
        category.setPlan(null);
    }
}
