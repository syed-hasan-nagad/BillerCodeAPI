package com.billercode.api.billercodeapi.models;

public class BillerValidation {
    private String billerCode;
    private String sessionId;

    private String status;

    public BillerValidation(String billerCode, String sessionId, String status) {
        this.billerCode = billerCode;
        this.sessionId = sessionId;
        this.status = status;
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
