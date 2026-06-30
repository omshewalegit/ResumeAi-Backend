package com.resume_ai_backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(Map.of(
                        "error", "Something went wrong",
                        "message", ex.getMessage() != null ? ex.getMessage() : "Unknown error"
                ));
    }
}