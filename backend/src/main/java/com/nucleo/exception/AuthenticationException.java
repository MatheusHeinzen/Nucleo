package com.nucleo.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseException {
    public AuthenticationException(String messageKey) {
        super(messageKey, null, HttpStatus.UNAUTHORIZED.value());
    }
}
