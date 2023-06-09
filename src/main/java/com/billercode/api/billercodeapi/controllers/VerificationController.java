package com.billercode.api.billercodeapi.controllers;

import com.billercode.api.billercodeapi.exceptions.CustomDataNotFoundException;
import com.billercode.api.billercodeapi.exceptions.CustomTimeoutException;
import com.billercode.api.billercodeapi.exceptions.CustomUnmappedResponseException;
import com.billercode.api.billercodeapi.models.Biller;
import com.billercode.api.billercodeapi.models.BillerValidation;
import com.billercode.api.billercodeapi.models.ValidationJson;
import com.billercode.api.billercodeapi.models.VerificationResponse;
import com.billercode.api.billercodeapi.services.BillerService;
import com.billercode.api.billercodeapi.services.BillerValidationStorageService;
import com.billercode.api.billercodeapi.services.sessionIdGenerator;
import com.billercode.api.billercodeapi.utils.HashCreator;
import com.billercode.api.billercodeapi.utils.SendHTTPRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "verification")
public record VerificationController(Gson gson) {

    static final BillerService billerService = new BillerService();
    static final BillerValidationStorageService billerValidationStorageService = new BillerValidationStorageService();
    static final HashCreator hashCreator = new HashCreator();

    @PostMapping
    public ResponseEntity<Object> BillValidation(@RequestBody ValidationJson request) {
        try {
            String billerCode = request.getBillerCode();
            Biller biller = billerService.getBillerByBillerCodeFromDB(billerCode);
            String sessionID = sessionIdGenerator.getNewSessionId();

            URL url = new URL(biller.validationUrl());
            Map<String, Object> requestBody = new HashMap<>();

            Map<String, String> parameterMapping = biller.parameterMapping();
            String userRequestString = gson.toJson(request);
            Map<String, String> userRequestMap = gson.fromJson(userRequestString,
                                                               new TypeToken<Map<String, String>>() {
                                                               }.getType());

            parameterMapping.forEach((key, value) -> {
                if (!value.isEmpty()) {
                    requestBody.put(value,
                                    userRequestMap.get(key));
                }
            });

            ArrayList<String> response = SendHTTPRequest.sendHttpRequest(biller.requestMethod(),
                                                                         requestBody,
                                                                         url,
                                                                         biller.connectionTimeout(),
                                                                         biller.readTimeout(),
                                                                         biller.contentType(),
                                                                         biller.enableSSL(),
                                                                         biller.tlsVersion());

            Map<String, String> responseMapping = biller.verificationResponseMapping();
            Map<String, String> responseBody = new HashMap<>();

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> billerResponseMap = objectMapper.readValue(response.get(0),
                                                                           Map.class);
            //Map<String, String> billerResponseMap = gson.fromJson(response.get(0), new TypeToken<Map<String, String>>() {}.getType());

            responseMapping.forEach((key, value) -> {
                if ((!value.isEmpty() && (billerResponseMap.get(value) != null))) {
                    responseBody.put(key,
                                     billerResponseMap.get(value).toString());
                }
            });
            responseBody.put("validationId",
                             sessionID);
//            responseBody.put("status",
//                             billerResponseMap.get("status").toString());
//            responseBody.put("message",
//                             billerResponseMap.get("message").toString());


            BillerValidation billerValidation;
            String hash = hashCreator.createSHAHash(sessionID + request.getParam1());
            if (biller.verificationResponseStatusMapping().contentType().equalsIgnoreCase("application/json")) {

                if (biller.verificationResponseStatusMapping().success().contains(billerResponseMap.get(biller.verificationResponseStatusMapping().method()))) {

                    responseBody.put("status","success");
                    responseBody.put("message","Verification successful.");

                    billerValidation = new BillerValidation(billerCode,
                                                            sessionID,
                                                            "success",
                                                            "pending",
                                                            hash,
                                                            response.get(0),
                                                            null);
                } else if (biller.verificationResponseStatusMapping().fail().contains(billerResponseMap.get(biller.verificationResponseStatusMapping().method()))) {
                    responseBody.put("status","failed");
                    responseBody.put("message","Verification failed.");

                    billerValidation = new BillerValidation(billerCode,
                                                            sessionID,
                                                            "fail",
                                                            null,
                                                            hash,
                                                            response.get(0),
                                                            null);
                } else {
                    throw new CustomUnmappedResponseException(HttpStatus.INTERNAL_SERVER_ERROR,
                                                              "Response received from Biller not mapped.",
                                                              response.get(0));
                }
            } else if (biller.verificationResponseStatusMapping().contentType().equalsIgnoreCase("HTTP/StatusCodes")) {
                if (biller.verificationResponseStatusMapping().success().contains(response.get(1))) {
                    responseBody.put("status","success");
                    responseBody.put("message","Verification successful.");

                    billerValidation = new BillerValidation(billerCode,
                                                            sessionID,
                                                            "success",
                                                            "pending",
                                                            hash,
                                                            response.get(0),
                                                            null);
                } else if (biller.verificationResponseStatusMapping().fail().contains(response.get(1))) {
                    responseBody.put("status","failed");
                    responseBody.put("message","Verification failed.");

                    billerValidation = new BillerValidation(billerCode,
                                                            sessionID,
                                                            "fail",
                                                            null,
                                                            hash,
                                                            response.get(0),
                                                            null);
                } else {
                    throw new CustomUnmappedResponseException(HttpStatus.INTERNAL_SERVER_ERROR,
                                                              "Response received from Biller not mapped.",
                                                              response.get(0));
                }
            } else {
                throw new CustomUnmappedResponseException(HttpStatus.INTERNAL_SERVER_ERROR,
                                                          "Response method received from Biller not mapped.",
                                                          response.get(0));
            }

            billerValidationStorageService.saveBillerValidation(billerValidation);
            VerificationResponse verificationResponse = objectMapper.convertValue(responseBody,
                                                                                  VerificationResponse.class);
            return ResponseEntity.ok().body(verificationResponse);
        } catch (SQLException e) {
            throw new CustomTimeoutException("SQL");
        } catch (SocketTimeoutException e) {
            throw new CustomTimeoutException("The connection to the Biller API timed out");
            //return ResponseEntity.status(500).body("Error: Timed Out with External Server");
        } catch (NullPointerException e) {
            throw new CustomDataNotFoundException("Data Not Found");
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

    }
}
