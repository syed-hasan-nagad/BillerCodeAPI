package com.billercode.api.billercodeapi.models;

public class Biller {
    public Biller(String billerCode, String billerName, String endpointUrl) {
        this.billerCode = billerCode;
        this.billerName = billerName;
        this.endpointUrl = endpointUrl;
    }


    private String billerCode;
    private String billerName;

    private String endpointUrl;
}
