package com.securedoc.domain.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class BadRequestExcetpion extends RuntimeException{

    private final String errorCode;

    public BadRequestExcetpion(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
