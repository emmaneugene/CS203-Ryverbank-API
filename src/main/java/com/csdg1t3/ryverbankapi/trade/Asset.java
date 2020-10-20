package com.csdg1t3.ryverbankapi.trade;

import javax.persistence.*;
import javax.sound.sampled.Port;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.Valid;

import java.util.List;
import java.util.ArrayList;
import com.csdg1t3.ryverbankapi.user.*;

@Entity 
public class Asset {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assetId;

    @JsonIgnore
    @NotNull(message = "customer should not be null")
    private Long customerId;

    @ManyToOne
    @JoinColumn(name = "symbol", nullable = false)
    @JsonIgnore
    private Stock stock;

    @ManyToOne
    @JoinColumn(name = "cust_id", nullable = false)
    @JsonIgnore
    private User cust;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private Portfolio portfolio;

    @NotNull(message = "symbol should not be null")
    private String stockSymbol;

    @NotNull(message = "quantity should not be null")
    @Positive(message = "quanity should be more than zero")
    private int quantity;

    @NotNull(message = "avg_price should not be null")
    @JsonIgnore
    private double avg_price;

    @NotNull(message = "current_price should not be null")
    @JsonIgnore
    private double current_price;

    @NotNull(message = "value should not be null")
    @JsonIgnore
    private double value;

    @NotNull(message = "gain_loss should not be null")
    @JsonIgnore
    private double gain_loss;

    public Asset() {};

    public Asset(Long assetId, Long customerId, User customer, Stock stock, String stockSymbol, int quantity, double avg_price, double current_price) {
        this.assetId = assetId;
        this.customerId = customerId;
        this.cust = customer;
        this.stock = stock;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.avg_price = avg_price;
        this.current_price = current_price;
        this.value = getValue();
        this.gain_loss = getGainLoss();
    }

    public Long getAssetId() { return assetId; }

    public Long getCustomerId() { return customerId; }

    public User getCustomer() { return cust; }

    public Stock getStock() { return stock;}

    public String getSymbol() { return stockSymbol; }

    public int getQuantity() { return quantity; }

    public double getAvgPrice() { return avg_price; }

    public double getCurrentPrice() { return stock.getAsk(); }

    public double getValue() { return value; }

    public double getGainLoss() { return gain_loss; }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setCustomer(User customer) {
        this.cust = customer;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public void setSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        setValue();
        setGainLoss();
    }

    public void setAvgPrice(double avg_price) {
        this.avg_price = avg_price;
        setGainLoss();
    }

    public void setCurrentPrice() {
        this.current_price = this.stock.getAsk();
        setValue();
        setGainLoss();
    }

    public void setValue() {
        this.value = this.current_price * this.quantity;
    }

    public void setGainLoss() {
        this.gain_loss = this.value - (this.quantity * this.avg_price);
    }

    @Override
    public String toString() {
        return String.format("Asset[symbol=%d, quantity=%d, avg_price=%.2lf, current_price=%.2lf, value=%.2lf, gain_loss=%.2lf", this.stockSymbol, this.quantity, this.avg_price, this.current_price, this.value, this.gain_loss);
    }

}
