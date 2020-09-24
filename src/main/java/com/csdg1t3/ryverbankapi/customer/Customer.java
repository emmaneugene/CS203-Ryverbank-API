package com.csdg1t3.ryverbankapi.customer;

public class Customer {
    private long custID;
    private String custName;
    private String custNric;
    private int custPhoneNo;
    private String custAddress;
    private String custUsername;
    private String custPassword;
    private String custAuthorities;
    private boolean custStatus;

    public Customer(long custID, String custName, String custNric, int custPhoneNo, String custAddress,String custUsername, String custPassword,String custAuthorities, boolean custStatus){
        this.custID = custID;
        this.custName = custName;
        this.custNric = custNric;
        this.custPhoneNo = custPhoneNo;
        this.custAddress = custAddress;
        this.custUsername = custUsername;
        this.custPassword = custPassword;
        this.custAuthorities = custAuthorities;
        this.custStatus = custStatus;
    }

    public long getCustomerID(){
        return custID;
    }

    public String getCustomerName(){
        return custName;
    }

    public String getCustomerNric(){
        return custNric;
    }

    public int getCustomerPhoneNo(){
        return custPhoneNo;
    }

    public String getCustomerAddress(){
        return custAddress;
    }

    public String getCustomerUsername(){
        return custUsername;
    }

    public String getCustomerPassword(){
        return custPassword;
    }

    public String getCustomerAuthorities(){
        return custAuthorities;
    }

    public boolean getCustomerStatus(){
        return custStatus;
    }

    public void setCustomerID(long custID){
        this.custID = custID;
    }

    public void setCustomerName(String custName){
        this.custName = custName;
    }

    public void setCustomerNric(String custNric){
        this.custNric = custNric;
    }
    public void setCustomerPhoneNo(int custPhoneNo){
        this.custPhoneNo = custPhoneNo;
    }

    public void setCustomerAddress(String custAddress){
        this.custAddress =  custAddress;
    }

    public void setCustomerUsername(String custUsername){
        this.custUsername = custUsername;
    }

    public void setCustomerPassword(String custPassword){
        this.custPassword = custPassword;
    }

    public void setCustomerAuthorities(String custAuthorities){
        this.custAuthorities = custAuthorities;
    }

    public void setCustomerStatus(boolean custStatus){
        this.custStatus = custStatus;
    }
}