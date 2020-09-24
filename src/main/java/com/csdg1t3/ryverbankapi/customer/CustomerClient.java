package com.csdg1t3.ryverbankapi.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomerClient{
    private RestTemplate template;

    @Autowired
    void setTemplate(final RestTemplate template) {
        this.template = template;
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Other configurations here if needed
        return builder.build();
    }

    /**
     * Get a customer with given URI and id
     * 
     * @param URI
     * @param id
     * @return
     */
    public Customer getCustomer(final String URI, final Long id) {
        final Customer customer = template.getForObject(URI + "/" + id, Customer.class);
        return customer;
    }
    // should we get customer by name and nric too 

    /**
     * Add a new customer
     * 
     * @param URI whats URI lmao
     * @param newBook
     * @return
     */
    public Customer addCustomer(final String URI, final Customer newCustomer) {
        final Customer toReturn = template.postForObject(URI, newCustomer, Customer.class);
        return toReturn;
    }

}