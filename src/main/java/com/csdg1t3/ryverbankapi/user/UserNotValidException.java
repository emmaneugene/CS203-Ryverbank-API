package com.csdg1t3.ryverbankapi.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user detail is not valid. This also returns a HTTP response.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotValidException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public UserNotValidException(String message) {
        super(message);
    }
    
}