package com.csdg1t3.ryverbankapi.account;

/**
 * Account
 */
public class Account {
    private Long id;
    private Long customerId;
    private Double balance;
    private Double availableBalance;

    public Account() {}

    public Account(Long id, Long customerId, Double balance, Double availableBalance) {
        this.id = id;
        this.customerId = customerId;
        this.balance = balance;
        this.availableBalance = availableBalance;
    }

    public Long getId() { return id; }

    public Long getCustomerId() { return customerId; }

    public Double getBalance() { return balance; }

    public Double getAvailableBalance() { return availableBalance; }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void setAvailableBalance(Double availableBalance) {
        this.availableBalance = availableBalance;
    } 
    
}