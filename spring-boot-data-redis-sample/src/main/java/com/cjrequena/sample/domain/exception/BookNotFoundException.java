package com.cjrequena.sample.domain.exception;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class BookNotFoundException extends DomainRuntimeException {
  public BookNotFoundException(String message) {
    super(message);
  }
}
