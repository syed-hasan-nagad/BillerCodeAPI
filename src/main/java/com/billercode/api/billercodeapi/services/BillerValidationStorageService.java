package com.billercode.api.billercodeapi.services;

import com.billercode.api.billercodeapi.models.BillerValidation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class BillerValidationStorageService {


    public void saveBillerValidation(BillerValidation row) throws SQLException {

        Connection connection =  DatabaseConnectionService.getDBConnection();
        String sql = "INSERT INTO SANZID.BILLERVALIDATIONS VALUES (?,?,?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,row.getSessionId());
            statement.setString(2, row.getBillerCode());
            statement.setString(3, row.getStatus());
            statement.setString(4, row.getHash());
            int i =  statement.executeUpdate(); // returns number of records saved/changed as an integer
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        connection.close();

    }
    public void setBillerValidationToComplete(BillerValidation row) throws SQLException {

        Connection connection =  DatabaseConnectionService.getDBConnection();
        String sql = "UPDATE SANZID.BILLERVALIDATIONS SET status = 'completed' WHERE SESSIONID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,row.getSessionId());
            int i =  statement.executeUpdate(); // returns number of records saved/changed as an integer
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        connection.close();

    }

    public Optional<BillerValidation> getBillerValidationBySessionID(String sessionID) throws SQLException {

        Connection connection =  DatabaseConnectionService.getDBConnection();
        String sql = "SELECT * FROM SANZID.BILLERVALIDATIONS WHERE SESSIONID=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, sessionID);
        ResultSet resultSet = statement.executeQuery();
        BillerValidation billerValidation = null;
        while (resultSet.next()){
            String billerCode = resultSet.getString("billercode");
            String sessionId = resultSet.getString("sessionId");
            String status = resultSet.getString("status");
            String hash =  resultSet.getString("hash");
            billerValidation = new BillerValidation(billerCode,sessionID,status,hash);
        }
        connection.close();
        if (billerValidation == null){
            return Optional.empty();
        }
        return Optional.of(billerValidation);

    }
}
