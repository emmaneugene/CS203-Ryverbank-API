package com.csdg1t3.ryverbankapi.customer;

import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.csdg1t3.ryverbankapi.account.*;
import com.csdg1t3.ryverbankapi.user.*;

/**
 * POJO that stores the details of a given customer in the bank. 
 * Customer is linked to Account in a one-to-many relationship. Each customer must have at least one
 * account
 */
@Entity
public class Customer extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String nric;
    private int phoneNo;
    private String address;
    //private String username;
   // private String password;
    //private String authorities;
    private boolean status;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Account> accounts;

    public Customer() {
        super();
    }

    public Customer(long id, String name, String nric, int phoneNo, String address,String username, 
    String password,String authorities, boolean status) {
        super(username,password,authorities);
        this.id = id;
        this.name = name;
        this.nric = nric;
        this.phoneNo = phoneNo;
        this.address = address;
        // this.username = username;
        // this.password = password;
        // this.authorities = authorities;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNric() {
        return nric;
    }

    public int getPhoneNo() {
        return phoneNo;
    }

    public String getAddress() {
        return address;
    }

    // public String getUsername() {
    //     return username;
    // }

    // public String getPassword() {
    //     return password;
    // }

    // public String getAuthorities() {
    //     return authorities;
    // }

    public boolean getStatus() {
        return status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }
    public void setPhoneNo(int phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setAddress(String address) {
        this.address =  address;
    }

    // public void setUsername(String username) {
    //     this.username = username;
    // }

    // public void setPassword(String password) {
    //     this.password = password;
    // }

    // public void setAuthorities(String authorities) {
    //     this.authorities = authorities;
    // }

    public void setStatus(boolean status) {
        this.status = status;
    }
}