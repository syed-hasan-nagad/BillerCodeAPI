package com.billercode.api.billercodeapi.controllers;

import com.billercode.api.billercodeapi.models.Biller;
import com.billercode.api.billercodeapi.models.BillerValidation;
import com.billercode.api.billercodeapi.models.ValidationJson;
import com.billercode.api.billercodeapi.services.BillerService;
import com.billercode.api.billercodeapi.services.BillerValidationStorageService;
import com.billercode.api.billercodeapi.services.sessionIdGenerator;
import com.billercode.api.billercodeapi.utils.HashCreator;
import com.billercode.api.billercodeapi.utils.SendHTTPRequest;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "validation")
public record ValidationController(Gson gson) {

    static final BillerService billerService = new BillerService();
    static final BillerValidationStorageService billerValidationStorageService =  new BillerValidationStorageService();
    static final HashCreator hashCreator = new HashCreator();

    @PostMapping
    public ResponseEntity<BillerValidation> BillValidation(@RequestBody ValidationJson request) throws SQLException, IOException, NoSuchAlgorithmException, KeyManagementException {
        String billerCode = request.getBillerCode();
        Biller biller = billerService.getBillerByBillerCodefromDB(billerCode);
        String sessionID = sessionIdGenerator.getNewSessionId();

        URL url = new URL(biller.validationUrl());
        Map <String,Object> requestBody = new HashMap<>();
        ArrayList<String> response = SendHTTPRequest.sendHttpRequest(
                biller.requestMethod(),
                requestBody,
                url,
                biller.connectionTimeout(),
                biller.readTimeout(),
                biller.contentType(),
                biller.enableSSL(),
                biller.tlsVersion()
        );




        BillerValidation billerValidation;
        int status = Integer.parseInt(response.get(1));
        String hash = hashCreator.createSHAHash(sessionID+request.getParam1());

        if((status>=200)&&(status<400) ){

            billerValidation = new BillerValidation(billerCode, sessionID, "success", "pending", hash);
        }
        else{
            billerValidation = new BillerValidation(billerCode, sessionID, "fail", null, hash);
        }
        billerValidationStorageService.saveBillerValidation(billerValidation);
        return ResponseEntity.ok().body(billerValidation);

    }
}
