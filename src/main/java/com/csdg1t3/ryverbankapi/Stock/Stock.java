package com.csdg1t3.ryverbankapi.stock;

public class Stock {
    private String symbol;
    private double last_price;
    private int bid_volume;
    private double bid;
    private int ask_volume;
    private double ask; 

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

}
