package com.example.erp.exception;

public class InvalidSessionException extends RuntimeException {
    public InvalidSessionException() { super("Session is invalid"); }
}