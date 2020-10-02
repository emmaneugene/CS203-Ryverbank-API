package com.csdg1t3.ryverbankapi.customer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class CustomerNotFoundException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CustomerNotFoundException(Long id) {
        super("Could not find customer " + id);
    }
    
}