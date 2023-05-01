package com.billercode.api.billercodeapi.models;

import java.math.BigDecimal;

public class PayBillRequestBody {
    private String customerID;

    public String getBillerCode() {
        return billerCode;
    }

    private String billerCode;
    private String referenceNo;
    private BigDecimal amount;
}
