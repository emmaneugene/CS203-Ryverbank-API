package com.csdg1t3.ryverbankapi.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class UserNotValidException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UserNotValidException(String message) {
        super(message);
    }
    
}