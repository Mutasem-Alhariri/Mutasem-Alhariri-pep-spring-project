package com.example.exception;

public class NoAssociatedUserException extends RuntimeException{
    public NoAssociatedUserException(String message) {
        super(message);
    }
}
