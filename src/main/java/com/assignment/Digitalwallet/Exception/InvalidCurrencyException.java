package com.assignment.Digitalwallet.Exception;

public class InvalidCurrencyException extends RuntimeException {
    public InvalidCurrencyException(String message) {
        super(message);
    }
    
    public InvalidCurrencyException(String currency, String message) {
        super("Invalid currency code: " + currency + ". " + message);
    }
    
    public InvalidCurrencyException(String currency, Throwable cause) {
        super("Invalid currency code: " + currency + ". " + cause.getMessage(), cause);
    }
}