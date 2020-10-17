package com.csdg1t3.ryverbankapi.trade;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String action;
    private String symbol;
    private Long quantity;
    private Double bid;
    private Double ask;
    private Double avg_price;
    private Long filledQuantity;
    private long date;
    private Long accountId;
    private Long customerId;
    private String status;

    public Trade(long Id, String action, String symbol, long quantity, double bid, double ask, double avg_price, long filledQuantity, long date, long accountId, long customerId, String status){
        this.Id = Id;
        this.action = action;
        this.symbol = symbol;
        this.quantity = quantity;
        this.bid = bid;
        this.ask = ask;
        this.avg_price = avg_price;
        this.filledQuantity = filledQuantity;
        this.date = date;
        this.accountId = accountId;
        this.customerId = customerId;
        this.status = status;
    }

    public Long getTradeID() {
        return Id;
    }
    
    public String getAction() {
        return action;
    }

    public Long getQuantity() {
        return quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public Double getBid() {
        return bid;
    }

    public Double getAsk() {
        return ask;
    }

    public Double getAveragePrice() {
        return avg_price;
    }

    public Long getFilledQuantity() {
        return filledQuantity;
    }

    public long getDate() {
        return date;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getCustomerId() {
        return customerId;
    }
    
    public String getStatus() {
        return status;
    }

    public void setTradeID(long Id) {
        this.Id = Id;
    }
    
    public void setAction(String action) {
        this.action = action;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity; 
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public void setAvg_price(double avg_price) {
        this.avg_price = avg_price;
    }

    public void setfilledQuantity(long filledQuantity) {
        this.filledQuantity = filledQuantity;
    }

    public void setDate(long date) {
        this.date = date;
        
    }

    public void setAccountId(Long accountId) {
        
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}