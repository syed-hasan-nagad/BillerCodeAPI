package com.billercode.api.billercodeapi.services;

import java.sql.*;
import java.util.HashMap;

public class BillerService {

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
}
