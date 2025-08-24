package com.rnab.rnab.dto.account;

import com.rnab.rnab.model.enums.AccountType;
import jakarta.validation.constraints.Size;

public class UpdateAccountRequest {
    @Size(min=3, max=50, message="Account name must be between 3 and 50 characters")
    private String accountName;

    private AccountType accountType;

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
}
