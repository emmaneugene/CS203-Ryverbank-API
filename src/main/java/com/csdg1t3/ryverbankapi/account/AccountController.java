package com.csdg1t3.ryverbankapi.account;

import java.util.List;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that manages HTTP GET/POST/PUT/DELETE requests by calling methods in AccountService
 */
@RestController
public class AccountController {
    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public List<Account> getAccounts() {
        return accountService.listAccounts();
    }

    @GetMapping("/accounts/{id}")
    public Account getAccount(@PathVariable Long id) {
        Account account = accountService.getAccount(id);
        if (account == null) {
            throw new AccountNotFoundException(id);
        }
        return account;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/accounts")
    public Account addAccount(@Valid @RequestBody Account newAccount) {
        return accountService.addAccount(newAccount);
    }
    
    @PutMapping("/accounts/{id}")
    public Account updateAccount(@PathVariable Long id, @Valid @RequestBody Account newAccount) {
        Account account = accountService.getAccount(id);

        if (account == null) {
            throw new AccountNotFoundException(id);
        }
        return accountService.updateAccount(id, newAccount);
    }

    @DeleteMapping("/accounts/{id}")
    public void deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteAccount(id);
        } catch (Exception e) {
            throw new AccountNotFoundException(id);
        }
    }
}