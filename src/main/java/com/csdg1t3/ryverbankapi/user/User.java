package com.csdg1t3.ryverbankapi.user;

import java.util.Collection;
import java.util.Arrays;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.csdg1t3.ryverbankapi.account.*;
import com.csdg1t3.ryverbankapi.user.*;


/**
 * POJO that stores the details of a given customer in the bank. 
 * Customer is linked to Account in a one-to-many relationship. Each customer must have at least one
 * account
 */
@Entity

public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "name should not be null")
    @Size(min = 5, max = 150, message = "name should be between 5 and 150 characters")
    private String name;

    @NotNull(message = "nric should not be null")
    @Size(min = 9, max = 9, message = "Nric should be exactly 9 characters")
    private String nric;

    // @NotEmpty(message = "phone no should not be null")
    //@Length(min = 8, max = 8, message = "phone No should be exactly 8 characters")
    private int phoneNo;

    @NotNull(message = "address should not be null")
    @Size(min = 5, max = 200, message = "address should be between 5 and 200 characters")
    private String address;

    @NotNull(message = "Username should not be null")
    @Size(min = 5, max = 20, message = "Username should be between 5 and 20 characters")
    private String username;

    @NotNull(message = "password should not be null")
    @Size(min = 8, max =100, message = "password should be at least 8 characters")
    private String password;

    @NotNull(message = "authorities should not be null")
    private String authorities;

    // can be null if manager or analyst 
    @NotNull(message = "status should not be null")
    private boolean status;
    
    @OneToMany(mappedBy = "cust", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Account> accounts;

    public User() {
    }

    public User(long id, String name, String nric, int phoneNo, String address,String username, 
    String password,String authorities, boolean status) {
        this.id = id;
        this.name = name;
        // nric not valid
        // if(true){
        //     throw new NricNotValidException();
        // }
        this.nric = nric;

        // check for phone valid 
        // if(true){
        //     throw new phoneNoNotValidException();
        // }
        this.phoneNo = phoneNo;
        this.address = address;

        //check if username is unique 
        // if(true){
        //     throw new UserNotValidException();
        // } -- shld check in control or smth, not here 
        this.username = username;
        this.password = password;

        //check if authorities are valid 
        // if(true){
        //     throw new authoritiesNotValidException();
        // }
        this.authorities = authorities;


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

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getStringAuthorities() {
        return authorities;
    }

     /* Return a collection of authorities (roles) granted to the user.
    */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //String au = Arrays.toString(authorities);
        return Arrays.asList(new SimpleGrantedAuthority(authorities));
    }

    
    public boolean getStatus() {
        return status;
    }

    public String toString(){
        return "" + getUsername() + " " + getPassword() +" " + getAuthorities();
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}