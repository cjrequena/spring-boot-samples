package com.cjrequena.sample.domain.exception;

public class CustomertNotFoundException extends DomainRuntimeException {

    public CustomertNotFoundException(String message) {
        super(message);
    }

    public CustomertNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
