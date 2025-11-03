package com.cjrequena.sample.controller.excepption;

import com.fasterxml.jackson.databind.JsonMappingException;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.cjrequena.sample.shared.common.util.Constant.ISO_LOCAL_DATE_TIME;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler({ControllerException.class})
  @ResponseBody
  public ResponseEntity<Object> handleControllerException(ControllerException ex, HttpServletRequest request) {
    log.info("Exception: {}", ex.getMessage());
    return buildErrorResponse(ex.getHttpStatus(), ex.getMessage());
  }

  @ExceptionHandler({ControllerRuntimeException.class})
  @ResponseBody
  public ResponseEntity<Object> handleControllerRuntimeException(ControllerRuntimeException ex, HttpServletRequest request) {
    log.info("Exception: {}", ex.getMessage());
    return buildErrorResponse(ex.getHttpStatus(), ex.getMessage());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseBody
  public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
    log.warn("Failed to read HTTP message at {}: {}", request.getRequestURI(), ex.getMostSpecificCause().toString());

    Throwable cause = ex.getCause();
    String message;

    if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException invalidFormatEx) {
      Class<?> targetType = invalidFormatEx.getTargetType();

      // Handle invalid Date/Time format
      if (targetType != null && java.time.temporal.Temporal.class.isAssignableFrom(targetType)) {
        String field = invalidFormatEx.getPath().stream()
          .map(JsonMappingException.Reference::getFieldName)
          .filter(Objects::nonNull)
          .reduce((a, b) -> a + "." + b)
          .orElse("unknown");

        String invalidValue = String.valueOf(invalidFormatEx.getValue());
        message = String.format("Invalid date/time format for field '%s': '%s . Expected format: '%s' ", field, invalidValue, ISO_LOCAL_DATE_TIME);

        return buildErrorResponse(HttpStatus.BAD_REQUEST ,message);
      }

      // Handle invalid Enum values
      if (targetType != null && targetType.isEnum()) {
        String field = invalidFormatEx.getPath().stream()
          .map(JsonMappingException.Reference::getFieldName)
          .filter(Objects::nonNull)
          .reduce((a, b) -> a + "." + b)
          .orElse("unknown");

        String invalidValue = String.valueOf(invalidFormatEx.getValue());
        String acceptedValues = Arrays.stream(targetType.getEnumConstants())
          .map(Object::toString)
          .collect(Collectors.joining(", "));

        message = String.format("Invalid value for enum field '%s': '%s'. Accepted values are: [%s].", field, invalidValue, acceptedValues);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
      }
    }

    // Generic fallback for malformed JSON or other unreadable content
    message = "Malformed JSON request or invalid request body.";
    return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
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
      .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
    ErrorDTO error = ErrorDTO.builder()
      .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
      .status(status.value())
      .errorCode(status.getReasonPhrase())
      .message(message)
      .build();

    return ResponseEntity.status(status).body(error);
  }

  private ValidationError mapToValidationError(FieldError fieldError) {
    return ValidationError.builder()
      .field(fieldError.getField())
      .message(fieldError.getDefaultMessage())
      .rejectedValue(fieldError.getRejectedValue())
      .build();
  }
}
