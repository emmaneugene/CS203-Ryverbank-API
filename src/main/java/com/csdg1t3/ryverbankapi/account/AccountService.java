package com.csdg1t3.ryverbankapi.account;

import java.util.List;
/**
 * Convenience interface for a service class allowing from AccountController to read and modify data
 * from AccountRepository
 */
public interface AccountService {
    List<Account> listAccounts();
    List<Account> listAccountsForUser(Long id);
    Account getAccount(Long id);
    Account addAccount(Account account);
    Account updateAccount(Long id, Account account);
    void deleteAccount(Long id); 
}