package com.nucleo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    // Injeta automaticamente o messageSource configurado pelo Spring
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, HttpServletRequest request) {

        String mensagem = ex.resolveMessage(messageSource);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getStatus())
                .error(HttpStatus.valueOf(ex.getStatus()).getReasonPhrase())
                .message(mensagem)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    // Exemplo: pegando o valor de error.not-found do messages.properties
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, jakarta.servlet.http.HttpServletRequest request) {
        // Busca a mensagem no messages.properties
        String mensagemTraduzida = messageSource.getMessage(
                "error.not-found",      // chave no messages.properties
                null,                   // parâmetros (usados se tiver {0}, {1} etc.)
                LocaleContextHolder.getLocale() // idioma atual (padrão: pt-BR)
        );

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(404)
                .error("Not Found")
                .message(mensagemTraduzida + ": " + ex.getMessage()) // usa a mensagem do bundle
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, jakarta.servlet.http.HttpServletRequest request) {
        // Busca a mensagem no messages.properties
        String mensagemTraduzida = messageSource.getMessage(
                "login.login-failed",      // chave no messages.properties
                null,                   // parâmetros (usados se tiver {0}, {1} etc.)
                LocaleContextHolder.getLocale() // idioma atual (padrão: pt-BR)
        );

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(401)
                .error(HttpStatus.valueOf(ex.getStatus()).getReasonPhrase())
                .message(mensagemTraduzida + ": " + ex.getMessage()) // usa a mensagem do bundle
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(401).body(error);
    }

    @ExceptionHandler(EntityNotCreatedException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotCreated(EntityNotCreatedException ex, jakarta.servlet.http.HttpServletRequest request) {
        return handleBaseException(ex, request);
    }

    @ExceptionHandler(EntityNotUpdatedException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotUpdated(EntityNotUpdatedException ex, jakarta.servlet.http.HttpServletRequest request) {
        return handleBaseException(ex, request);
    }

    @ExceptionHandler(EntityNotDeletedException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotDeleted(EntityNotDeletedException ex, jakarta.servlet.http.HttpServletRequest request){
        return handleBaseException(ex, request);
    }

}
