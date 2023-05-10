package com.billercode.api.billercodeapi.controllers;

import com.billercode.api.billercodeapi.models.RequestJson;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/airlines")
public class AirlineController {

    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private Gson gson;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> routeRequest(@RequestBody RequestJson request) {
        try {

            HashMap<String, String> apiList = new HashMap<>();
            URL endpointUrl;
            ResponseEntity<String> response = null;
            Map<String, Object> requestPayload = new HashMap<>();
            apiList.put("1101", "https://api.instantwebtools.net/v1/airlines");
            apiList.put("1102", "https://api.instantwebtools.net/v1/passenger");


            String billerCode = request.getBillerCode();
            if (billerCode == null) {
                return ResponseEntity.badRequest().body("Biller code cannot be null");
            }

            switch (billerCode) {
                case "1101": // add airlines
                    endpointUrl = new URL(apiList.get(billerCode));

                    requestPayload.put("name", request.getParam1());
                    requestPayload.put("country", request.getParam2());
                    requestPayload.put("logo", request.getParam3());
                    requestPayload.put("slogan", request.getParam4());
                    requestPayload.put("head_quarters", request.getParam5());
                    requestPayload.put("website", request.getParam6());
                    requestPayload.put("established", request.getParam7());

                    response = ResponseEntity.ok().body(restTemplate.postForObject(endpointUrl.toURI(), requestPayload, String.class));
                    break;
                case "1102": // add passenger
                    endpointUrl = new URL(apiList.get(billerCode));

                    requestPayload.put("name", request.getParam1());
                    requestPayload.put("trips", request.getParam2());
                    requestPayload.put("airline", request.getParam3());

                    response = ResponseEntity.ok().body(restTemplate.postForObject(endpointUrl.toURI(), requestPayload, String.class));
                    break;

                default: // incase code not in use here
                    requestPayload.put("Message", "The code sent is not valid for any vendors");
                    response = ResponseEntity.badRequest().body(gson.toJson(requestPayload));
            }


            return response;

        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
