package com.csdg1t3.ryverbankapi.user;

import java.util.List;

/**
 * Convenience interface for a service class allowing from CustomerController to read and modify 
 * data from CustomerRepository
 */
public interface UserService {
    List<User> listUsers(String authority);
    User getUser(Long id);
    User addUser(User user);
    User updateUser(Long id, User user,String authority);
    void deleteUser(Long id);

    // User addCustomer(User customer);
    // User updateCustomer(Long id, User customer);
    // void deleteCustomer(Long id);
}