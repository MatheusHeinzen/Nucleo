package com.nucleo.exception;

import org.springframework.http.HttpStatus;

public class EntityNotDeletedException extends BaseException{

    public EntityNotDeletedException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs, HttpStatus.BAD_REQUEST.value());
    }
}
