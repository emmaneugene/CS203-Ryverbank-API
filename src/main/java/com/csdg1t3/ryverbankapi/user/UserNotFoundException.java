package com.csdg1t3.ryverbankapi.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class UserNotFoundException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(Long id) {
        super("Could not find user " + id);
    }

    public UserNotFoundException(String message){
        super(message);
    }
    
}