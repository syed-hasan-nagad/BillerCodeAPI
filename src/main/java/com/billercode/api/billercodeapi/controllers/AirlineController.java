package com.billercode.api.billercodeapi.controllers;

import com.billercode.api.billercodeapi.models.Biller;
import com.billercode.api.billercodeapi.models.RequestJson;
import com.billercode.api.billercodeapi.services.BillerService;
import com.billercode.api.billercodeapi.utils.SendHTTPRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/airlines")
public class AirlineController {

    @Autowired
    private Gson gson;

    private BillerService billerService = new BillerService();


    Map<String, String> headers = new HashMap<>();
    URL endpointUrl;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllAirlines() throws IOException {
        headers.put("Content-Type", "application/json");
        endpointUrl = new URL("https://api.instantwebtools.net/v1/airlines");
        String res =  SendHTTPRequest.sendHttpRequest("GET",10000,10000,null,endpointUrl,headers);

        return ResponseEntity.ok().body(res);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> routeRequest(@RequestBody RequestJson request) throws SQLException, IOException {

        Biller biller =  billerService.getBillerByBillerCodefromDB(request.getBillerCode());

        URL url = new URL(biller.getEndpointUrl());
        headers.put("Content-Type", "application/json");
        Map <String, String> parameterMapping =  biller.getParameterMapping();
        String userRequestString = gson.toJson(request);
        Map<String, String> userRequestMap = gson.fromJson(userRequestString,new TypeToken<Map<String, String>>() {}.getType());

        Map <String,Object> requestBody = new HashMap<>();

        parameterMapping.forEach((key,value)->{
            if(!value.isEmpty()){
                requestBody.put(value,userRequestMap.get(key));
            }
        });

       String response = SendHTTPRequest.sendHttpRequest(biller.getRequestMethod(),10000,10000,requestBody,url,headers);

        return ResponseEntity.ok().body(response);
    }

}
