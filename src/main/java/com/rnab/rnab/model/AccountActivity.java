package com.rnab.rnab.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rnab.rnab.model.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name="account_activities")
public class AccountActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="account_activity_id")
    private Long id;

    @Column(nullable = false)
    private LocalDate accountActivityDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalDeposits = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalWithdrawals = BigDecimal.ZERO;

    @OneToMany(mappedBy = "accountActivity", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    @JsonIgnore
    private Account account;


    public Long getId() {
        return id;
    }

    public LocalDate getAccountActivityDate() {
        return accountActivityDate;
    }

    public void setAccountActivityDate(LocalDate accountActivityDate) {
        this.accountActivityDate = accountActivityDate;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void updateBalance() {
        this.currentBalance = this.totalDeposits.subtract(this.totalWithdrawals);
    }

    public BigDecimal getTotalDeposits() {
        return totalDeposits;
    }

    public void addToTotalDeposits(BigDecimal depositAmount) {
        this.totalDeposits = this.totalDeposits.add(depositAmount);
        updateBalance();
    }

    public void subtractFromTotalDeposits(BigDecimal depositAmount) {
        this.totalDeposits = this.totalDeposits.subtract(depositAmount);
        updateBalance();
    }

    public BigDecimal getTotalWithdrawals() {
        return totalWithdrawals;
    }

    public void addToTotalWithdrawals(BigDecimal withdrawalAmount) {
        this.totalWithdrawals = this.totalWithdrawals.add(withdrawalAmount);
        updateBalance();
    }

    public void subtractFromTotalWithdrawals(BigDecimal withdrawalAmount) {
        this.totalWithdrawals = this.totalWithdrawals.subtract(withdrawalAmount);
        updateBalance();
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setAccountActivity(this);
        if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
            addToTotalDeposits(transaction.getTransactionAmount());
        } else {
            addToTotalWithdrawals(transaction.getTransactionAmount());
        }
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
        transaction.setAccountActivity(null);
        if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
            subtractFromTotalDeposits(transaction.getTransactionAmount());
        } else {
            subtractFromTotalWithdrawals(transaction.getTransactionAmount());
        }
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
