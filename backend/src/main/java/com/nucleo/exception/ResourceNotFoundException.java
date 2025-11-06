package com.nucleo.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String messageKey, Object... args) {
        super(messageKey, args, HttpStatus.UNAUTHORIZED.value());
    }
}
