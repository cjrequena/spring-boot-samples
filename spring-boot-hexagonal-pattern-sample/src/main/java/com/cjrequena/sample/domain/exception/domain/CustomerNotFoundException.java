package com.cjrequena.sample.domain.exception.domain;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class CustomerNotFoundException extends RuntimeDomainException {
  public CustomerNotFoundException(String message) {
    super(message);
  }
}
