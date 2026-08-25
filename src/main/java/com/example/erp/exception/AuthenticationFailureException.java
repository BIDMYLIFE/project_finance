package com.example.erp.exception;

public class AuthenticationFailureException extends RuntimeException {
    public AuthenticationFailureException() { super("Authentication failed"); }
}