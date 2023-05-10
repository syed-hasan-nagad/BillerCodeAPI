package com.billercode.api.billercodeapi.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/paybill/electricitybill")
public class ElectricityBillerController {
    @Autowired
    Gson gson;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String payBill(@RequestBody String request){
        Type requestMapType = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> requestMap = gson.fromJson(request, requestMapType);
        requestMap.put("message","Electricity Bill has been paid Successfully!");
        String response = gson.toJson(requestMap);

        BigDecimal amount = new BigDecimal(requestMap.get("amount").toString());
        String accountNo = requestMap.get("accountNo").toString();
        String billNo = requestMap.get("billNo").toString();

        return response;

    }


}
