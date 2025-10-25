package com.bank.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.bank.dto.ErrorResponse;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidOtpException;
import com.bank.exception.ResourceNotFoundException;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(value={ResourceNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest req) {
        ErrorResponse er = new ErrorResponse();
        er.setStatus(HttpStatus.NOT_FOUND.value());
        er.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        er.setMessage(ex.getMessage());
        er.setPath(req.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(er);
    }

    @ExceptionHandler(value={InsufficientFundsException.class})
    public ResponseEntity<ErrorResponse> handleInsufficient(InsufficientFundsException ex, WebRequest req) {
        ErrorResponse er = new ErrorResponse();
        er.setStatus(HttpStatus.CONFLICT.value());
        er.setError(HttpStatus.CONFLICT.getReasonPhrase());
        er.setMessage(ex.getMessage());
        er.setPath(req.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(er);
    }

    @ExceptionHandler(value={InvalidOtpException.class})
    public ResponseEntity<ErrorResponse> handleInvalidOtp(InvalidOtpException ex, WebRequest req) {
        ErrorResponse er = new ErrorResponse();
        er.setStatus(HttpStatus.FORBIDDEN.value());
        er.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
        er.setMessage(ex.getMessage());
        er.setPath(req.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(er);
    }

    @ExceptionHandler(value={MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(f -> f.getField() + ": " + f.getDefaultMessage()).collect(Collectors.toList());
        ErrorResponse er = new ErrorResponse();
        er.setStatus(HttpStatus.BAD_REQUEST.value());
        er.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        er.setMessage("Validation failed");
        er.setErrors(errors);
        er.setPath(req.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(er);
    }

    @ExceptionHandler(value={ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest req) {
        List<String> errors = ex.getConstraintViolations().stream().map(v -> String.valueOf(v.getPropertyPath()) + ": " + v.getMessage()).collect(Collectors.toList());
        ErrorResponse er = new ErrorResponse();
        er.setStatus(HttpStatus.BAD_REQUEST.value());
        er.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        er.setMessage("Validation failed");
        er.setErrors(errors);
        er.setPath(req.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(er);
    }

    @ExceptionHandler(value={Exception.class})
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, WebRequest req) {
        ErrorResponse er = new ErrorResponse();
        er.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        er.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        er.setMessage(ex.getMessage());
        er.setPath(req.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
    }
}
