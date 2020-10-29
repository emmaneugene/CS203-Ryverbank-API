package com.csdg1t3.ryverbankapi.trade;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.csdg1t3.ryverbankapi.account.*;
import com.csdg1t3.ryverbankapi.user.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "action should not be null")
    private String action;

    @NotNull(message = "symbol should not be null")
    private String symbol;

    @NotNull(message = "quantity should not be null")
    @Positive(message = "quantity should be positive")
    private int quantity;

    private Double bid;

    private Double ask;

    private Double avg_price;

    private int filled_quantity;

    @NotNull(message = "date should not be null")
    private long date;

    @NotNull(message = "account_id should not be null")
    private Long account_id; 

    @ManyToOne
    @JoinColumn(name = "acc_id", nullable = true)
    @JsonIgnore
    private Account account;

    @NotNull(message = "customer_id should not be null")
    private Long customer_id;

    @ManyToOne
    @JoinColumn(name = "cust_id", nullable = true)
    @JsonIgnore
    private User customer;

    @NotNull(message = "status should not be null")
    private String status;

    @JsonIgnore
    private boolean processed;

    public Trade() {}

    public Trade(Long id, String action, String symbol, int quantity, Double bid, Double ask, 
    double avg_price, int filled_quantity, long date, Account account, User customer, 
    String status, boolean processed){
        this.id = id;
        this.action = action;
        this.symbol = symbol;
        this.quantity = quantity;
        this.bid = bid;
        this.ask = ask;
        this.avg_price = avg_price;
        this.filled_quantity = filled_quantity;
        this.date = date;
        this.account_id = account.getId();
        this.account = account;
        this.customer_id = customer.getId();
        this.customer = customer;
        this.status = status;
        this.processed = processed;
    }

    public Long getId() { return id; }
    
    public String getAction() { return action; }

    public String getSymbol() { return symbol; }

    public int getQuantity() { return quantity; }

    public Double getBid() { return bid; }

    public Double getAsk() { return ask; }

    public Double getAvg_price() { return avg_price; }

    public int getFilled_quantity() { return filled_quantity; }

    public long getDate() { return date; }

    public Long getAccount_id() { return account_id; }

    @JsonIgnore
    public Account getAccount() { return account; }

    public Long getCustomer_id() { return customer_id; }

    public User getCustomer() { return customer; }

    public String getStatus() { return status; }

    @JsonIgnore
    public boolean getProcessed() { return processed; }

    @JsonIgnore
    public boolean isFilled() { return filled_quantity == quantity; }

    @JsonIgnore
    public int getRemaining_quantity() { return quantity - filled_quantity; }

    public void setId(long id) { this.id = id; }
    
    public void setAction(String action) { this.action = action; }

    public void setSymbol(String symbol) { this.symbol = symbol; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void setBid(Double bid) { this.bid = bid; }

    public void setAsk(Double ask) { this.ask = ask; }

    public void setAvg_price(double avg_price) { this.avg_price = avg_price; }

    public void setFilled_quantity(int filled_quantity) { this.filled_quantity = filled_quantity; }

    public void setDate(long date) { this.date = date; }

    public void setAccount_id(Long account_id) { this.account_id = account_id; }

    public void setAccount(Account account) { this.account = account; }

    public void setCustomer_id(Long customer_id) { this.customer_id = customer_id; }

    public void setCustomer(User customer) { this.customer = customer; }

    public void setStatus(String status) { this.status = status; }

    public void setProcessed(boolean processed) { this.processed = processed; }
}