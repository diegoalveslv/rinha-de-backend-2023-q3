package com.example.rinhadebackend2023q3.infra;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
