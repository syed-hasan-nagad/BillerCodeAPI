package com.billercode.api.billercodeapi.controllers;

import com.billercode.api.billercodeapi.models.RequestJson;
import com.billercode.api.billercodeapi.utils.SendHTTPRequest;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/airlines")
public class AirlineController {

    @Autowired
    private Gson gson;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> routeRequest(@RequestBody RequestJson request) {
        try {

            HashMap<String, String> apiList = new HashMap<>();
            Map<String, String> headers = new HashMap<>();
            URL endpointUrl;
            ResponseEntity<String> response;

            Map<String, Object> requestPayload = new HashMap<>();
            apiList.put("1101", "https://api.instantwebtools.net/v1/airlines");
            apiList.put("1102", "https://api.instantwebtools.net/v1/passenger");


            String billerCode = request.getBillerCode();
            if (billerCode == null) {
                return ResponseEntity.badRequest().body("Biller code cannot be null");
            }

            String responseBody;

            switch (billerCode) {
                case "1101" -> { // add airlines
                    endpointUrl = new URL(apiList.get(billerCode));

                    // setting the headers to use for the request
                    headers.put("Content-Type", "application/json");
                    requestPayload.put("name", request.getParam1());
                    requestPayload.put("country", request.getParam2());
                    requestPayload.put("logo", request.getParam3());
                    requestPayload.put("slogan", request.getParam4());
                    requestPayload.put("head_quarters", request.getParam5());
                    requestPayload.put("website", request.getParam6());
                    requestPayload.put("established", request.getParam7());

                    //Using HttpUrlConnection
                    try {
                        // sending the request
                        responseBody = SendHTTPRequest.sendHttpRequest("POST",10000, requestPayload, endpointUrl, headers);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = ResponseEntity.ok().body(responseBody);
                }
                case "1102" -> { // add passenger
                    endpointUrl = new URL(apiList.get(billerCode));
                    requestPayload.put("name", request.getParam1());
                    requestPayload.put("trips", request.getParam2());
                    requestPayload.put("airline", request.getParam3());
                    try {
                        responseBody = SendHTTPRequest.sendHttpRequest("POST",10000, requestPayload, endpointUrl, headers);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = ResponseEntity.ok().body(responseBody);
                }
                //response = ResponseEntity.ok().body(restTemplate.postForObject(endpointUrl.toURI(), requestPayload, String.class));
                default -> { // incase code not in use here
                    requestPayload.put("Message", "The code sent is not valid for any vendors");
                    response = ResponseEntity.badRequest().body(gson.toJson(requestPayload));
                }
            }


            return response;

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
