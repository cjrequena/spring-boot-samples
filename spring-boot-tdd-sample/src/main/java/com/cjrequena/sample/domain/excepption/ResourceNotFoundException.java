package com.cjrequena.sample.domain.excepption;

public class ResourceNotFoundException extends DomainRuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
