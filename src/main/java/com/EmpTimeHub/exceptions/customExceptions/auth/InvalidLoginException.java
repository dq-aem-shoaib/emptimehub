package com.EmpTimeHub.exceptions.customExceptions.auth;

public class InvalidLoginException extends RuntimeException{
    public InvalidLoginException(String message) {
        super(message);
    }
}
