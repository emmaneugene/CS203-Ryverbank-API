package com.csdg1t3.ryverbankapi.Customer;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
    private CustomerService customerService;

    public CustomerController(CustomerService cs){
        this.customerService = cs;
    }

    /**
     * List all customers in the system
     * @return list of all books
     */
    @GetMapping("/customers")
    public List<Book> getCustomers(){
        return bookService.listCustomers();
    }

    /**
     * Search for customer with the given id
     * If there is no customer with the given "id", throw a CustomerNotFoundException
     * @param id
     * @return customer with the given id
     */
    @GetMapping("/customers/{id}")
    public Customer getCustomer(@PathVariable Long id){
        Customer customer = customerService.getCustomer(id);

        // Need to handle "customer not found" error using proper HTTP status code
        // In this case it should be HTTP 404
        if(customer == null) throw new CustomerNotFoundException(id);
        return customerService.getCustomer(id);

    }
    /**
     * Add a new customer with POST request to "/customers"
     * Note the use of @RequestBody
     * @param customer
     * @return list of all books
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/customers")
    public Customer addCustomer(@RequestBody Customer customer){
        return customerService.addCustomer(customer);
    }

    /**
     * If there is no customer with the given "id", throw a CustomerNotFoundException
     * @param id
     * @param newCustomerInfo
     * @return the updated, or newly added book
     */
    @PutMapping("/customers/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer newCustomerInfo){
        Customer customer = customerService.updateCustomer(id, newCustomerInfo);
        if(customer == null) throw new CustomerNotFoundException(id);
        
        return customer;
    }

    /**
     * Remove a customer with the DELETE request to "/customers/{id}"
     * If there is no customer with the given "id", throw a CustomerNotFoundException
     * @param id
     */
    @DeleteMapping("/books/{id}")
    public void deleteCustomer(@PathVariable Long id){
        if(bookService.deleteCustomer(id) == 0) throw new CustomerNotFoundException(id);
    }
}