package com.csdg1t3.ryverbankapi.account;

import javax.persistence.*;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.Valid;

import java.util.List;
import java.util.ArrayList;
import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.trade.*;

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

    @NotNull(message = "Customer should not be null")
    private Long customer_id;

    @NotNull(message = "Balance should not be null")
    private Double balance;

    @NotNull(message = "Available balance should not be null")
    private Double available_balance;
   
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Transfer> sentTransfers = new ArrayList<Transfer>();
    
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Transfer> receivedTransfers = new ArrayList<Transfer>();

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<Trade> accountTrades = new ArrayList<Trade>();

    /**
     * Empty constructor for class Account.
     */
    public Account() {}

    
    /**
     * Full field constructor for class Account.
     * @param id The unique ID number for the account.
     * @param customer The customer who owns the account.
     * @param customerId The unique ID of the customer who owns the account.
     * @param balance The customer's total amount of money.
     * @param availableBalance The customer's available amount of money.
     */
    public Account(Long id, User customer, Long customer_id, Double balance, 
    Double available_balance) {
        this.id = id;
        this.cust = customer;
        this.customer_id = customer_id;
        this.balance = balance;
        this.available_balance = available_balance;
    }

    public Long getId() { return id; }

    @JsonIgnore
    public User getCustomer() { return cust; }

    public Long getCustomer_id() { return customer_id; }

    public Double getBalance() { return balance; }

    public Double getAvailable_balance() { return available_balance; }

    @JsonIgnore
    public List<Transfer> getSentTransfers() { return sentTransfers; }

    @JsonIgnore
    public List<Transfer> getReceivedTransfers() { return receivedTransfers; }

    @JsonIgnore
    public List<Trade> getTrades() { return accountTrades; }

    public void setId(Long id) { this.id = id; }

    public void setCustomer(User customer) { this.cust = customer; }

    public void setCustomerId(Long customer_id) { this.customer_id = customer_id; }

    public void setBalance(Double balance) { this.balance = balance; }

    public void setAvailable_balance(Double available_balance) { 
        this.available_balance = available_balance;
    }

    public void setSentTransfers(List<Transfer> sentTransfers) { 
        this.sentTransfers = sentTransfers; 
    }

    public void setReceivedTransfers(List<Transfer> receivedTransfers) { 
        this.receivedTransfers = receivedTransfers; 
    }

    public void setAccountTrades(List<Trade> accountTrades) { this.accountTrades = accountTrades; }
    @Override
    public String toString() {
        return String.format("Account[id=%d, customerId=%d, balance=%lf, availableBalance=%lf]", 
        id, cust.getId(), balance, available_balance);
    }
    
}