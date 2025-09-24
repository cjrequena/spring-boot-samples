package com.cjrequena.sample.domain.exception.controller;

import com.cjrequena.sample.domain.exception.domain.DomainException;
import com.cjrequena.sample.shared.common.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.cjrequena.sample.shared.common.util.Constant.DATE_TIME_FORMAT;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

  private static final String EXCEPTION_LOG = "Exception {}";

  @ExceptionHandler({Exception.class})
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> unhandledExceptions(Exception ex) {
    log.error(EXCEPTION_LOG, ex.getMessage(), ex);
    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
    errorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    errorDTO.setErrorCode(ex.getClass().getSimpleName());
    errorDTO.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
  }

  @ExceptionHandler({RuntimeException.class})
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> unhandledRuntimeExceptions(Exception ex) {
    log.error(EXCEPTION_LOG, ex.getMessage(), ex);
    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
    errorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    errorDTO.setErrorCode(ex.getClass().getSimpleName());
    errorDTO.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
  }

  @ExceptionHandler({DomainException.class})
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ResponseEntity<Object> handleServiceException(DomainException ex) {
    log.error(EXCEPTION_LOG, ex.getMessage(), ex);
    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
    errorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    errorDTO.setErrorCode(ex.getClass().getSimpleName());
    errorDTO.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
  }

  @ExceptionHandler({ControllerException.class})
  @ResponseBody
  public ResponseEntity<Object> handleControllerException(ControllerException ex) {
    //log.error(EXCEPTION_LOG, ex.getMessage(), ex);
    log.error(EXCEPTION_LOG, ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
    errorDTO.setStatus(ex.getHttpStatus().value());
    errorDTO.setErrorCode(ex.getClass().getSimpleName());
    errorDTO.setMessage(ex.getMessage());
    return ResponseEntity.status(ex.getHttpStatus()).body(errorDTO);
  }

  @ExceptionHandler({ControllerRuntimeException.class})
  @ResponseBody
  public ResponseEntity<Object> handleControllerRuntimeException(ControllerRuntimeException ex) {
    //log.error(EXCEPTION_LOG, ex.getMessage(), ex);
    log.error(EXCEPTION_LOG, ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
    errorDTO.setStatus(ex.getHttpStatus().value());
    errorDTO.setErrorCode(ex.getClass().getSimpleName());
    errorDTO.setMessage(ex.getMessage());
    return ResponseEntity.status(ex.getHttpStatus()).body(errorDTO);
  }

}
