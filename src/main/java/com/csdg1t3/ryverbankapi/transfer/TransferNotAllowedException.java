package com.csdg1t3.ryverbankapi.transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 404 error
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransferNotAllowedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    TransferNotAllowedException(Long id) {
        super("Unable to find transfer due to lack of funds " + id);
    }
}