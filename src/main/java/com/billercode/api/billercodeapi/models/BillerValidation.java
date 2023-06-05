package com.billercode.api.billercodeapi.models;

public record BillerValidation(String billerCode, String sessionId, String validationStatus, String confirmationStatus,
                               String hash, String verificationResponse, String confirmationResponse) {
}
