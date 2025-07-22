package com.rnab.rnab.model;

public class Category {
    private String categoryName;
    private double plannedAmount;
    private double assignedAmount;
    private double activityAmount;
    private double availableAmount;
    private String categoryGroup;
    private double defaultPlannedAmount;


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getPlannedAmount() {
        return plannedAmount;
    }

    public void setPlannedAmount(double plannedAmount) {
        this.plannedAmount = plannedAmount;
    }

    public double getAssignedAmount() {
        return assignedAmount;
    }

    public void setAssignedAmount(double assignedAmount) {
        this.assignedAmount = assignedAmount;
    }

    public double getActivityAmount() {
        return activityAmount;
    }

    public void setActivityAmount(double activityAmount) {
        this.activityAmount = activityAmount;
    }

    public double getAvailableAmount() {
        return availableAmount;
    }

    public String getCategoryGroup() {
        return categoryGroup;
    }

    public void setCategoryGroup(String categoryGroup) {
        this.categoryGroup = categoryGroup;
    }

    public double getDefaultPlannedAmount() {
        return defaultPlannedAmount;
    }

    public void setDefaultPlannedAmount(double defaultPlannedAmount) {
        this.defaultPlannedAmount = defaultPlannedAmount;
    }

    public void updateAvailableAmount() {
        this.availableAmount = this.assignedAmount - this.activityAmount;
    }


    public void addToAssignedAmount(double amount) {
        this.assignedAmount += amount;
        updateAvailableAmount();
    }

    public void subtractFromAssignedAmount(double amount) {
        this.assignedAmount -= amount;
        updateAvailableAmount();
    }

    public void addToActivityAmount(double amount) {
        this.activityAmount += amount;
        updateAvailableAmount();
    }

    public void subtractFromActivityAmount(double amount) {
        this.activityAmount -= amount;
        updateAvailableAmount();
    }
}
