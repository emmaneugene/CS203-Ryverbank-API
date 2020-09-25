package com.csdg1t3.ryverbankapi.transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 404 error
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransferNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    TransferNotFoundException(Long id) {
        super("Unable to find transfer " + id);
    }
}