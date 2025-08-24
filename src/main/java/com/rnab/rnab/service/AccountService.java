package com.rnab.rnab.service;

import com.rnab.rnab.dto.account.CreateAccountRequest;
import com.rnab.rnab.dto.account.CreateTransactionRequest;
import com.rnab.rnab.dto.account.UpdateAccountRequest;
import com.rnab.rnab.exception.account.AccountNotFoundException;
import com.rnab.rnab.exception.account.InvalidTransactionDateException;
import com.rnab.rnab.model.*;
import com.rnab.rnab.model.enums.AccountType;
import com.rnab.rnab.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    public Account createAccountForUser(String email, CreateAccountRequest request) {
        // find user
        User user = userService.findByEmail(email);

        // extract info from request body
        String accountName = request.getAccountName();
        AccountType accountType = request.getAccountType();

        // create account
        Account newAccount = new Account();
        newAccount.setUser(user);
        newAccount.setAccountName(accountName);
        newAccount.setAccountType(accountType);

        // save and return
        accountRepository.save(newAccount);
        loadAccountData(newAccount);
        return newAccount;
    }


    public List<Account> getUserAccounts(String email) {
        User user = userService.findByEmail(email);
        List<Account> userAccounts = accountRepository.findByUser(user);

        for(Account account: userAccounts) {
            loadAccountData(account);
        }

        return userAccounts;
    }


    public Account getAccountDetails(Long accountId, String email) {
        Account userAccount = findAccountAndUser(accountId, email);
        loadAccountData(userAccount);
        return userAccount;
    }

    public Account updateAccountDetails(Long accountId, String email, UpdateAccountRequest request) {
        // find account
        Account userAccount = findAccountAndUser(accountId, email);

        // update name if it's not null or empty
        if(request.getAccountName() != null && !request.getAccountName().isBlank()) {
            userAccount.setAccountName(request.getAccountName());
        }

        // update account type if it's not null
        if(request.getAccountType() != null) {
            userAccount.setAccountType(request.getAccountType());
        }

        // save and return user account
        accountRepository.save(userAccount);
        loadAccountData(userAccount);
        return  userAccount;

    }

    public void deleteAccount(Long accountId, String email) {
        // find account and delete
        Account userAccount = findAccountAndUser(accountId,email);
        accountRepository.delete(userAccount);
    }



    private Account findAccountAndUser(Long accountId, String email) {
        User user = userService.findByEmail(email);
        return accountRepository.findByIdAndUser(accountId, user)
                .orElseThrow(() -> new AccountNotFoundException("Account for user: " + email + " not found"));
    }


    private void loadAccountData(Account account) {
        account.getAccountMonthlyActivities().size();

        for(AccountActivity accountActivity: account.getAccountMonthlyActivities()) {
            accountActivity.getTransactions().size();
        }
    }

}
