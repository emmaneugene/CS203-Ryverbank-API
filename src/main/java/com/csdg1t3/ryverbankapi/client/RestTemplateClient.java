package com.csdg1t3.ryverbankapi.client;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.csdg1t3.ryverbankapi.customer.*;

@Component
public class RestTemplateClient {
    
    private final RestTemplate template;

    /**
     * Add authentication information for the RestTemplate
     * 
     */
    public RestTemplateClient(RestTemplateBuilder restTemplateBuilder) {
        this.template = restTemplateBuilder
                .basicAuthentication("admin", "goodpassword")
                .build();
    }
    /**
     * Get a customer with given id
     * 
     * @param URI
     * @param id
     * @return
     */
    public Customer getCustomer(final String URI, final Long id) {
        final Customer customer = template.getForObject(URI + "/" + id, Customer.class);
        return customer;
    }

    /**
     * Add a new customer
     * 
     * @param URI
     * @param newCustomer
     * @return
     */
    public Customer addCustomer(final String URI, final Customer newCustomer) {
        final Customer returned = template.postForObject(URI, newCustomer, Customer.class);
        
        return returned;
    }

    /**
     * Get a customer, but return a HTTP response entity.
     * @param URI
     * @param id
     * @return
     */
    public ResponseEntity<Customer> getCustomerEntity(final String URI, final Long id){
        return template.getForEntity(URI + "/{id}", Customer.class, Long.toString(id));
    }
    
}