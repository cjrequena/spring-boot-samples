package com.cjrequena.sample.controller.excepption;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.cjrequena.sample.shared.common.util.Constant.ISO_LOCAL_DATE_TIME;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler({ControllerException.class})
  @ResponseBody
  public ResponseEntity<Object> handleControllerException(ControllerException ex, HttpServletRequest request) {
    log.info("Exception: {}", ex.getMessage());
    ErrorDTO errorDTO = ErrorDTO
      .builder()
      .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(ISO_LOCAL_DATE_TIME)))
      .status(ex.getHttpStatus().value())
      .errorCode(ex.getClass().getSimpleName())
      .message(ex.getMessage())
      .path(request.getRequestURI())
      .build();
    return ResponseEntity.status(ex.getHttpStatus()).body(errorDTO);
  }

  @ExceptionHandler({ControllerRuntimeException.class})
  @ResponseBody
  public ResponseEntity<Object> handleControllerRuntimeException(ControllerRuntimeException ex, HttpServletRequest request) {
    log.info("Exception: {}", ex.getMessage());
    ErrorDTO errorDTO = ErrorDTO
      .builder()
      .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(ISO_LOCAL_DATE_TIME)))
      .status(ex.getHttpStatus().value())
      .errorCode(ex.getClass().getSimpleName())
      .message(ex.getMessage())
      .path(request.getRequestURI())
      .build();
    return ResponseEntity.status(ex.getHttpStatus()).body(errorDTO);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseBody
  public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
    log.info("Exception: {}", ex.getHttpInputMessage());
    ErrorDTO error = ErrorDTO.builder()
      .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(ISO_LOCAL_DATE_TIME)))
      .status(HttpStatus.BAD_REQUEST.value())
      .errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase())
      .message(ex.getLocalizedMessage())
      .path(request.getRequestURI())
      .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseBody
  public ResponseEntity<ErrorDTO> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

    log.error("Validation error: {}", ex.getMessage());

    List<ValidationError> validationErrors = ex.getBindingResult()
      .getFieldErrors()
      .stream()
      .map(this::mapToValidationError)
      .collect(Collectors.toList());

    ErrorDTO error = ErrorDTO.builder()
      .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(ISO_LOCAL_DATE_TIME)))
      .status(HttpStatus.BAD_REQUEST.value())
      .errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase())
      .message("Validation failed for one or more fields")
      .path(request.getRequestURI())
      .validationErrors(validationErrors)
      .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorDTO> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {

    log.error("Illegal argument: {}", ex.getMessage());

    ErrorDTO error = ErrorDTO.builder()
      .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(ISO_LOCAL_DATE_TIME)))
      .status(HttpStatus.BAD_REQUEST.value())
      .errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase())
      .message(ex.getMessage())
      .path(request.getRequestURI())
      .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorDTO> handleIllegalStateException(
    IllegalStateException ex,
    HttpServletRequest request) {

    log.error("Illegal state: {}", ex.getMessage());

    ErrorDTO error = ErrorDTO.builder()
      .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(ISO_LOCAL_DATE_TIME)))
      .status(HttpStatus.CONFLICT.value())
      .errorCode(HttpStatus.CONFLICT.getReasonPhrase())
      .message(ex.getMessage())
      .path(request.getRequestURI())
      .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDTO> handleGenericException(
    Exception ex,
    HttpServletRequest request) {

    log.error("Unexpected error occurred", ex);

    ErrorDTO error = ErrorDTO.builder()
      .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(ISO_LOCAL_DATE_TIME)))
      .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
      .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
      .message("An unexpected error occurred")
      .path(request.getRequestURI())
      .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  private ValidationError mapToValidationError(FieldError fieldError) {
    return ValidationError.builder()
      .field(fieldError.getField())
      .message(fieldError.getDefaultMessage())
      .rejectedValue(fieldError.getRejectedValue())
      .build();
  }
}
