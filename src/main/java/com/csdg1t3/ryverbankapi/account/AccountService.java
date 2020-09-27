package com.csdg1t3.ryverbankapi.account;

import java.util.List;
/**
 * AccountService
 */
public interface AccountService {
    List<Account> listAccounts();
    Account getAccount(Long id);
    Account addAccount(Account account);
    Account updateAccountBalance(Long id, Double balance);
    Account updateAccountAvailableBalance(Long id, Double balance);
    void deleteAccount(Long id); 
}