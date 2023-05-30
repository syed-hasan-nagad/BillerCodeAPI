package com.billercode.api.billercodeapi.controllers;

import com.billercode.api.billercodeapi.models.Biller;
import com.billercode.api.billercodeapi.models.BillerValidation;
import com.billercode.api.billercodeapi.models.ConfirmationJson;
import com.billercode.api.billercodeapi.services.BillerService;
import com.billercode.api.billercodeapi.services.BillerValidationStorageService;
import com.billercode.api.billercodeapi.utils.SendHTTPRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import java.util.Optional;

import static com.billercode.api.billercodeapi.controllers.ValidationController.hashCreator;


@RestController
@RequestMapping("/airlines")
public class ConfirmationController {

    private final BillerService billerService = new BillerService();
    private final BillerValidationStorageService billerValidationStorageService = new BillerValidationStorageService();
    Map<String, String> headers = new HashMap<>();
    @Autowired
    private Gson gson;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> routeRequest(@RequestBody ConfirmationJson request) throws SQLException, IOException, NoSuchAlgorithmException, KeyManagementException {

        Optional<BillerValidation> validationBiller = billerValidationStorageService.getBillerValidationBySessionID(request.getValidationId());
        String hash = hashCreator.createSHAHash(request.getValidationId()+request.getParam1());
        if (validationBiller.isEmpty()) {
            return ResponseEntity.status(400).body("Error: Transaction validation is not found.");
        }
        if (validationBiller.get().getStatus().equalsIgnoreCase("fail")) {
            return ResponseEntity.status(400).body("Error: Transaction failed Validation.");
        }
        if (validationBiller.get().getStatus().equalsIgnoreCase("completed")) {
            return ResponseEntity.status(400).body("Error: Transaction already Completed.");
        }
        if ((validationBiller.get().getStatus().equalsIgnoreCase("success"))
                && (validationBiller.get().getHash().equalsIgnoreCase(hash))) {

            Biller biller = billerService.getBillerByBillerCodefromDB(request.getBillerCode());
            URL url = new URL(biller.getEndpointUrl());
            Map<String, String> parameterMapping = biller.getParameterMapping();
            String userRequestString = gson.toJson(request);
            Map<String, String> userRequestMap = gson.fromJson(userRequestString, new TypeToken<Map<String, String>>() {
            }.getType());

            Map<String, Object> requestBody = new HashMap<>();

            parameterMapping.forEach((key, value) -> {
                if (!value.isEmpty()) {
                    requestBody.put(value, userRequestMap.get(key));
                }
            });

            ArrayList<String> response = SendHTTPRequest.sendHttpRequest(
                    biller.getRequestMethod(),
                    requestBody,
                    url,
                    biller.getConnectionTimeout(),
                    biller.getReadTimeout(),
                    biller.getContentType(),
                    biller.isEnableSSL(),
                    biller.getTlsVersion()
            );
            if (response.get(1).equals("200")) {
                billerValidationStorageService.setBillerValidationToComplete(validationBiller.get());
            }
            return ResponseEntity.ok().body(response.get(0));
        } else {
            return ResponseEntity.status(400).body("Error: Bad request");
        }

    }

}
