package com.example.erp.exception;

public class BootstrapConflictException extends RuntimeException {
    public BootstrapConflictException() { super("Bootstrap is unavailable"); }
}