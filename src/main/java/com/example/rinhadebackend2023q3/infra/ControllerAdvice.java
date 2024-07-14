package com.example.rinhadebackend2023q3.infra;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j //info I'm not logging a lot of stack traces exceptions here. This should never be done in production. I just want visibility on internal errors for testing purposes
public class ControllerAdvice {

    @ExceptionHandler({ConstraintViolationException.class, UnprocessableEntityException.class, })
    public ResponseEntity<?> handleConstraintViolation(Exception e) {
//        log.warn("constraint violation occurred: {}", e.getMessage());
        return ResponseEntity.unprocessableEntity().build();
    }

    @ExceptionHandler({BadRequesException.class, MissingServletRequestParameterException.class, HttpMessageNotReadableException.class, HttpMessageConversionException.class})
    public ResponseEntity<?> handleBadRequestException(Exception e) {
//        log.warn("bad request occurred: {}", e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(Exception e) {
//        log.warn("not found occurred: {}", e.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> generalException(Exception e) {
        log.error("unexpected error occurred:", e);
        return ResponseEntity.internalServerError().build();
    }
}
