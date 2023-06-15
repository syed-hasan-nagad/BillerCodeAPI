package com.billercode.api.billercodeapi.services;

import com.billercode.api.billercodeapi.models.Biller;
import com.billercode.api.billercodeapi.models.ResponseStatusMapping;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
@Service
public class BillerService {

    final Gson gson = new Gson();


    public Biller getBillerByBillerCodeFromDB(String billerCode) throws SQLException {
        Connection connection = DatabaseConnectionService.getDBConnection();
        Biller biller = null;
        boolean enableSslFlag = false;
        String sql = "SELECT * FROM SANZID.BILLERS WHERE biller_code = ?";


        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1,
                            billerCode);

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String endpointUrl = resultSet.getString("endpoint_url");
            billerCode = resultSet.getString("biller_code");
            String billerName = resultSet.getString("biller_name");
            String requestMethod = resultSet.getString("request_method");
            String parameterMappingString = resultSet.getString("parameter_mapping");
            int connectionTimeout = resultSet.getInt("connectionTimeout");
            int readTimeout = resultSet.getInt("readTimeout");
            String contentType = resultSet.getString("content_type");
            String tlsVersion = resultSet.getString("tlsVersion");
            String enableSSL = resultSet.getString("enableSsl");
            String validationUrl = resultSet.getString("validation_url");
            String verificationResponseMappingString = resultSet.getString("verification_response_mapping");
            String confirmationResponseMappingString = resultSet.getString("confirmation_response_mapping");
            String verificationResponseStatusMappingString = resultSet.getString("verification_response_status_mapping");
            String confirmationResponseStatusMappingString = resultSet.getString("confirmation_response_status_mapping");

            if (enableSSL.equals("Y")) {
                enableSslFlag = true;
            } else if (enableSSL.equals("N")) {
                enableSslFlag = false;
            }


            Map<String, String> parameterMappingMap;
            Type parameterMappingType = new TypeToken<Map<String, String>>() {
            }.getType();
            parameterMappingMap = gson.fromJson(parameterMappingString,
                                                parameterMappingType);

            Map<String, String> verificationResponseMappingMap;
            Type responseMappingType = new TypeToken<Map<String, String>>() {
            }.getType();
            verificationResponseMappingMap = gson.fromJson(verificationResponseMappingString,
                                                           responseMappingType);

            Map<String, String> confirmationResponseMappingMap;
            Type confirmationResponseMappingType = new TypeToken<Map<String, String>>() {
            }.getType();
            confirmationResponseMappingMap = gson.fromJson(confirmationResponseMappingString,
                                                           confirmationResponseMappingType);

            ResponseStatusMapping verificationResponseStatusMapping = gson.fromJson(verificationResponseStatusMappingString,
                                                                                    ResponseStatusMapping.class);
            ResponseStatusMapping confirmationResponseStatusMapping = gson.fromJson(confirmationResponseStatusMappingString,
                                                                                    ResponseStatusMapping.class);


            biller = new Biller(billerCode,
                                billerName,
                                endpointUrl,
                                requestMethod,
                                parameterMappingMap,
                                connectionTimeout,
                                readTimeout,
                                contentType,
                                tlsVersion,
                                enableSslFlag,
                                validationUrl,
                                verificationResponseMappingMap,
                                confirmationResponseMappingMap,
                                verificationResponseStatusMapping,
                                confirmationResponseStatusMapping);

        }
        return biller;
    }

}
