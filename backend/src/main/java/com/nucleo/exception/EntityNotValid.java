package com.nucleo.exception;

import org.springframework.http.HttpStatus;

public class EntityNotValid extends BaseException {

    public EntityNotValid(String messageKey) {
        super(messageKey, null, HttpStatus.BAD_REQUEST.value());
    }
}
