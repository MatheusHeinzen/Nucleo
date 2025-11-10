package com.nucleo.exception;

import org.springframework.http.HttpStatus;

public class EntityNotCreatedException extends BaseException {
    public EntityNotCreatedException(String messagekey, Object... args) {
        super(messagekey,args, HttpStatus.BAD_REQUEST.value());
    }
}
