package com.billercode.api.billercodeapi.exceptions;

import org.springframework.http.HttpStatus;

public class CustomUnmappedResponseException extends RuntimeException {
    private String response;
    private HttpStatus status;

    public CustomUnmappedResponseException() {
        super();
    }

    public CustomUnmappedResponseException(String message) {
        super(message);
    }

    public CustomUnmappedResponseException(
            HttpStatus status,
            String message,
            String response
    ) {
        this(message);
        this.status = status;
        this.response = response;
    }
}