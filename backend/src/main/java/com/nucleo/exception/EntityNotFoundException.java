package com.nucleo.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BaseException {
    public EntityNotFoundException(String messageKey, Object... args) {
        super(messageKey, args, HttpStatus.NOT_FOUND.value());
    }
}
