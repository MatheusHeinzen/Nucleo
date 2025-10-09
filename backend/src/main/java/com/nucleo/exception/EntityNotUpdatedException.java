package com.nucleo.exception;

import org.aspectj.weaver.patterns.ArgsPointcut;
import org.springframework.http.HttpStatus;

public class EntityNotUpdatedException extends BaseException {
    public EntityNotUpdatedException(String message, Object... args) {
        super(message,args, HttpStatus.NOT_MODIFIED.value());
    }
}
