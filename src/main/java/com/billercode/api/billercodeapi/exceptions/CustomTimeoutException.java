package com.billercode.api.billercodeapi.exceptions;

public class CustomTimeoutException extends RuntimeException {
    public CustomTimeoutException() {
        super();
    }

    public CustomTimeoutException(String message) {
        super(message);
    }
}