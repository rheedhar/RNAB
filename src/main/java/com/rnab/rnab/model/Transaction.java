package com.rnab.rnab.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rnab.rnab.model.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="transaction_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_activity_id", nullable = false)
    @JsonIgnore
    private AccountActivity accountActivity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private boolean isReadyToAssignTransaction = false;

    @Column(nullable = false)
    private LocalDate transactionDate;

    private String payee;

    private String memo;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal transactionAmount;


    public Long getId() {
        return id;
    }

    public AccountActivity getAccountActivity() {
        return accountActivity;
    }

    public void setAccountActivity(AccountActivity accountActivity) {
        this.accountActivity = accountActivity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isReadyToAssignTransaction() {
        return isReadyToAssignTransaction;
    }

    public void setReadyToAssignTransaction(boolean readyToAssignTransaction) {
        this.isReadyToAssignTransaction = readyToAssignTransaction;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}
