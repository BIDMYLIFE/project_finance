package com.example.erp.exception;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) { super(message); }
}