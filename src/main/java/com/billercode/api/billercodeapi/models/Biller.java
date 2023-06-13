package com.billercode.api.billercodeapi.models;

import java.util.Map;

public record Biller(String billerCode, String billerName, String endpointUrl, String requestMethod,
                     Map<String, String> parameterMapping, int connectionTimeout, int readTimeout, String contentType,
                     String tlsVersion, boolean enableSSL, String validationUrl,
                     Map<String, String> verificationResponseMapping, Map<String, String> confirmationResponseMapping,
                     ResponseStatusMapping verificationResponseStatusMapping,
                     ResponseStatusMapping confirmationResponseStatusMapping) {

}
