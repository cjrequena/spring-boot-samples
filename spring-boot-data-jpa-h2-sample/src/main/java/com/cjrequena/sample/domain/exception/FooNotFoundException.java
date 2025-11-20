package com.cjrequena.sample.domain.exception;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class FooNotFoundException extends DomainRuntimeException {
  public FooNotFoundException(String message) {
    super(message);
  }
}
