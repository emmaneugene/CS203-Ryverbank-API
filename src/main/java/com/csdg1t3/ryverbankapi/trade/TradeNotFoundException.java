package com.csdg1t3.ryverbankapi.trade;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a trade cannot be found. This also returns a HTTP response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class TradeNotFoundException extends RuntimeException{
        private static final long serialVersionUID = 1L;

    public TradeNotFoundException(Long id) {
        super("Could not find trade " + id);
    }
    
}