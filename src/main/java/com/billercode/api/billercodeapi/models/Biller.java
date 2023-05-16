package com.billercode.api.billercodeapi.models;

import java.util.Map;

public class Biller {
    private final String billerCode;
    private final String billerName;
    private final String endpointUrl;
    private final Map<String,String> parameterMapping;

    public Biller(String billerCode, String billerName, String endpointUrl, Map<String, String> parameterMapping) {
        this.billerCode = billerCode;
        this.billerName = billerName;
        this.endpointUrl = endpointUrl;
        this.parameterMapping = parameterMapping;
    }


    public String getBillerCode() {
        return billerCode;
    }

    public String getBillerName() {
        return billerName;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public Map<String, String> getParameterMapping() {
        return parameterMapping;
    }
}
