package com.nucleo.exception;

import org.springframework.http.HttpStatus;

public class EntityNotValid extends BaseException {

    public EntityNotValid(String messageKey) {
      super(messageKey, null, HttpStatus.UNAUTHORIZED.value());
    }
  }
