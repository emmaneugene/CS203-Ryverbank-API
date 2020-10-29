package com.csdg1t3.ryverbankapi.trade;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a trade to be created is not valid. This also returns a HTTP response.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TradeNotValidException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TradeNotValidException(String message) {
        super(message);
    }
}
