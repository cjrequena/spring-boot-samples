package com.cjrequena.sample.domain.exception.domain;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class InvalidEmailException extends DomainRuntimeException {
  public InvalidEmailException(String message) {
    super(message);
  }
}
