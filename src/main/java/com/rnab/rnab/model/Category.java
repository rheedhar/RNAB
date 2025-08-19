package com.rnab.rnab.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name="categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="category_id")
    private Long id;

    @Column(nullable = false)
    private String categoryName;

    @JsonFormat(pattern="0.00")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal plannedAmount = new BigDecimal("0.00");

    @JsonFormat(pattern="0.00")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal defaultPlannedAmount = new BigDecimal("0.00");

    @JsonFormat(pattern="0.00")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal assignedAmount = new BigDecimal("0.00");

    @JsonFormat(pattern="0.00")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal activityAmount = new BigDecimal("0.00");

    @JsonFormat(pattern="0.00")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal availableAmount = new BigDecimal("0.00");

    @ManyToOne
    @JoinColumn(name = "category_group_id", nullable = false)
    @JsonIgnore
    private CategoryGroup categoryGroup;

    public Long getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getPlannedAmount() {
        return plannedAmount;
    }

    public void setPlannedAmount(BigDecimal plannedAmount) {
        this.plannedAmount = plannedAmount;
    }

    public BigDecimal getAssignedAmount() {
        return assignedAmount;
    }

    public void setAssignedAmount(BigDecimal assignedAmount) {
        this.assignedAmount = assignedAmount;
        updateAvailableAmount();
    }

    public BigDecimal getActivityAmount() {
        return activityAmount;
    }

    public void setActivityAmount(BigDecimal activityAmount) {
        this.activityAmount = activityAmount;
        updateAvailableAmount();
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public BigDecimal getDefaultPlannedAmount() {
        return defaultPlannedAmount;
    }

    public void setDefaultPlannedAmount(BigDecimal defaultPlannedAmount) {
        this.defaultPlannedAmount = defaultPlannedAmount;
    }

    public CategoryGroup getCategoryGroup() {
        return categoryGroup;
    }

    public void setCategoryGroup(CategoryGroup categoryGroup) {
        this.categoryGroup = categoryGroup;
    }


    public void updateAvailableAmount() {
        this.availableAmount = this.assignedAmount.subtract(this.activityAmount);
    }


    public void addToAssignedAmount(BigDecimal amount) {
        this.assignedAmount = this.assignedAmount.add(amount);
        updateAvailableAmount();
    }

    public void subtractFromAssignedAmount(BigDecimal amount) {
        this.assignedAmount = this.assignedAmount.subtract(amount);
        updateAvailableAmount();
    }

    public void addToActivityAmount(BigDecimal amount) {
        this.activityAmount = this.activityAmount.add(amount);
        updateAvailableAmount();
    }

    public void subtractFromActivityAmount(BigDecimal amount) {
        this.activityAmount = this.activityAmount.subtract(amount);
        updateAvailableAmount();
    }

}
