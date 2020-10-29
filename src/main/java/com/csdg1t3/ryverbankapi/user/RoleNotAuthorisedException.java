package com.csdg1t3.ryverbankapi.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the role of the user is not suitable to perform the action.
 * This also returns a HTTP response.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class RoleNotAuthorisedException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public RoleNotAuthorisedException(String message) {
        super(message);
    }
    
}