package com.billercode.api.billercodeapi.utils;

import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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
    public static String sendHttpRequest(String requestMethod,
                                         Map<String, Object> requestPayload,
                                         URL endpointUrl,
                                         int connectionTimeout,
                                         int readTimeout,
                                         String contentType,
                                         boolean enableSSL,
                                         String tlsVersion) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        BufferedReader streamReader;
        StringBuilder fullResponseBuilder =  new StringBuilder();

        HashSet<String> requestMethods = new HashSet<>(Arrays.asList( "GET", "POST", "PUT", "DELETE"));

        HttpURLConnection connection ;

        if (enableSSL){
            connection = (HttpsURLConnection) endpointUrl.openConnection();
        }else{
            connection = (HttpURLConnection) endpointUrl.openConnection();
        }




        if (requestMethods.contains(requestMethod) ) {
            connection.setRequestMethod(requestMethod.toUpperCase());
            if(enableSSL){
                SSLContext sslContext = SSLContext.getInstance(tlsVersion);
                sslContext.init(null, null, new SecureRandom());
                ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
            }
            connection.setRequestProperty("Content-Type", contentType);
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

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
