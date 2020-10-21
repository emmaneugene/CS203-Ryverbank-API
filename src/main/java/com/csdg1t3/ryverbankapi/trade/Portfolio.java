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
    @NotNull(message = "customerId should not be null")
    private Long customerId;

    @OneToOne
    @JoinColumn(name = "cust_id", nullable = false)
    @JsonIgnore
    private User customer;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    private List<Asset> assets;

    @NotNull (message = "unrealized_gain_loss should not be null")
    private double unrealized_gain_loss;

    @NotNull (message = "total_gain_loss should not be null")
    private double total_gain_loss;

    public Portfolio() {};

    public Portfolio(Long customerId, User customer, List<Asset> assets, double unrealized_gain_loss, double total_gain_loss) {
        this.customerId = customerId;
        this.customer = customer;
        this.assets = assets;
        this.unrealized_gain_loss = unrealized_gain_loss;
        this.total_gain_loss = total_gain_loss;
    }

    public Long getCustomerId() { return customerId; }

    @JsonIgnore
    public User getCustomer() { return customer; }

    public List<Asset> getAssets() { return assets;}

    public double getUnrealizedGainLoss() { return unrealized_gain_loss; }

    public double getTotalGainLoss() { return total_gain_loss; }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public void setUnrealizedGainLoss() {
        if (assets == null) {
            this.unrealized_gain_loss = 0;
            return;
        }
        
        double unrealized_gain_loss = 0;
        for (Asset a : this.assets) {
            unrealized_gain_loss += a.getGainLoss();
        }

        this.unrealized_gain_loss = unrealized_gain_loss;
    }

    public void setTotalGainLoss(double total_gain_loss) {
        this.total_gain_loss = total_gain_loss;
    }

    public String printAssets() {
        String result = "";
        for (Asset a : assets) {
            result += a.toString();
            result += "\n";
        }

        return result;
    }

    @Override
    public String toString() {
        return String.format("Portfolio[customerId=%d, assets[\n        %s\n    ], unrealized_gain_loss=%.2lf, total_gain_loss=%.2lf", this.customerId, printAssets(), this.unrealized_gain_loss, this.total_gain_loss);
    }

}
