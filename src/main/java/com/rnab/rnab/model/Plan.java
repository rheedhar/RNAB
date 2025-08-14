package com.rnab.rnab.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name="plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="plan_id")
    private Long id;

    @Column(nullable = false)
    private LocalDate planDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal readyToAssignAmount = new BigDecimal("0.00");

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CategoryGroup> categoryGroups = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public Long getId() {
        return id;
    }

    public void setPlanDate(LocalDate planDate) {
        this.planDate = planDate;
    }

    public LocalDate getPlanDate() {
        return planDate;
    }

    public BigDecimal getReadyToAssignAmount() {
        return readyToAssignAmount;
    }

    public void setReadyToAssignAmount(BigDecimal readyToAssignAmount) {
        this.readyToAssignAmount = readyToAssignAmount;
    }

    public List<CategoryGroup> getCategoryGroups() {
        return Collections.unmodifiableList(categoryGroups);
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

    public void addToReadyToAssign(BigDecimal amount) {
        this.readyToAssignAmount = this.readyToAssignAmount.add(amount);
    }

    public void subtractFromReadyToAssign(BigDecimal amount) {
        this.readyToAssignAmount = this.readyToAssignAmount.subtract(amount);
    }

}
