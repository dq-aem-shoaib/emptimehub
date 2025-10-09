package com.EmpTimeHub.exceptions.customExceptions;

public class UserNotSavedException extends RuntimeException {
    public UserNotSavedException(String message) {
        super(message);
    }
}
