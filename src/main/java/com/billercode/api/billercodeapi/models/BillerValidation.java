package com.billercode.api.billercodeapi.models;

public class BillerValidation {
    private final String billerCode;
    private final String sessionId;

    private final String status;
    private final String hash;

    public BillerValidation(String billerCode, String sessionId, String status, String hash) {
        this.billerCode = billerCode;
        this.sessionId = sessionId;
        this.status = status;
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public String getBillerCode() {
        return billerCode;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getStatus() {
        return status;
    }
}
