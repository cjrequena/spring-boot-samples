package com.cjrequena.sample.domain.exception.service;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class InvalidEmailServiceException extends RuntimeServiceException {
  public InvalidEmailServiceException(String message) {
    super(message);
  }
}
