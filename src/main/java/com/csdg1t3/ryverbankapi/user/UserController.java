package com.csdg1t3.ryverbankapi.user;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.Valid;

import com.csdg1t3.ryverbankapi.trade.*;


/**
 * Controller that manages HTTP requests and updates data within UserRepository
 */
@RestController
public class UserController {
    private UserRepository userRepo;
    private PortfolioRepository portfolioRepo;
    private BCryptPasswordEncoder encoder;

    public UserController(UserRepository userRepo, PortfolioRepository portfolioRepo, 
    BCryptPasswordEncoder encoder) {
        this.userRepo= userRepo;
        this.portfolioRepo = portfolioRepo;
        this.encoder = encoder;
    }

    /**
     * Retrieve all customers in the bank. The method returns ROLE_MANAGER and ROLE_ANALYST 
     * accounts as well.
     * 
     * This method is only authorised for ROLE_MANAGER, as configured in SecurityConfig. 
     * @return a List of all customers
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/customers")
    public List<User> getCustomers() {
        return userRepo.findAll();
    }

    /**
     * Search for customer with the given ID.
     * If there is no customer with the given ID, throw UserNotFoundException
     * 
     * This method is authorised for the following roles: 
     * ROLE_MANAGER - can view all customer accounts
     * ROLE_USER - can only view their own account
     * @param id ID of the customer to be retrieved.
     * @return Customer with the given ID.
     * @throws UserNotFoundException If user ID is not found.
     * @throws RoleNotAuthorisedException If the role of the user accessing is not ROLE_MANAGER
     *                                    or the username is not the same as the user ID being accessed.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/customers/{id}")
    public User getCustomer(@PathVariable Long id) {
        Optional<User> result = userRepo.findById(id);
        if (!result.isPresent())
            throw new UserNotFoundException(id);

        User user = result.get();
        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
        
        if (!uDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) 
            && !user.getUsername().equals(uDetails.getUsername())) 
            throw new RoleNotAuthorisedException("You cannot view other customer details");
                
        return user;
    }

    
    /**
     * Creates a new customer in the bank. In addition to basic field validation, the controller 
     * must verify additional fields:
     * 1. Username must be unique
     * 2. NRIC must be valid 
     * 3. Phone number must be valid
     * If any data is not valid, the method throws a UserNotValidException
     * 
     * This controller also creates an empty portfolio for the user if the user is ROLE_USER
     * 
     * This method is only authorised for ROLE_MANAGER, as configured in SecurityConfig
     * @param user The user to be added into the database.
     * @return The user after being added into the database.
     * @throws UserNotValidException If username is already taken, NRIC or phone number is invalid.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/customers")
    public User createUser(@Valid @RequestBody User user) {
        Optional<User> result = userRepo.findByUsername(user.getUsername());
        if (result.isPresent())
            throw new UserNotValidException("Username already taken");

        if (!Validator.validateNRIC(user.getNric())) 
            throw new UserNotValidException("NRIC is invalid");

        if (!Validator.validatePhoneno(user.getPhone())) 
            throw new UserNotValidException("Phone number is invalid");

        user.setPassword(encoder.encode(user.getPassword()));

        User savedUser = userRepo.save(user);

        if (user.getStringAuthorities().contains("ROLE_USER")) {
            portfolioRepo.save(new Portfolio(null, user.getId(), user, null, 0, 0));
        } 

        return savedUser;
    }

    /**
     * Updates customer information. The method only allows for phone no, address and password fields
     * and status to be updated. Changes to any other fields are ignored.
     * 
     * This method is authorised for the following roles:
     * ROLE_USER - only allowed to update their own information, and cannot
     * change their status
     * ROLE_MANAGER - allowed to update information for all customers, including their status 
     * 
     * @param id The ID of the customer to be updated.
     * @param newUserInfo The new details to be updated.
     * @return The new customer with the updated details.
     * @throws RoleNotAuthorisedException If user is updating another customer's details or not a ROLE_MANAGER.
     * @throws UserNotValidException If new phone number is invalid.
    */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/customers/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User newUserInfo) {
        Optional<User> result = userRepo.findById(id); 
        if (!result.isPresent())
            throw new UserNotFoundException(id);

        User user = result.get();
        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();

        if (!uDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) 
            && !user.getUsername().equals(uDetails.getUsername()))
            throw new RoleNotAuthorisedException("You cannot update another customer's details");
        
        if (newUserInfo.getPhone() != null)
            if (!Validator.validatePhoneno(user.getPhone())) 
                throw new UserNotValidException("Phone number is invalid");
            user.setPhone(newUserInfo.getPhone());
        
        if (newUserInfo.getAddress() != null)
            user.setAddress(newUserInfo.getAddress());
        
        if (newUserInfo.getPassword() != null)
            user.setPassword(encoder.encode(newUserInfo.getPassword()));
        
        if (uDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) 
            && newUserInfo.getStatus() != null)
            user.setActive(newUserInfo.getStatus());

        return userRepo.save(user);
    }

}