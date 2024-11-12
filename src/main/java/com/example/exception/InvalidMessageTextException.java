package com.example.exception;

public class InvalidMessageTextException extends RuntimeException{
    public InvalidMessageTextException(String message) {
        super(message);
    }
}
