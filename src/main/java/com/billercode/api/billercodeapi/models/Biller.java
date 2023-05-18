package com.billercode.api.billercodeapi.models;

import java.util.Map;

public class Biller {
    private final String billerCode;
    private final String billerName;
    private final String endpointUrl;
    private final String requestMethod;
    private final Map<String,String> parameterMapping;
    private final Map<String,String> connectionSettings;


    public Biller(String billerCode, String billerName, String endpointUrl, String requestMethod, Map<String, String> parameterMapping, Map<String, String> connectionSettings) {
        this.billerCode = billerCode;
        this.billerName = billerName;
        this.endpointUrl = endpointUrl;
        this.requestMethod = requestMethod;
        this.parameterMapping = parameterMapping;
        this.connectionSettings = connectionSettings;
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

    public String getRequestMethod() {
        return requestMethod;
    }

    public Map<String, String> getParameterMapping() {
        return parameterMapping;
    }
    public Map<String, String> getConnectionSettings() { return connectionSettings; }

}
