package com.billercode.api.billercodeapi.utils;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class SendHTTPRequest {


    static Gson gson = new Gson();
    public static String sendHttpRequest(String requestMethod, int connectionTimeout,int readTimeout, Map<String, Object> requestPayload, URL endpointUrl, Map<String,String> headers) throws IOException {
        BufferedReader streamReader;
        StringBuilder fullResponseBuilder =  new StringBuilder();

        HashSet<String> requestMethods = new HashSet<>(Arrays.asList( "GET", "POST", "PUT", "DELETE"));

        HttpURLConnection connection =  (HttpURLConnection) endpointUrl.openConnection();

        if (requestMethods.contains(requestMethod) ) {
            connection.setRequestMethod(requestMethod.toUpperCase());
            if (!headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
        }
            //connection.setRequestProperty("Accept","application/json"); Not required most of the time
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
