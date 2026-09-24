package com.TestingApp.test.advice;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandling {


    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<?> handleException(ResourceNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
