package com.csdg1t3.ryverbankapi.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when no credentials are found. Returns HTTP 401 Unauthorized response
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NoCredentialsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoCredentialsException(String message) {
        super(message);
    }
}
