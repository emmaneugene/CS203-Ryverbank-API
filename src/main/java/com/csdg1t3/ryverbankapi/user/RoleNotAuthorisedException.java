package com.csdg1t3.ryverbankapi.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // HTTP 403: Role is not suitable to perform action
public class RoleNotAuthorisedException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public RoleNotAuthorisedException(String message) {
        super(message);
    }
    
}