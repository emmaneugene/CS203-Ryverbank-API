package com.csdg1t3.ryverbankapi.trade;
import java.util.Date;


public class Trade {
    private long tradeID;
    private String action;
    private String symbol;
    private long quantity;
    private double bid;
    private double ask;
    private double avg_price;
    private int filled_quantity;
    private Date date;
    private long account_id;
    private long customer_id;
    private String status;

    public Trade(long tradeID, String action, String symbol, long quantity, double bid, double ask, double avg_price, int filled_quantity, Date date, long account_id, long customer_id, String status){
        this.tradeID = tradeID;
        this.action = action;
        this.symbol = symbol;
        this.quantity = quantity;
        this.bid = bid;
        this.ask = ask;
        this.avg_price = avg_price;
        this.filled_quantity = filled_quantity;
        this.date = date;
        this.account_id = account_id;
        this.customer_id = customer_id;
        this.status = status;
    }

    public long getTradeID(){
        return tradeID;
    }
    
    public String getAction(){
        return action;
    }

    public long getQuantity(){
        return quantity;
    }

    public double getBid(){
        return bid;
    }

    public double getAsk(){
        return ask;
    }

    public double getAvg_price(){
        return avg_price;
    }

    public int getFilled_quantity(){
        return filled_quantity;
    }

    public Date getDate(){
        return date;
    }

    public String getStatus(){
        return status;
    }

    public void setTradeID(long tradeID){
        this.tradeID = tradeID;
    }
    
    public void setAction(String action){
        this.action = action;
    }

    public void setQuantity(long quantity){
        this.quantity = quantity; 
    }

    public void setBid(double bid){
        this.bid = bid;
    }

    public void setAsk(double ask){
        this.ask = ask;
    }

    public void setAvg_price(double avg_price){
        this.avg_price = avg_price;
    }

    public void setFilled_quantity(int filled_quantity){
        this.filled_quantity = filled_quantity;
    }

    public void setDate(Date date){
        this.date = date;
        
    }

    public void setStatus(String status){
        this.status = status;
    }
}