package com.csdg1t3.ryverbankapi.transfer;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.csdg1t3.ryverbankapi.account.*;

/**
 * POJO that stores the details of a bank transfer between two accounts.
 * As such, the class has to store 2 Account classes, sender and receiver
 */
@Entity
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Account sender;
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Account receiver;
    private double amount;

    public Transfer() {}

    public Transfer(long id, Account sender, Account receiver, double amount) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public Account getSender() {
        return sender;
    }

    public Account getReceiver() {
        return receiver;
    }

    public double getAmount() {
        return amount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}