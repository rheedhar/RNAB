package com.rnab.rnab.model;

import com.rnab.rnab.model.enums.AccountType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<AccountActivity> accountMonthlyActivities = new ArrayList<>();

    public Long getAccountId() {
        return accountId;
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
        return accountMonthlyActivities;
    }

    public void addToAccountMonthlyActivities(AccountActivity accountActivity) {
        accountMonthlyActivities.add(accountActivity);
        accountActivity.setAccount(this);
    }

}
