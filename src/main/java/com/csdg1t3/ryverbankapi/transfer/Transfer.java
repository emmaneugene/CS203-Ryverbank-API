package com.csdg1t3.ryverbankapi.transfer;

import javax.persistence.*;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @JsonIgnore
    private Account sender;
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    @JsonIgnore
    private Account receiver;
    private Long accFrom;
    private Long accTo;
    private Double amount;

    public Transfer() {}

    public Transfer(long id, Account sender, Account receiver, Long accFrom, Long accTo, Double amount) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.accFrom = accFrom;
        this.accTo = accTo;
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

    public Long getFrom() {
        return accFrom;
    }

    public Long getTo() {
        return accTo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    public void setFrom(Long accFrom) {
        this.accFrom = accFrom;
    }

    public void setTo(Long accTo) {
        this.accTo = accTo;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}