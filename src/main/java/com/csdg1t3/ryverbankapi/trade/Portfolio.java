package com.csdg1t3.ryverbankapi.trade;

import javax.persistence.*;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.Valid;

import java.util.List;
import java.util.ArrayList;

import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.trade.*;

@Entity 
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull(message = "customer_id should not be null")
    private Long customer_id;

    @OneToOne
    @JoinColumn(name = "cust_id", nullable = false)
    @JsonIgnore
    private User customer;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    private List<Asset> assets;

    @NotNull(message = "unrealized_gain_loss should not be null")
    private double unrealized_gain_loss;

    @NotNull(message = "total_gain_loss should not be null")
    private double total_gain_loss;

    public Portfolio() {};

    public Portfolio(Long id, User customer, List<Asset> assets, 
    double unrealized_gain_loss, double total_gain_loss) {
        this.id = id;
        this.customer_id = customer.getId();
        this.customer = customer;
        this.assets = assets;
        this.unrealized_gain_loss = unrealized_gain_loss;
        this.total_gain_loss = total_gain_loss;
    }

    @JsonIgnore
    public Long getId() { return id; }

    public Long getCustomer_id() { return customer_id; }

    @JsonIgnore
    public User getCustomer() { return customer; }

    public List<Asset> getAssets() { return assets;}

    public double getUnrealized_gain_loss() { return unrealized_gain_loss; }

    public double getTotal_gain_loss() { return total_gain_loss; }

    public void setId(Long id) { this.id = id; }

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

    public void setTotal_gain_loss(double total_gain_loss) {
        this.total_gain_loss = total_gain_loss;
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
        this.id, assetsToString(), this.unrealized_gain_loss, this.total_gain_loss);
    }

}
