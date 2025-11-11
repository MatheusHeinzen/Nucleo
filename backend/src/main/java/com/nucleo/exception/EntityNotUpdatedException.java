package com.nucleo.exception;

import org.springframework.http.HttpStatus;

public class EntityNotUpdatedException extends BaseException {
    public EntityNotUpdatedException(String message, Object... args) {
        super(message, args, HttpStatus.BAD_REQUEST.value());
    }
}
