package com.billercode.api.billercodeapi.controllers;

import com.billercode.api.billercodeapi.models.PayBillRequestBody;
import com.billercode.api.billercodeapi.services.BillerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PayBillController {




    @PostMapping("/paybill")
    public String makePayment(@RequestBody PayBillRequestBody request) {
        try {
             BillerService billerService = new BillerService();
            HashMap<String, String> billers = billerService.getBillersfromDB();
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            URL endpointUrl= new URL(uri.toString()+billers.get(request.getBillerCode()));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<PayBillRequestBody> httpEntity = new HttpEntity<>(request, headers);

            //ResponseEntity<String> response = restTemplate.exchange(endpointUrl.toURI(), HttpMethod.POST, httpEntity, String.class);
            return endpointUrl.toString();

        } catch (SQLException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
