package com.rnab.rnab.controller;

import com.rnab.rnab.dto.account.CreateAccountRequest;
import com.rnab.rnab.dto.account.CreateTransactionRequest;
import com.rnab.rnab.dto.account.UpdateAccountRequest;
import com.rnab.rnab.model.Account;
import com.rnab.rnab.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Account>> getUserAccounts(Authentication auth) {
        String email = auth.getName();
        List<Account> accounts = accountService.getUserAccounts(email);
        return ResponseEntity.ok(accounts);
    }


    @PostMapping("/create")
    public ResponseEntity<Account> createAccountForUser(
            @Valid @RequestBody CreateAccountRequest request,
            Authentication auth
            ) {
        String email = auth.getName();
        Account account = accountService.createAccountForUser(email, request);
        return ResponseEntity.ok(account);
    }


    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountDetails(
            @PathVariable Long accountId,
            Authentication auth
    ) {
        String email = auth.getName();
        Account account = accountService.getAccountDetails(accountId, email);
        return ResponseEntity.ok(account);
    }


    @PatchMapping("/{accountId}")
    public ResponseEntity<Account> updateAccountDetails(
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateAccountRequest request,
            Authentication auth
            ) {
        String email = auth.getName();
        Account account = accountService.updateAccountDetails(accountId, email, request);
        return ResponseEntity.ok(account);
    }


    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long accountId,
            Authentication auth) {
        String email = auth.getName();
        accountService.deleteAccount(accountId, email);
        return ResponseEntity.noContent().build();
    }


}
