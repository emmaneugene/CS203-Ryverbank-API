package com.csdg1t3.ryverbankapi.account;


import javax.persistence.*;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import com.csdg1t3.ryverbankapi.customer.*;
import com.csdg1t3.ryverbankapi.transfer.*;

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
    private Customer customer;
    private Long customerId;
    private Double balance;
    private Double availableBalance;
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Transfer> sentTransfers;
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Transfer> receivedTransfers;

    public Account() {}

    public Account(Long id, Customer customer, Long customerId, Double balance, 
    Double availableBalance, List<Transfer> sentTransfers, List<Transfer> receivedTransfers) {
        this.id = id;
        this.customer = customer;
        this.customerId = customerId;
        this.balance = balance;
        this.availableBalance = availableBalance;
        this.sentTransfers = sentTransfers;
        this.receivedTransfers = receivedTransfers;
    }

    public Long getId() { return id; }

    public Customer getCustomer() { return customer; }

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

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    @Override
    public String toString() {
        return String.format("Account[id=%d, customerId=%d, balance=%lf, availableBalance=%lf]", id, customer.getId(), balance, availableBalance);
    }
    
}