package com.cjrequena.sample.domain.exception.domain;


public class MapperException extends RuntimeDomainException {

  public MapperException(Throwable ex) {
    super(ex);
  }

  public MapperException(String message) {
    super(message);
  }

  public MapperException(String message, Throwable ex) {
    super(message, ex);
  }
}
