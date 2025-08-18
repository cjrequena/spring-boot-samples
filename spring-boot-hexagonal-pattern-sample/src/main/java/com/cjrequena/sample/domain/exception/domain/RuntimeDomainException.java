package com.cjrequena.sample.domain.exception.domain;

import lombok.ToString;

@ToString
public abstract class RuntimeDomainException extends RuntimeException {
  public RuntimeDomainException(Throwable ex) {
    super(ex);
  }

  public RuntimeDomainException(String message) {
    super(message);
  }

  public RuntimeDomainException(String message, Throwable ex) {
    super(message, ex);
  }

}
