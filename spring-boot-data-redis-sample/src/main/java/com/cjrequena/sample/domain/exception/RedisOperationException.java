package com.cjrequena.sample.domain.exception;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class RedisOperationException extends DomainRuntimeException {
  public RedisOperationException(String message) {
    super(message);
  }

  public RedisOperationException(String message, Throwable ex) {
    super(message, ex);
  }

}
