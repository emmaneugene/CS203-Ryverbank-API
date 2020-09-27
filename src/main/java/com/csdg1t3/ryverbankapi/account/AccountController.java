package com.csdg1t3.ryverbankapi.account;

import java.util.List;

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
 * AccountController
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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/accounts")
    public Account addBook(@RequestBody Account newAccount) {
        return accountService.addAccount(newAccount);
    }
    
    @PutMapping("/accounts/{id}")
    public Account updateAccount(@PathVariable Long id, @RequestBody Account newAccount) {
        Account account = accountService.getAccount(id);

        if (account == null) {
            throw new AccountNotFoundException(id);
        }

        accountService.updateAccountBalance(id, newAccount.getBalance());
        return accountService.updateAccountAvailableBalance(id, newAccount.getAvailableBalance());
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