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
     * Adds authentication information for the RestTemplate
     * @param restTemplateBuilder A builder for RestTemplate
     */
    public RestTemplateClient(RestTemplateBuilder restTemplateBuilder) {
        this.template = restTemplateBuilder
                .basicAuthentication("admin", "goodpassword")
                .build();
    }
    /**
     * Gets a user with given id
     * 
     * @param URI The URI used by the code.
     * @param id The ID of the user to be retrieved.
     * @return The user with the given ID.
     */
    public User getUser(final String URI, final Long id) {
        final User user = template.getForObject(URI + "/" + id, User.class);
        return user;
    }

    /**
     * Adds a new user
     * 
     * @param URI The URI used by the code.
     * @param newUser The new user to be added into the database.
     * @return The new user after being added into the database.
     */
    public User addUser(final String URI, final User newUser) {
        System.out.println(template.postForObject(URI, newUser, User.class));

        // final User returned = template.postForObject(URI, newUser, User.class);
        return newUser;
    }

    /**
     * Gets a user, but returns a HTTP response entity.
     * 
     * @param URI The URI used by the code.
     * @param id The ID of the user to be retrieved.
     * @return The HTTP response when the user is retrieved.
     */
    public ResponseEntity<User> getUserEntity(final String URI, final Long id){
        System.out.println("String URI " + URI + " id " + id );
        return template.getForEntity(URI + "/{id}", User.class, Long.toString(id));
    }


}