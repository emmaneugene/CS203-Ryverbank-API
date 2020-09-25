package com.csdg1t3.ryverbankapi.transfer;

public class Transfer {
    private long id;
    private long senderId;
    private long receiverId;
    private double amount;

    // @ManyToOne
    // @JoinColumn(name = "account_id", nullable = false)
    public Transfer(long id, long senderId, long receiverId, double amount) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public long getSenderId() {
        return senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public double getAmount() {
        return amount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}