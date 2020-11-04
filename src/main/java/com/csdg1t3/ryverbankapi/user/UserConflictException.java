package com.csdg1t3.ryverbankapi.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the role of the user is not suitable to perform the action.
 * This also returns a HTTP response.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserConflictException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserConflictException(String message) {
        super(message);
    }
}
