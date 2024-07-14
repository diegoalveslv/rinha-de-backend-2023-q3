package com.example.rinhadebackend2023q3.infra;

public class BadRequesException extends RuntimeException {
    public BadRequesException(String message) {
        super(message);
    }
}
