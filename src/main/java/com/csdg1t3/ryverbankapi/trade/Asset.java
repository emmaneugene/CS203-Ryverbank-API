package com.csdg1t3.ryverbankapi.trade;

import javax.persistence.*;
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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "symbol", nullable = false)
    @JsonIgnore
    private Stock stock;

    @NotNull(message = "symbol should not be null")
    private String stockSymbol;

    @ManyToOne
    @JoinColumn(name = "portfolio", nullable = false)
    @JsonIgnore
    private Portfolio portfolio;

    @NotNull(message = "portfolioId should not be null")
    @JsonIgnore
    private Long portfolioId;

    @NotNull(message = "quantity should not be null")
    @Positive(message = "quantity should be positive")
    private int quantity;

    @NotNull(message = "avgPrice should not be null")
    private double avgPrice;

    @NotNull(message = "currentPrice should not be null")
    private double currentPrice;

    public Asset() {};

    public Asset(Long id, Stock stock, String stockSymbol, int quantity, double avgPrice, double currentPrice) {
        this.id = id;
        this.stock = stock;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
        this.currentPrice = currentPrice;
    }

    public Long getId() { return id; }

    public Stock getStock() { return stock;}

    public String getSymbol() { return stockSymbol; }

    public int getQuantity() { return quantity; }

    public double getAvgPrice() { return avgPrice; }

    public double getCurrentPrice() { return currentPrice; }

    public double getValue() { return currentPrice * quantity; }

    public double getGainLoss() { return (currentPrice - avgPrice) * quantity; }

    public void setId(Long id) {
        this.id = id;
    }
    
    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public void setSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    @Override
    public String toString() {
        return String.format("Asset[symbol=%d, quantity=%d, avgPrice=%.2lf, currentPrice=%.2lf, value=%.2lf, gain_loss=%.2lf", this.stockSymbol, this.quantity, this.avgPrice, this.currentPrice, getValue(), getGainLoss());
    }

}
