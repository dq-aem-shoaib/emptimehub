package com.EmpTimeHub.exceptions.customExceptions;

public class UserRoleNotFoundException extends RuntimeException {
    public UserRoleNotFoundException(String message) {
        super(message);
    }
}
