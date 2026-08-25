package com.example.erp.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() { super("Resource not found"); }
}