package com.cjrequena.sample.domain.exception.rest;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@ToString
public abstract class RuntimeRestException extends RuntimeException {
  @Getter
  private final HttpStatus httpStatus;

  /**
   * Constructor
   * @param httpStatus status code
   */
  public RuntimeRestException(HttpStatus httpStatus) {
    super(httpStatus.getReasonPhrase());
    this.httpStatus = httpStatus;
  }

  /**
   * Constructor
   * @param httpStatus status code
   * @param message Custom message
   */
  public RuntimeRestException(HttpStatus httpStatus, String message) {
    super(message);
    this.httpStatus = httpStatus;
  }

  /**
   * Constructor
   * @param httpStatus status code
   * @param message custom message
   * @param throwable Exception
   */
  public RuntimeRestException(HttpStatus httpStatus, String message, Throwable throwable) {
    super(message, throwable);
    this.httpStatus = httpStatus;
  }

}
