package com.csdg1t3.ryverbankapi.account;


import javax.persistence.*;
import javax.validation.constraints.*;

import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.user.UserNotFoundException;

import javax.validation.Valid;


/**
 * POJO that stores the details of a customer's bank account
 * Account is linked to Customer in a many-to-one relationship, and each account must have a 
 * customer
 * 
 */
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "customer should not be null")
    private User customer;

    @NotNull(message = "balance should not be null")
    @Size(min = 50000, max = 1000000000, message = "initial balance must be more than 50000")
    private Double balance;

    @NotNull(message = "balance should not be null")
    @Size(min = 50000, max = 1000000000, message = "initial available balance must be more than 50000")
    private Double availableBalance;

    public Account() {}

    public Account(Long id, User customer, Double balance, Double availableBalance) {
        this.id = id;
        if(!customer.getStringAuthorities().contains("ROLE_USER")){
            throw new UserNotValidException("Only customers can set up accounts");
        }
        this.customer = customer;
        this.balance = balance;
        this.availableBalance = availableBalance;
    }

    public Long getId() { return id; }

    public User getCustomer() { return customer; }

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