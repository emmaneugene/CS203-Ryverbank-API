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
     * Get a user with given id
     * 
     * @param URI
     * @param id
     * @return
     */
    public User getUser(final String URI, final Long id) {
        final User user = template.getForObject(URI + "/" + id, User.class);
        return user;
    }

    /**
     * Add a new user
     * 
     * @param URI
     * @param newUser
     * @return
     */
    public User addUser(final String URI, final User newUser) {
       System.out.println("IM HERE ");
    
        System.out.println( template.postForObject(URI, newUser, User.class));

        // final User returned = template.postForObject(URI, newUser, User.class);
        return newUser;
    }

    /**
     * Get a user, but return a HTTP response entity.
     * @param URI
     * @param id
     * @return
     */
    public ResponseEntity<User> getUserEntity(final String URI, final Long id){
        System.out.println("String URI " + URI + " id " + id );
        return template.getForEntity(URI + "/{id}", User.class, Long.toString(id));
    }
    
}