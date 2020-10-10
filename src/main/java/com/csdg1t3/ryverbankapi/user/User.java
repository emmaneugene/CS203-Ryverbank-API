package com.csdg1t3.ryverbankapi.user;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.csdg1t3.ryverbankapi.account.*;



/**
 * POJO that stores the details of a given customer in the bank. 
 * Customer is linked to Account in a one-to-many relationship. Each customer must have at least one
 * account
 */
@Entity
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Name should not be null")
    @Size(min = 5, max = 150, message = "name should be between 5 and 150 characters")
    private String name;

    @NotNull(message = "Nric should not be null")
    @Size(min = 9, max = 9, message = "nric should be exactly 9 characters")
    private String nric;

    @NotNull(message = "phone no should not be null")
    @Size(min = 8, max = 8, message = "Phone number should be exactly 8 characters")
    private int phoneNo;

    @NotNull(message = "Address should not be null")
    @Size(min = 5, max = 200, message = "address should be between 5 and 200 characters")
    private String address;

    @NotNull(message = "Username should not be null")
    @Size(min = 5, max = 20, message = "Username should be between 5 and 20 characters")
    private String username;

    @NotNull(message = "Password should not be null")
    @Size(min = 8, max =100, message = "password should be at least 8 characters")
    private String password;

    @NotNull(message = "Authorities should not be null")
    private String authorities;

    // can be null if manager or analyst 
    @NotNull(message = "Status should not be null")
    private boolean status;
    
    @OneToMany(mappedBy = "cust", cascade = CascadeType.ALL /*, orphanRemoval = true*/)
    @JsonIgnore
    private List<Account> accounts;

    @Transient
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User() {
    }

    public User(long id, String name, String nric, int phoneNo, String address,String username, 
    String password,String authorities, boolean status) {
        // BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.id = id;
        this.name = name;
        this.nric = nric;
        this.phoneNo = phoneNo;
        this.address = address;
        this.username = username;
        this.password = password;
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

    public String[] getStringAuthorities() {
        return authorities;
    }

     /* Return a collection of authorities (roles) granted to the user.
    */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> result = new ArrayList<>();
        for (String authority : authorities) {
            result.add(new SimpleGrantedAuthority(authority));
        }
        return result;
    }

    
    public boolean getStatus() {
        return status;
    }

    public String toString(){
        return username + " " + password + " " + getStringAuthorities();
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
        // BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = authorities;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return status;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}