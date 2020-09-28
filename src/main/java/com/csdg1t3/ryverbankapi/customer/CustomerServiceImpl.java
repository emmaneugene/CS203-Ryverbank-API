package com.csdg1t3.ryverbankapi.customer;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/**
 * Concrete class that implements CustomerService
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    private CustomerRepository customers;
    
    public CustomerServiceImpl(CustomerRepository customers) {
        this.customers = customers;
    }
    
    @Override
    public List<Customer> listCustomers() {
        return customers.findAll();
    }

    @Override
    public Customer getCustomer(Long id) {
        Optional<Customer> result = customers.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    @Override
    public Customer addCustomer(Customer newCustomer) {
        return customers.save(newCustomer);
    }

    @Override
    public Customer updateCustomer(Long id, Customer customer) {
        Optional<Customer> result = customers.findById(id);
        if (result.isPresent()) {
            return customers.save(customer);
        }
        return null;
    }

    @Override
    public void deleteCustomer(Long id) {
        customers.deleteById(id);
    }
}