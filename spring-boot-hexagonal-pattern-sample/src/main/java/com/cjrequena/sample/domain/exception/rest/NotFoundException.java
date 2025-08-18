package com.cjrequena.sample.domain.exception.rest;

import org.springframework.http.HttpStatus;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class NotFoundException extends RuntimeRestException {
  public NotFoundException() {
    super(HttpStatus.NOT_FOUND);
  }

  public NotFoundException(String message) {
    super(HttpStatus.NOT_FOUND, message);
  }
}
