package com.billercode.api.billercodeapi.utils;

import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class SendHTTPRequest {
    static Gson gson = new Gson();
    public static String sendHttpRequest(String requestMethod, Map<String, Object> requestPayload, URL endpointUrl, Map<String,String> connectionSettings) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        BufferedReader streamReader;
        StringBuilder fullResponseBuilder =  new StringBuilder();

        HashSet<String> requestMethods = new HashSet<>(Arrays.asList( "GET", "POST", "PUT", "DELETE"));

        HttpsURLConnection connection = (HttpsURLConnection) endpointUrl.openConnection();
        boolean enableSSL = Boolean.parseBoolean(connectionSettings.get("enableSSL"));



        if (requestMethods.contains(requestMethod) ) {
            connection.setRequestMethod(requestMethod.toUpperCase());
            if(enableSSL){
                SSLContext sslContext = SSLContext.getInstance(connectionSettings.get("tlsVersion"));
                sslContext.init(null, null, new SecureRandom());
                connection.setSSLSocketFactory(sslContext.getSocketFactory());
            }
            connection.setRequestProperty("Content-Type", connectionSettings.get("contentType"));
            connection.setConnectTimeout(Integer.parseInt(connectionSettings.get("connectionTimeout")));
            connection.setReadTimeout(Integer.parseInt(connectionSettings.get("readTimeout")));

            if (!requestMethod.equalsIgnoreCase("GET")){
                connection.setDoOutput(true);
                String jsonString = gson.toJson(requestPayload);
                OutputStream outStream = connection.getOutputStream();
                byte[] jsonBody = jsonString.getBytes(StandardCharsets.UTF_8);
                outStream.write(jsonBody,0,jsonBody.length);
            }
            else{
                connection.setDoOutput(false);
            }

            int status = connection.getResponseCode();

            if (status > 299) {
                streamReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                streamReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            return fullResponseBuilder.append(streamReader.readLine()).toString();
        }
        else{
            throw new RuntimeException("Error! Request method is incorrect");
        }

    }
}
