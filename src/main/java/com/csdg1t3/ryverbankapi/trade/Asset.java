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

    @ManyToOne
    @JoinColumn(name = "portfolio", nullable = false)
    @JsonIgnore
    private Portfolio portfolio;

    @NotNull(message = "quantity should not be null")
    @Positive(message = "quantity should be positive")
    private int quantity;

    @NotNull(message = "avg_price should not be null")
    private double avg_price;

    @NotNull(message = "currentPrice should not be null")
    private double current_price;

    public Asset() {};

    public Asset(Long id, Stock stock, int quantity, double avg_price, double current_price) {
        this.id = id;
        this.stock = stock;
        this.quantity = quantity;
        this.avg_price = avg_price;
        this.current_price = current_price;
    }

    @JsonIgnore
    public Long getId() { return id; }

    @JsonIgnore
    public Stock getStock() { return stock;}

    public String getCode() { return stock.getSymbol(); }
    
    @JsonIgnore
    public Portfolio getPortfolio() { return portfolio; }

    @JsonIgnore
    public Long getPortfolio_id() { return portfolio.getCustomer_id(); }

    public int getQuantity() { return quantity; }

    public double getAvg_price() { return avg_price; }

    public double getCurrent_price() { return current_price; }

    public double getValue() { return current_price * quantity; }

    public double getGain_loss() { return (current_price - avg_price) * quantity; }

    public void setId(Long id) {
        this.id = id;
    }
    
    public void setStock(Stock stock) {
        this.stock = stock;
    }


    public void setPortfolio (Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setAvg_price(double avg_price) {
        this.avg_price = avg_price;
    }

    public void setCurrent_price(double current_price) {
        this.current_price = current_price;
    }

    @Override
    public String toString() {
        return String.format("Asset[symbol=%d, quantity=%d, avg_price=%.2lf, currentPrice=%.2lf, value=%.2lf, gain_loss=%.2lf",
        stock.getSymbol(), quantity, avg_price, current_price, getValue(), getGain_loss());
    }

}
