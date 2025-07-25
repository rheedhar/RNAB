package com.rnab.rnab.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="category_groups")
public class CategoryGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryGroupId;

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false)
    private boolean isSystemGroup = false;

    @OneToMany(mappedBy = "categoryGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    public Long getCategoryGroupId() {
        return categoryGroupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isSystemGroup() {
        return isSystemGroup;
    }

    public void setSystemGroup(boolean systemGroup) {
        isSystemGroup = systemGroup;
    }


    public List<Category> getCategories() {
        return categories;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }


    public void addCategory(Category category) {
        categories.add(category);
        category.setGroup(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.setGroup(null);
    }

    public BigDecimal getTotalPlanned() {
        return categories.stream().map(Category::getPlannedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalAssigned() {
        return categories.stream().map(Category::getAssignedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalActivity() {
        return categories.stream().map(Category::getActivityAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalAvailable() {
        return getTotalAssigned().subtract(getTotalActivity());
    }




}
