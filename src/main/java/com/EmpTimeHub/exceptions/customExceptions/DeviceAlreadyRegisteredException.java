package com.EmpTimeHub.exceptions.customExceptions;

public class DeviceAlreadyRegisteredException extends RuntimeException {
    public DeviceAlreadyRegisteredException(String message) {
        super(message);
    }
}