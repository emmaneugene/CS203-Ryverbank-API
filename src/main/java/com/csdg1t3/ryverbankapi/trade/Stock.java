package com.csdg1t3.ryverbankapi.trade;

import javax.persistence.*;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.Valid;


@Entity
public class Stock {
    @Id
    private String symbol;

    @NotNull(message = "last price should not be null")
    private double last_price;

    @NotNull(message = "bid volume should not be null")
    private int bid_volume;

    @NotNull(message = "bid should not be null")
    private double bid;

    @NotNull(message = "ask_volume should not be null")
    private int ask_volume;

    @NotNull(message = "ask should not be null")
    private double ask; 

    public Stock(){}

    public Stock(String symbol, double last_price, int bid_volume, double bid, int ask_volume, double ask){
        this.symbol = symbol;
        this.last_price = last_price;
        this.bid_volume = bid_volume;
        this.bid = bid;
        this.ask_volume = ask_volume;
        this.ask = ask;
    }

    public String getSymbol(){
        return symbol;
    }

    public double getLast_price(){
        return last_price;
    }

    public int getBid_volume(){
        return bid_volume;
    }

    public double getBid(){
        return bid;
    }

    public int getAsk_volume(){
        return ask_volume;
    }

    public double getAsk(){
        return ask;
    }

    public void setSymbol(String symbol){
        this.symbol = symbol;
    }

    public void setLast_price(double last_price){
        this.last_price = last_price;
    }

    public void setBid_volume(int bid_volume){
        this.bid_volume = bid_volume;
    }

    public void setBid(double bid){
        this.bid = bid;
    }

    public void setAsk_volume(int ask_volume){
        this.ask_volume = ask_volume;
    }

    public void setAsk(double ask){
        this.ask = ask;
    }

    @Override
    public String toString() {
        return String.format("Symbol[id=%s, Last Price=%f, Bid Volume=%d, Bid = %f, Ask Volume = %d, Ask=%f]", symbol, last_price, bid_volume, bid, ask_volume, ask);
    }

}
