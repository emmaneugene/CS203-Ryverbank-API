package com.csdg1t3.ryverbankapi.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/**
 * Concrete class that implements UserService
 */
@Service
public class UserServiceImpl implements UserService {
    private UserRepository users;
    
    public UserServiceImpl(UserRepository users) {
        this.users = users;
    }

    @Override 
    public List<User> listUsers(String authortities){
        return users.findAllByAuthorities(authortities);
    }
    
    // @Override
    // public List<Customer> listCustomers() {
    //     return customers.findAll();
    // } 

    @Override 
    public User getUser(Long id){
        Optional<User> result = users.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    // @Override
    // public Customer getCustomer(Long id) {
    //     Optional<Customer> result = customers.findById(id);
    //     if (result.isPresent()) {
    //         return result.get();
    //     }
    //     return null;
    // }

    @Override
    public User addUser(User newUser) {
        return users.save(newUser);
    }

    // @Override
    // public Customer addCustomer(Customer newCustomer) {
    //     return customers.save(newCustomer);
    // }

    @Override
    public User updateUser(Long id, User newUser, String authority) {
        Optional<User> result = users.findById(id);
        User userAtId = getUser(id);
        if (userAtId == null || !result.isPresent() || userAtId.getId() != newUser.getId() || !userAtId.getStringAuthorities().contains("ROLE_USER")){
            throw new UserNotFoundException(id);
        }

        if(!newUser.getUsername().equals(userAtId.getUsername())){
            throw new UserNotValidException("Update of username is not supported.");
        }

        if(!newUser.getNric().equals(userAtId.getNric())){
            throw new UserNotValidException("Update of nric is not supported.");
        }

        if(!newUser.getName().equals(userAtId.getName())){
            throw new UserNotValidException("Update of name is not supported.");
        }

        if(!newUser.getAuthorities().equals(userAtId.getAuthorities())){
            throw new UserNotValidException("Update of authorities is not supported.");
        }
        
        if(newUser.getStatus() != userAtId.getStatus() && !authority.equals("ROLE_MANAGER")){
            throw new UserNotValidException("Update of status by customers is not supported.");
        }
        return users.save(newUser);
    }

    // @Override
    // public Customer updateCustomer(Long id, Customer customer) {
    //     Optional<Customer> result = customers.findById(id);
    //     if (result.isPresent()) {
    //         return customers.save(customer);
    //     }
    //     return null;
    // }

    @Override
    public void deleteUser(Long id) {
        users.deleteById(id);
    }

    // @Override
    // public void deleteCustomer(Long id) {
    //     customers.deleteById(id);
    // }
}