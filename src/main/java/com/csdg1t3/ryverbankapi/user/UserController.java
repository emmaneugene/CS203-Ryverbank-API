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


/**
 * Controller that manages HTTP  requests
 */
@RestController
public class UserController {
    private UserRepository userRepo;
    private BCryptPasswordEncoder encoder;


    public UserController(UserRepository userRepo, BCryptPasswordEncoder encoder) {
        this.userRepo= userRepo;
        this.encoder = encoder;
    }

    /**
     * 
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/customers")
    public List<User> getCustomers() {
        return userRepo.findAll();
    }

    /**
     * Search for customer with the given id. If there is no customer with the given "id", throw 
     * @param id
     * @return customer with the given id
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/customers/{id}")
    public User getCustomer(@PathVariable Long id) {
        Optional<User> result = userRepo.findById(id); // userService.getUser(id);
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
     * 
     * @param user
     * @return 
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/customers")
    public User createUser(@Valid @RequestBody User user) {
        Optional<User> result = userRepo.findByUsername(user.getUsername());
        if (result.isPresent())
            throw new UserNotValidException("Username already taken");

        if (!Validator.validateNRIC(user.getNric())) 
            throw new UserNotValidException("NRIC is invalid");

        if (!Validator.validatePhoneno(user.getPhoneNo())) 
            throw new UserNotValidException("Phone number is invalid");

        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    /**
     * Updates customer info using HTTP PUT request. Customers with ROLE_USER are authorised 
     * to update their phone no, address and password. Customers with ROLE_MANAGER are further
     * authorised to update customer status 
     * @param id
     * @param newUserInfo
     * @return
    */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/customers/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User newUserInfo) {
        Optional<User> result = userRepo.findById(id); // userService.getUser(id);
        if (!result.isPresent())
            throw new UserNotFoundException(id);

        User user = result.get();
        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();

        if (!uDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) 
            && !user.getUsername().equals(uDetails.getUsername()))
            throw new RoleNotAuthorisedException("You cannot update another customer's details");
        
        if (newUserInfo.getPhoneNo() != null)
            if (!Validator.validatePhoneno(user.getPhoneNo())) 
                throw new UserNotValidException("Phone number is invalid");
            user.setPhoneNo(newUserInfo.getPhoneNo());
        
        if (newUserInfo.getAddress() != null)
            user.setAddress(newUserInfo.getAddress());
        
        if (newUserInfo.getPassword() != null)
            user.setPassword(encoder.encode(newUserInfo.getPassword()));
        
        if (uDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) 
            && newUserInfo.getStatus() != null)
            user.setStatus(newUserInfo.getStatus());

        return userRepo.save(user);
    }
}