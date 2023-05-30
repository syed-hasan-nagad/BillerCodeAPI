package com.billercode.api.billercodeapi.models;

import java.util.Map;

public class Biller {
    private final String billerCode;
    private final String billerName;
    private final String endpointUrl;
    private final String requestMethod;
    private final Map<String,String> parameterMapping;
    private final int connectionTimeout;
    private final int readTimeout;
    private final String contentType;
    private final String tlsVersion;
    private final boolean enableSSL;
    private final String validationUrl;

    public Biller(String billerCode, String billerName, String endpointUrl, String requestMethod, Map<String, String> parameterMapping, int connectionTimeout, int readTimeout, String contentType, String tlsVersion, boolean enableSSL, String validationUrl) {
        this.billerCode = billerCode;
        this.billerName = billerName;
        this.endpointUrl = endpointUrl;
        this.requestMethod = requestMethod;
        this.parameterMapping = parameterMapping;
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
        this.contentType = contentType;
        this.tlsVersion = tlsVersion;
        this.enableSSL = enableSSL;
        this.validationUrl = validationUrl;
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

    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    public int getReadTimeout() {
        return readTimeout;
    }

    public String getContentType() {
        return contentType;
    }

    public String getTlsVersion() {
        return tlsVersion;
    }

    public boolean isEnableSSL() {
        return enableSSL;
    }

    public String getValidationUrl() {
        return validationUrl;
    }

}
