package com.nucleo.exception;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public abstract class BaseException extends RuntimeException {

    private final String messageKey;
    private final Object[] messageArgs;
    @Getter
    private final int status;

    public BaseException(String messageKey, Object[] messageArgs, int status) {
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
        this.status = status;
    }


    public String resolveMessage(MessageSource messageSource) {
        return messageSource.getMessage(messageKey, messageArgs, LocaleContextHolder.getLocale());
    }
}
