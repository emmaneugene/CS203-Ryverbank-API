package com.csdg1t3.ryverbankapi.account;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.csdg1t3.ryverbankapi.customer.*;

/**
 * Controller that manages HTTP GET/POST/PUT/DELETE requests by calling methods in AccountService
 */
@RestController
public class AccountController {
    private AccountService accountService;
    private CustomerRepository customerRepo;

    public AccountController(AccountService accountService, CustomerRepository customerRepo) {
        this.accountService = accountService;
        this.customerRepo = customerRepo;
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
    public Account addAccount(@RequestBody Account account) {
        Optional<Customer> result = customerRepo.findById(account.getCustomerId());
        if (result.isPresent()) {
            account.setCustomer(result.get());
            return accountService.addAccount(account);
        }
        throw new CustomerNotFoundException(account.getCustomerId());
    }
    
    @PutMapping("/accounts/{id}")
    public Account updateAccount(@PathVariable Long id, @RequestBody Account account) {
        account.setId(id);
        Account result = accountService.getAccount(id);
        
        if (result == null) {
            throw new AccountNotFoundException(id);
        }
        return accountService.updateAccount(id, account);
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