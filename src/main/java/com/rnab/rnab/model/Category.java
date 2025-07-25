package com.rnab.rnab.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name="categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false)
    private String categoryName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal plannedAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal defaultPlannedAmount;

    @Column(nullable = false)
    private String categoryGroup;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal assignedAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal activityAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal availableAmount = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    public Long getCategoryId() {
        return categoryId;
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
    }

    public BigDecimal getActivityAmount() {
        return activityAmount;
    }

    public void setActivityAmount(BigDecimal activityAmount) {
        this.activityAmount = activityAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public String getCategoryGroup() {
        return categoryGroup;
    }

    public void setCategoryGroup(String categoryGroup) {
        this.categoryGroup = categoryGroup;
    }

    public BigDecimal getDefaultPlannedAmount() {
        return defaultPlannedAmount;
    }

    public void setDefaultPlannedAmount(BigDecimal defaultPlannedAmount) {
        this.defaultPlannedAmount = defaultPlannedAmount;
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

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }
}
