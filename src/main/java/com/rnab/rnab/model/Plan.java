package com.rnab.rnab.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
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
    private List<CategoryGroup> categoryGroups = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanDate(LocalDate planDate) {
        this.planDate = planDate;
    }

    public LocalDate getPlanDate() {
        return planDate;
    }

    public List<CategoryGroup> getCategoryGroups() {
        return categoryGroups;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void addCategoryGroup(CategoryGroup categoryGroup) {
        categoryGroups.add(categoryGroup);
        categoryGroup.setPlan(this);
    }

    public void removeCategoryGroup(CategoryGroup categoryGroup) {
        categoryGroups.remove(categoryGroup);
        categoryGroup.setPlan(null);
    }

}
