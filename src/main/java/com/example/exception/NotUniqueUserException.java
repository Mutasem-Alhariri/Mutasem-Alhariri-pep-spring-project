package com.example.exception;

public class NotUniqueUserException extends RuntimeException{
    public NotUniqueUserException(String message) {
        super(message);
    }
}
