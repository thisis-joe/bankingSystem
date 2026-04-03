package com.bankingsystem.channel.api.presentation.handler;

import com.bankingsystem.channel.api.presentation.response.ApiErrorResponse;
import com.bankingsystem.common.exception.BankingException;
import com.bankingsystem.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BankingException.class)
    public ResponseEntity<ApiErrorResponse> handleBankingException(
        BankingException exception,
        HttpServletRequest request
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
            .body(new ApiErrorResponse(
                errorCode.getCode(),
                exception.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
            ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {
        String message = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .map(FieldError::getDefaultMessage)
            .orElse(ErrorCode.VALIDATION_ERROR.getMessage());

        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getHttpStatus())
            .body(new ApiErrorResponse(
                ErrorCode.VALIDATION_ERROR.getCode(),
                message,
                request.getRequestURI(),
                OffsetDateTime.now()
            ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
        Exception exception,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
            .body(new ApiErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
            ));
    }
}
