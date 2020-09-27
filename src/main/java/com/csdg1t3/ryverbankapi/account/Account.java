package com.csdg1t3.ryverbankapi.account;


import javax.persistence.*;
import javax.validation.constraints.*;

import com.csdg1t3.ryverbankapi.customer.*;

/**
 * POJO that stores the details of a customer's bank account
 * Account is linked to Customer in a many-to-one relationship, and each account must have a customer
 * 
 */
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "cust_id", nullable = false)
    private Customer customer;
    private Double balance;
    private Double availableBalance;

    public Account() {}

    public Account(Long id, Customer customer, Double balance, Double availableBalance) {
        this.id = id;
        this.customer = customer;
        this.balance = balance;
        this.availableBalance = availableBalance;
    }

    public Long getId() { return id; }

    public Customer getCustomer() { return customer; }

    public Double getBalance() { return balance; }

    public Double getAvailableBalance() { return availableBalance; }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void setAvailableBalance(Double availableBalance) {
        this.availableBalance = availableBalance;
    }

    @Override
    public String toString() {
        return String.format("Account[id=%d, customerId=%d, balance=%lf, availableBalance=%lf]", id, customer.getId(), balance, availableBalance);
    }
    
}