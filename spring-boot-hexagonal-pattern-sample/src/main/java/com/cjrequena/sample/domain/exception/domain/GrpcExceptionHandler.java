package com.cjrequena.sample.domain.exception.domain;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class GrpcExceptionHandler {

    public StatusRuntimeException buildErrorResponse(Throwable err) {
        ErrorContext context = mapExceptionToErrorContext(err);

        // Log based on severity
        switch (context.severity()) {
            case ERROR -> log.error("Error processing request: {}", err.getMessage(), err);
            case WARN -> log.warn("Warning: {}", err.getMessage(), err);
            case INFO -> log.info("Client error: {}", err.getMessage());
        }

        var statusBuilder = com.google.rpc.Status.newBuilder()
            .setCode(context.grpcCode().getNumber());

        // Add error message if available
        if (err.getMessage() != null && !err.getMessage().isEmpty()) {
            statusBuilder.setMessage(context.userMessageOptional().orElse(err.getMessage()));
        }

        // Add error details for better client handling
        if (context.httpStatus() != null || context.errorDomain() != null) {
            var errorInfoBuilder = ErrorInfo.newBuilder();

            if (context.httpStatus() != null) {
                errorInfoBuilder.putMetadata("http_code", context.httpStatus().toString());
            }

            if (context.errorDomain() != null) {
                errorInfoBuilder.setDomain(context.errorDomain());
                errorInfoBuilder.setReason(context.errorReason());
            }

            statusBuilder.addDetails(Any.pack(errorInfoBuilder.build()));
        }

        return StatusProto.toStatusRuntimeException(statusBuilder.build());
    }

    private ErrorContext mapExceptionToErrorContext(Throwable err) {
        return switch (err) {
            case CustomerNotFoundException ignored -> ErrorContext.of(
                Code.NOT_FOUND,
                HttpStatus.NOT_FOUND,
                LogSeverity.INFO,
                "account_service",
                "ACCOUNT_NOT_FOUND",
                "The requested account was not found"
            );

            case IllegalArgumentException ignored -> ErrorContext.of(
                Code.INVALID_ARGUMENT,
                HttpStatus.BAD_REQUEST,
                LogSeverity.INFO,
                "validation",
                "INVALID_ARGUMENT",
                "Invalid argument provided"
            );

            case SecurityException ignored -> ErrorContext.of(
                Code.PERMISSION_DENIED,
                HttpStatus.FORBIDDEN,
                LogSeverity.WARN,
                "security",
                "ACCESS_DENIED",
                "Access denied"
            );

            case IllegalStateException ignored -> ErrorContext.of(
                Code.FAILED_PRECONDITION,
                HttpStatus.PRECONDITION_FAILED,
                LogSeverity.WARN,
                "service_state",
                "INVALID_STATE",
                "Service is in an invalid state for this operation"
            );

            default -> {
                if (isClientError(err)) {
                    yield ErrorContext.of(
                        Code.INVALID_ARGUMENT,
                        HttpStatus.BAD_REQUEST,
                        LogSeverity.INFO,
                        "client_error",
                        "CLIENT_ERROR",
                        "Invalid client request"
                    );
                } else {
                    yield ErrorContext.of(
                        Code.INTERNAL,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        LogSeverity.ERROR,
                        "internal_error",
                        "INTERNAL_ERROR",
                        "An internal server error occurred"
                    );
                }
            }
        };
    }

    private boolean isClientError(Throwable err) {
        String message = err.getMessage();
        return message != null && (
            message.contains("invalid") ||
            message.contains("malformed") ||
            message.contains("bad request") ||
            err instanceof IllegalArgumentException
        );
    }

    private record ErrorContext(
        Code grpcCode,
        HttpStatus httpStatus,
        LogSeverity severity,
        String errorDomain,
        String errorReason,
        String userMessage
    ) {
        public static ErrorContext of(Code grpcCode, HttpStatus httpStatus, LogSeverity severity,
                                      String domain, String reason, String message) {
            return new ErrorContext(grpcCode, httpStatus, severity, domain, reason, message);
        }

        public Optional<String> userMessageOptional() {
            return Optional.ofNullable(userMessage);
        }
    }

    private enum LogSeverity {
        INFO, WARN, ERROR
    }
}
