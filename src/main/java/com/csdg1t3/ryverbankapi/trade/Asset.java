package com.csdg1t3.ryverbankapi.trade;

import javax.persistence.*;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.Valid;

import java.util.List;
import java.util.ArrayList;

/**
 * POJO that stores the details of a customer's assets
 * Asset is linked to Portfolio in a many-to-one relationship, and each asset must have a 
 * portfolio
 * 
 */
@Entity 
public class Asset {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @ManyToOne
    @JoinColumn(name = "portfolio", nullable = false)
    @JsonIgnore
    private Portfolio portfolio;

    @NotNull(message = "Quantity should not be null")
    @Positive(message = "Quantity should be positive")
    private int quantity;

    @NotNull(message = "Quantity on hold should not be null")
    @JsonIgnore
    private int available_quantity;

    @NotNull(message = "Avg price should not be null")
    private double avg_price;

    @NotNull(message = "Current price should not be null")
    private double current_price;

    /**
     * Default constructor for class Asset.
     */
    public Asset() {};

    /**
     * Another constructor for class Asset
     * @param id The unique ID number for the asset
     * @param code The code of the asset
     * @param portfolio The portfolio which the asset belongs to
     * @param quantity The quantity of the asset
     * @param available_quantity The available quantity of the asset
     * @param avg_price The average price of the asset
     * @param current_price The current price of the asset
     */
    public Asset(Long id, String code, Portfolio portfolio, int quantity, int available_quantity, 
    double avg_price, double current_price) {
        this.id = id;
        this.code = code;
        this.portfolio = portfolio;
        this.quantity = quantity;
        this.available_quantity = available_quantity;
        this.avg_price = avg_price;
        this.current_price = current_price;
    }

    @JsonIgnore
    public Long getId() { return id; }

    public String getCode() { return code; }
    
    @JsonIgnore
    public Portfolio getPortfolio() { return portfolio; }

    public int getQuantity() { return quantity; }

    @JsonIgnore
    public int getAvailable_quantity() { return available_quantity; }

    public double getAvg_price() { return avg_price; }

    public double getCurrent_price() { return current_price; }

    public double getValue() { return current_price * quantity; }

    public double getGain_loss() { return (current_price - avg_price) * quantity; }

    public void setId(Long id) { this.id = id; }
    
    public void setCode(String code) { this.code = code; }

    public void setPortfolio (Portfolio portfolio) { this.portfolio = portfolio; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void setAvailable_quantity(int available_quantity) { this.available_quantity = available_quantity; }

    public void setAvg_price(double avg_price) { this.avg_price = avg_price; }

    public void setCurrent_price(double current_price) { this.current_price = current_price; }

    @Override
    public String toString() {
        return String.format("Asset[symbol=%d, quantity=%d, avg_price=%.2lf, currentPrice=%.2lf, value=%.2lf, gain_loss=%.2lf",
        code, quantity, avg_price, current_price, getValue(), getGain_loss());
    }

}
