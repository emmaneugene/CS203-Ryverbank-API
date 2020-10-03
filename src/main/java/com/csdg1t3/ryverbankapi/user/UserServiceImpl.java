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
    public User updateUser(Long id, User user) {
        Optional<User> result = users.findById(id);
        if (result.isPresent()) {
            return users.save(user);
        }
        return null;
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