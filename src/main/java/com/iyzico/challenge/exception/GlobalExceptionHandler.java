package com.iyzico.challenge.exception;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.of("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.of("BUSINESS_ERROR", ex.getMessage()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of("INTERNAL_ERROR", "Unexpected error occurred"));
    }


    @ExceptionHandler(SeatNotAvailableException.class)
    public ResponseEntity<ApiError> handleSeatNotAvailable(SeatNotAvailableException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.of("SEAT_NOT_AVAILABLE", e.getMessage()));
    }


    @Data
    @AllArgsConstructor
    public static class ApiError {
        private String code;
        private String message;
        private Instant timestamp;

        public static ApiError of(String code, String message) {
            return new ApiError(code, message, Instant.now());
        }
    }
}
