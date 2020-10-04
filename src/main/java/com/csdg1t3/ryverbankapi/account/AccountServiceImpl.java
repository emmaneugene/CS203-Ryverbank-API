package com.csdg1t3.ryverbankapi.account;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/**
 * Concrete class that implements AccountService
 */
@Service
public class AccountServiceImpl implements AccountService {
    private AccountRepository accounts;

    public AccountServiceImpl(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Override
    public List<Account> listAccounts() {
        return accounts.findAll();
    }

    public List<Account> listAccountsForUser(Long id) {
        Optional<List<Account>> result = accounts.findAllByCustomerId(id);
        if (result.isPresent()) {
            return result.get();
        }
        return null;    
    }

    @Override
    public Account getAccount(Long id) {
        Optional<Account> result = accounts.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    @Override
    public Account addAccount(Account newAccount) {
        return accounts.save(newAccount);
    }

    @Override
    public Account updateAccount(Long id, Account account) {
        Optional<Account> result = accounts.findById(id);
        if (result.isPresent()) {
            return accounts.save(account);
        }
        return null;
    }


    @Override
    public void deleteAccount(Long id) {
        accounts.deleteById(id);
    }
}