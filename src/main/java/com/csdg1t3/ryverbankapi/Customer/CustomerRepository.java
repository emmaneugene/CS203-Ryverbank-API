package com.csdg1t3.ryverbankapi.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Data access object - work with the persistent database
 */
public interface CustomerRepository {
    Long save(Customer customer);
    int update(Customer customer);
    int deleteById(Long id);
    List<Customer> findAll();

    // Using Optional - the return value of this method may contain a null value
    Optional<Customer> findById(Long id);
 
}