package com.csdg1t3.ryverbankapi.trade;

import javax.persistence.*;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import com.csdg1t3.ryverbankapi.user.*;

/**
 * POJO that stores the details of a customer's portfolio
 * Portfolio is linked to Customer in a many-to-one relationship, and each portfolio must have a 
 * customer
 * 
 */
@Entity 
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull(message = "Customer ID should not be null")
    private Long customer_id;

    @OneToOne
    @JoinColumn(name = "cust_id", nullable = false)
    @JsonIgnore
    private User customer;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asset> assets;

    private double unrealized_gain_loss;

    private double realized_gain_loss;

    /**
     * Empty constructor for class Portfolio.
     */
    public Portfolio() {};

    
    /**
     * Full field constructor for class Portfolio.
     * @param id The unique ID number for the portfolio
     * @param customer_id The ID of the customer that owns the portfolio
     * @param customer The customer who owns the portfolio
     * @param assets The assets that the portfolio contains
     * @param unrealized_gain_loss The unrealized gain loss of the customer's assets
     * @param realized_gain_loss The total gain loss of the customer's assets
     */
    public Portfolio(Long id, Long customer_id, User customer, List<Asset> assets, 
    double unrealized_gain_loss, double realized_gain_loss) {
        this.id = id;
        this.customer_id = customer_id;
        this.customer = customer;
        this.assets = assets;
        this.unrealized_gain_loss = unrealized_gain_loss;
        this.realized_gain_loss = realized_gain_loss;
    }

    @JsonIgnore
    public Long getId() { return id; }

    public Long getCustomer_id() { return customer_id; }

    @JsonIgnore
    public User getCustomer() { return customer; }

    public List<Asset> getAssets() { return assets;}

    public double getUnrealized_gain_loss() { return unrealized_gain_loss; }

    @JsonIgnore
    public double getRealized_gain_loss() { return realized_gain_loss; }

    public double getTotal_gain_loss() { return realized_gain_loss + unrealized_gain_loss; }

    public void setId(Long id) { this.id = id; }

    public void setCustomer_id(Long customer_id) { this.customer_id = customer_id; }

    public void setCustomer(User customer) { this.customer = customer; }

    public void setAssets(List<Asset> assets) { this.assets = assets; }

    public void setUnrealized_gain_loss() {
        unrealized_gain_loss = 0;
        if (assets == null) 
            return;
        
        for (Asset asset : assets) {
            unrealized_gain_loss += asset.getGain_loss();
        }
    }

    public void setRealized_gain_loss(double realized_gain_loss) {
        this.realized_gain_loss = realized_gain_loss;
    }

    public String assetsToString() {
        String result = "";
        for (Asset a : assets) {
            result += a.toString();
            result += "\n";
        }

        return result;
    }

    @Override
    public String toString() {
        return String.format("Portfolio[customerId=%d, assets[\n%s\n], unrealized_gain_loss=%.2lf, total_gain_loss=%.2lf",
        this.id, assetsToString(), this.unrealized_gain_loss, this.realized_gain_loss);
    }

}
