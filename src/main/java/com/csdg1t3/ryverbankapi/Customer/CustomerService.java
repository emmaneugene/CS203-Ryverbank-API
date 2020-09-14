package com.csdg1t3.ryverbankapi.Customer;

import java.util.List;


public interface CustomerService {
    List<Customer> listCustomers();
    Customer getCustomer(Long id);

    /**
     * Return the newly added customer
     */
    Customer addCustomer(Customer customer);

    /**
     * Return the updated customer
     * @param id
     * @param customer
     * @return
     */
    Customer updateCustomer(Long id, Customer customer);

    /**
     * Return status of the delete
     * If it's 1: the customer has been removed
     * If it's 0: the customer does not exist
     * @param id
     * @return 
     */
    int deleteCustomer(Long id);
}