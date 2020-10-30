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
import com.csdg1t3.ryverbankapi.trade.*;



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
    private Long id;

    @NotNull(message = "Name should not be null")
    @Size(min = 5, max = 150, message = "name should be between 5 and 150 characters")
    private String full_name;

    @NotNull(message = "Nric should not be null")
    @Size(min = 9, max = 9, message = "nric should be exactly 9 characters")
    private String nric;

    @NotNull(message = "Phone number should not be null")
    @Size(min = 8, max = 8, message = "Phone number should be exactly 8 characters")
    private String phone;

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
    @NotNull(message = "Active boolean should not be null")
    private Boolean active;
    
    @OneToMany(mappedBy = "cust", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Account> accounts;

    @OneToOne
    @JoinColumn(name = "portfolio_id")
    @JsonIgnore
    private Portfolio portfolio;

    public User() {}

    public User(Long id, String full_name, String nric, String phone, String address, String username, 
    String password, String authorities, Boolean active) {
        this.id = id;
        this.full_name = full_name;
        this.nric = nric;
        this.phone = phone;
        this.address = address;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.active = active;
    }

    public Long getId() { return id; }

    public String getFull_name() { return full_name; }

    public String getNric() { return nric; }

    public String getPhone() { return phone; }

    public String getAddress() { return address; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    @JsonIgnore
    public String getStringAuthorities() { return authorities; }

     /* 
     Return a collection of authorities (roles) granted to the user.
    */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> result = new ArrayList<>();
        for (String authority : authorities.split(",")) {
            result.add(new SimpleGrantedAuthority(authority));
        }
        return result;
    }

    public Boolean getActive() { return active; }

    public List<Account> getAccounts() { return accounts; }

    public Portfolio getPortfolio() { return portfolio; }

    public void setId(Long id) { this.id = id; }

    public void setName(String full_name) { this.full_name = full_name; }

    public void setNric(String nric) { this.nric = nric; }

    public void setPhone(String phone) { this.phone = phone; }

    public void setAddress(String address) { this.address =  address; }

    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password) { this.password = password; }

    public void setAuthorities(String authorities) { this.authorities = authorities; }

    public void setActive(Boolean active) { this.active = active; }

    public void setAccounts(List<Account> accounts) { this.accounts = accounts; }

    public void setPortfolio(Portfolio portfolio) { this.portfolio = portfolio; }

    public String toString() { 
        return username + " " + password + " " + authorities;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() { return active; }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() { return active; }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() { return active; }

    @Override
    @JsonIgnore
    public boolean isEnabled() { return active; }
}