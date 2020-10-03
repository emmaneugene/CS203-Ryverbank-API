package com.csdg1t3.ryverbankapi.client;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.csdg1t3.ryverbankapi.user.*;

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
    public User getCustomer(final String URI, final Long id) {
        final User customer = template.getForObject(URI + "/" + id, User.class);
        return customer;
    }

    /**
     * Add a new customer
     * 
     * @param URI
     * @param newCustomer
     * @return
     */
    public User addCustomer(final String URI, final User newCustomer) {
        final User returned = template.postForObject(URI, newCustomer, User.class);
        return returned;
    }

    /**
     * Get a customer, but return a HTTP response entity.
     * @param URI
     * @param id
     * @return
     */
    public ResponseEntity<User> getCustomerEntity(final String URI, final Long id){
        System.out.println("String URI " + URI + " id " + id );
        return template.getForEntity(URI + "/{id}", User.class, Long.toString(id));
    }
    
}