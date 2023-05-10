package com.billercode.api.billercodeapi.controllers;

import com.billercode.api.billercodeapi.services.BillerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;



@RestController
@RequestMapping("/paybill")
public class PayBillController {

    @Autowired
    private Gson gson;
    private final RestTemplate restTemplate = new RestTemplate();


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String makePayment(@RequestBody String request) {
        try {
            BillerService billerService = new BillerService();
            HashMap<String, String> billers = billerService.getBillersfromDB();

            Type requestMapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestMap = gson.fromJson(request, requestMapType);

            String billerCode = requestMap.get("billerCode").toString();
            if (billers.get(billerCode) == null) {
                return "Biller code is invalid";
            }

            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            URL endpointUrl = new URL(uri + billers.get(billerCode));

            requestMap.remove("billerCode");
            String jsonPayload = gson.toJson(requestMap);


            String response = restTemplate.postForObject(endpointUrl.toURI(), jsonPayload, String.class);
            return response;

        } catch (SQLException | MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
