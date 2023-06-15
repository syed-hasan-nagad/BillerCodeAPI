package com.billercode.api.billercodeapi.controllers;

import com.billercode.api.billercodeapi.exceptions.CustomTimeoutException;
import com.billercode.api.billercodeapi.exceptions.CustomUnmappedResponseException;
import com.billercode.api.billercodeapi.models.Biller;
import com.billercode.api.billercodeapi.models.BillerValidation;
import com.billercode.api.billercodeapi.models.ConfirmationJson;
import com.billercode.api.billercodeapi.models.ConfirmationResponse;
import com.billercode.api.billercodeapi.services.BillerService;
import com.billercode.api.billercodeapi.services.BillerValidationStorageService;
import com.billercode.api.billercodeapi.utils.SendHTTPRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.util.Optional;

import static com.billercode.api.billercodeapi.controllers.VerificationController.hashCreator;


@RestController
@RequestMapping("/confirmation")
public class ConfirmationController {

    private final BillerService billerService = new BillerService();
    private final BillerValidationStorageService billerValidationStorageService = new BillerValidationStorageService();
    Map<String, String> headers = new HashMap<>();
    @Autowired
    private Gson gson;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> routeRequest(@RequestBody ConfirmationJson request) {

        try {
            Optional<BillerValidation> validationBiller = billerValidationStorageService.getBillerValidationBySessionID(request.getValidationId());
            String hash = hashCreator.createSHAHash(request.getValidationId() + request.getParam1());
            if (validationBiller.isEmpty()) {
//                throw new CustomDataNotFoundException("Transaction validation is not found.");
                return ResponseEntity.status(400).body("Error: Transaction validation is not found.");
            }
            if (validationBiller.get().validationStatus().equalsIgnoreCase("fail")) {
//                throw new CustomDataNotFoundException("Transaction failed Validation.");
                return ResponseEntity.status(400).body("Error: Transaction failed Validation.");
            }
            if (validationBiller.get().confirmationStatus().equalsIgnoreCase("completed")) {
//                throw new CustomDataNotFoundException("Transaction is already completed.");
                return ResponseEntity.status(400).body("Error: Transaction is already completed.");
            }
            if ((validationBiller.get().validationStatus().equalsIgnoreCase("success")) && (validationBiller.get().hash().equalsIgnoreCase(hash)) && (validationBiller.get().confirmationStatus().equalsIgnoreCase("pending"))) {

                Biller biller = billerService.getBillerByBillerCodeFromDB(request.getBillerCode());
                URL url = new URL(biller.endpointUrl());
                Map<String, String> parameterMapping = biller.parameterMapping();
                String userRequestString = gson.toJson(request);
                Map<String, String> userRequestMap = gson.fromJson(userRequestString,
                                                                   new TypeToken<Map<String, String>>() {
                                                                   }.getType());

                Map<String, Object> requestBody = new HashMap<>();

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
                //ObjectMapper objectMapper = new ObjectMapper();
                //Response responseObject = objectMapper.readValue(response.get(0), Response.class);
                Map<String, String> responseMapping = biller.confirmationResponseMapping();
                Map<String, String> responseBody = new HashMap<>();

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> billerResponseMap = objectMapper.readValue(response.get(0),
                                                                               Map.class);

                responseMapping.forEach((key, value) -> {
                    if ((!value.isEmpty() && billerResponseMap.get(value) != null)) {
                        responseBody.put(key,
                                         billerResponseMap.get(value).toString());
                    }
                });


                if (biller.confirmationResponseStatusMapping().contentType().equalsIgnoreCase("application/json")) {
                    responseBody.put("status",
                                     billerResponseMap.get("status").toString());
                    responseBody.put("message",
                                     billerResponseMap.get("message").toString());
                    if (biller.confirmationResponseStatusMapping().success().contains(billerResponseMap.get(biller.confirmationResponseStatusMapping().method()))) {
                        responseBody.put("status","success");
                        responseBody.put("message","Confirmation successful.");

                        billerValidationStorageService.setConfirmationStatusToComplete(validationBiller.get());
                        billerValidationStorageService.setConfirmationResponse(validationBiller.get(),
                                                                               response.get(0));
                    } else if (biller.confirmationResponseStatusMapping().fail().contains(billerResponseMap.get(biller.confirmationResponseStatusMapping().method()))) {
                        responseBody.put("status","failed");
                        responseBody.put("message","Verification failed.");

                        billerValidationStorageService.setConfirmationStatusToFailed(validationBiller.get());
                        billerValidationStorageService.setConfirmationResponse(validationBiller.get(),
                                                                               response.get(0));
                    } else {
                        throw new CustomUnmappedResponseException(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                  "Response received from Biller not mapped.",
                                                                  response.get(0));
                    }
                } else if (biller.confirmationResponseStatusMapping().contentType().equalsIgnoreCase("HTTP/StatusCodes")) {
                    if (biller.confirmationResponseStatusMapping().success().contains(response.get(1))) {
                        responseBody.put("status","success");
                        responseBody.put("message","Verification successful.");
                        billerValidationStorageService.setConfirmationStatusToComplete(validationBiller.get());
                        billerValidationStorageService.setConfirmationResponse(validationBiller.get(),
                                                                               response.get(0));
                    } else if (biller.confirmationResponseStatusMapping().fail().contains(response.get(1))) {
                        responseBody.put("status","failed");
                        responseBody.put("message","Verification failed.");

                        billerValidationStorageService.setConfirmationStatusToFailed(validationBiller.get());
                        billerValidationStorageService.setConfirmationResponse(validationBiller.get(),
                                                                               response.get(0));
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

                ConfirmationResponse confirmationResponse = objectMapper.convertValue(responseBody,
                                                                                      ConfirmationResponse.class);
                return ResponseEntity.ok().body(confirmationResponse);
            } else {
                return ResponseEntity.status(400).body("Error: Bad request");
            }

        } catch (SocketTimeoutException e) {
            throw new CustomTimeoutException("Timed out with External Server");
//            return ResponseEntity.status(500).body("Error: Timed Out with External Server");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }

    }

}


