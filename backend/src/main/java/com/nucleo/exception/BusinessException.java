package com.nucleo.exception;

import org.springframework.http.HttpStatus;

/**
 * Usa essa prr quando for representar erros de lógica de negócio, como tentativas invalidas
 * ou regras de domínio quebradas.
 */
public class BusinessException extends BaseException {
    public BusinessException(String messageKey, Object[] args) {
        super(messageKey,args, HttpStatus.UNPROCESSABLE_ENTITY.value());
    }
}
