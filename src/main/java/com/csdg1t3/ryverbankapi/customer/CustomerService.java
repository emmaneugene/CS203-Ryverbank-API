package com.csdg1t3.ryverbankapi.customer;

import java.util.List;

/**
 * Convenience interface for a service class allowing from CustomerController to read and modify 
 * data from CustomerRepository
 */
public interface CustomerService {
    List<Customer> listCustomers();
    Customer getCustomer(Long id);
    Customer addCustomer(Customer customer);
    Customer updateCustomer(Long id, Customer customer);
    void deleteCustomer(Long id);
}