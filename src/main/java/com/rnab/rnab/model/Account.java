package com.rnab.rnab.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rnab.rnab.model.enums.AccountType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name="accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="account_id")
    private Long id;

    @Column(nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<AccountActivity> accountMonthlyActivities = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    public User user;

    public Long getId() {
        return id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public List<AccountActivity> getAccountMonthlyActivities() {
        return Collections.unmodifiableList(accountMonthlyActivities);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void addToAccountMonthlyActivities(AccountActivity accountActivity) {
        accountMonthlyActivities.add(accountActivity);
        accountActivity.setAccount(this);
    }

    public void removeAccountActivity(AccountActivity accountActivity) {
        accountMonthlyActivities.remove(accountActivity);
        accountActivity.setAccount(null);
    }

}
