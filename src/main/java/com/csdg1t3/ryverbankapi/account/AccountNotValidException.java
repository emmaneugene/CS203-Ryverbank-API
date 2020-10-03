package com.csdg1t3.ryverbankapi.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an account is invalid. This also returns a HTTP response 
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountNotValidException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AccountNotValidException(String message) {
        super(message);
    }
}
