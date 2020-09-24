package com.csdg1t3.ryverbankapi.content;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 404 error
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    ContentNotFoundException(Long id) {
        super("Unable to find content " + id);
    }
}