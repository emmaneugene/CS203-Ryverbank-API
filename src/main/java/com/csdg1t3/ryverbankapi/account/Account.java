package com.csdg1t3.ryverbankapi.account;


import javax.persistence.*;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.Valid;

import java.util.List;
import java.util.ArrayList;
import com.csdg1t3.ryverbankapi.user.*;

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
    @JoinColumn(name = "cust_id", nullable = false)
    @JsonIgnore
    private User cust;

    @NotNull(message = "customer should not be null")
    private Long customerId;

    @NotNull(message = "balance should not be null")
    private Double balance;

    @NotNull(message = "balance should not be null")
    private Double availableBalance;
   
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Transfer> sentTransfers = new ArrayList<Transfer>();
    
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Transfer> receivedTransfers = new ArrayList<Transfer>();

    /**
     * Default constructor for class Account.
     */
    public Account() {}

    /**
     * Another constructor for class Account.
     * @param id The unique ID number for the account.
     * @param customer The customer who owns the account.
     * @param customerId The unique ID of the customer who owns the account.
     * @param balance The customer's total amount of money.
     * @param availableBalance The customer's available amount of money.
     */
    public Account(Long id, User customer, Long customerId, Double balance, 
    Double availableBalance) {
        this.id = id;
        this.cust = customer;
        this.customerId = customerId;
        this.balance = balance;
        this.availableBalance = availableBalance;
    }

    public Long getId() { return id; }

    @JsonIgnore
    public User getCustomer() { return cust; }

    public Long getCustomerId() { return customerId; }

    public Double getBalance() { return balance; }

    public Double getAvailableBalance() { return availableBalance; }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void setAvailableBalance(Double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public void setCustomer(User customer) {
        this.cust = customer;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    @Override
    public String toString() {
        return String.format("Account[id=%d, customerId=%d, balance=%lf, availableBalance=%lf]", id, cust.getId(), balance, availableBalance);
    }
    
}