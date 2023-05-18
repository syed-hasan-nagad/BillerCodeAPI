package com.billercode.api.billercodeapi.services;

import com.billercode.api.billercodeapi.models.Biller;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BillerService {

    Gson gson = new Gson();

    public HashMap<String, String> getBillersfromDB() throws SQLException {
        Connection connection =  DatabaseConnectionService.getDBConnection();
        String sql = "SELECT * FROM SANZID.BILLERS";

        PreparedStatement statement = connection.prepareStatement(sql);

        ResultSet resultSet = statement.executeQuery();
        HashMap<String, String> billers = new HashMap<>();
        while (resultSet.next()){
            String endpointUrl = resultSet.getString("endpoint_url");
            String billerCode = resultSet.getString("biller_code");
            billers.put(billerCode,endpointUrl);
        }
        return billers;
    }

    public Biller getBillerByBillerCodefromDB(String billerCode) throws SQLException {
        Connection connection =  DatabaseConnectionService.getDBConnection();
        Biller biller = null;

        String sql = "SELECT * FROM SANZID.BILLERS WHERE biller_code = ?";


        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1,billerCode);

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()){
            String endpointUrl = resultSet.getString("endpoint_url");
                   billerCode = resultSet.getString("biller_code");
            String billerName = resultSet.getString("biller_name");
            String requestMethod = resultSet.getString("request_method");
            String parameterMappingString = resultSet.getString("parameter_mapping");
            String connectionSettingsString = resultSet.getString("connection_settings");

            Map<String,String> parameterMappingMap,connectionSettingsMap;
            Type parameterMappingType = new TypeToken<Map<String, String>>() {}.getType();
            parameterMappingMap = gson.fromJson(parameterMappingString, parameterMappingType);
            connectionSettingsMap = gson.fromJson(connectionSettingsString, parameterMappingType);

            biller = new Biller(billerCode,billerName,endpointUrl,requestMethod,parameterMappingMap,connectionSettingsMap);

        }
        return biller;
    }

}
