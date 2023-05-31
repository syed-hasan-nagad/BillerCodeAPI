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
        String sql = "INSERT INTO SANZID.BILLERVALIDATIONS VALUES (?,?,?,?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,row.sessionId());
            statement.setString(2, row.billerCode());
            statement.setString(3, row.validationStatus());
            statement.setString(4, row.hash());
            statement.setString(5, row.confirmationStatus());
            int i =  statement.executeUpdate(); // returns number of records saved/changed as an integer
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        connection.close();

    }
    public void setConfirmationStatusToComplete(BillerValidation row) throws SQLException {

        Connection connection =  DatabaseConnectionService.getDBConnection();
        String sql = "UPDATE SANZID.BILLERVALIDATIONS SET confirmationstatus = 'completed' WHERE SESSIONID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,row.sessionId());
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
            String validationStatus = resultSet.getString("validationstatus");
            String confirmationStatus =  resultSet.getString("confirmationstatus");
            String hash =  resultSet.getString("hash");
            billerValidation = new BillerValidation(billerCode,sessionID,validationStatus,confirmationStatus,hash);
        }
        connection.close();
        if (billerValidation == null){
            return Optional.empty();
        }
        return Optional.of(billerValidation);

    }
}
