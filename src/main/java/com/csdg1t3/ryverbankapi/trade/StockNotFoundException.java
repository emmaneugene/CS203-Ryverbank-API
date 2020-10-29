package com.csdg1t3.ryverbankapi.trade;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a stock cannot be found. This also returns a HTTP response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class StockNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    StockNotFoundException(String id) {
        super("Could not find stock " + id);
    }
    
}