package com.EmpTimeHub.exceptions.customExceptions;

public class EmptyUserListException extends RuntimeException {
    public EmptyUserListException(String message) {
        super(message);
    }
}
