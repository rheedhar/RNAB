package com.rnab.rnab.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name="category_groups", uniqueConstraints = @UniqueConstraint(columnNames = {"plan_id", "group_name"}))
public class CategoryGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="category_group_id")
    private Long id;

    @Column(nullable = false)
    private String groupName;

    @OneToMany(mappedBy = "categoryGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonIgnore
    private Plan plan;

    public Long getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }


    public void addCategory(Category category) {
        categories.add(category);
        category.setCategoryGroup(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.setCategoryGroup(null);
    }

    public BigDecimal getTotalPlanned() {
        return categories.stream().map(Category::getPlannedAmount).reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    public BigDecimal getTotalAssigned() {
        return categories.stream().map(Category::getAssignedAmount).reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    public BigDecimal getTotalActivity() {
        return categories.stream().map(Category::getActivityAmount).reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    public BigDecimal getTotalAvailable() {
        return getTotalAssigned().subtract(getTotalActivity());
    }

}
