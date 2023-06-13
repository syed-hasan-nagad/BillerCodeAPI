package com.billercode.api.billercodeapi.services;

import com.billercode.api.billercodeapi.models.BillerValidation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class BillerValidationStorageService {


    public void saveBillerValidation(BillerValidation row) throws SQLException {

        Connection connection = DatabaseConnectionService.getDBConnection();
        String sql = "INSERT INTO SANZID.BILLERVALIDATIONS VALUES (?,?,?,?,?,?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,
                                row.sessionId());
            statement.setString(2,
                                row.billerCode());
            statement.setString(3,
                                row.validationStatus());
            statement.setString(4,
                                row.hash());
            statement.setString(5,
                                row.confirmationStatus());
            statement.setString(6,
                                row.verificationResponse());
            statement.setString(7,
                                row.confirmationResponse());
            int i = statement.executeUpdate(); // returns number of records saved/changed as an integer
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        connection.close();

    }

    public void setConfirmationStatusToComplete(BillerValidation row) throws SQLException {

        Connection connection = DatabaseConnectionService.getDBConnection();
        String sql = "UPDATE SANZID.BILLERVALIDATIONS SET confirmationstatus = 'completed' WHERE SESSIONID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,
                                row.sessionId());
            int i = statement.executeUpdate(); // returns number of records saved/changed as an integer
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        connection.close();

    }

    public void setConfirmationResponse(BillerValidation row, String confirmationResponse) throws SQLException {

        Connection connection = DatabaseConnectionService.getDBConnection();
        String sql = "UPDATE SANZID.BILLERVALIDATIONS SET confirmation_response = ? WHERE SESSIONID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,
                                confirmationResponse);
            statement.setString(2,
                                row.sessionId());
            int i = statement.executeUpdate(); // returns number of records saved/changed as an integer
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        connection.close();

    }

    public void setConfirmationStatusToFailed(BillerValidation row) throws SQLException {

        Connection connection = DatabaseConnectionService.getDBConnection();
        String sql = "UPDATE SANZID.BILLERVALIDATIONS SET confirmationstatus = 'failed' WHERE SESSIONID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,
                                row.sessionId());
            int i = statement.executeUpdate(); // returns number of records saved/changed as an integer
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        connection.close();

    }

    public Optional<BillerValidation> getBillerValidationBySessionID(String sessionID) throws SQLException {

        Connection connection = DatabaseConnectionService.getDBConnection();
        String sql = "SELECT * FROM SANZID.BILLERVALIDATIONS WHERE SESSIONID=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1,
                            sessionID);
        ResultSet resultSet = statement.executeQuery();
        BillerValidation billerValidation = null;
        while (resultSet.next()) {
            String billerCode = resultSet.getString("billercode");
            String sessionId = resultSet.getString("sessionId");
            String validationStatus = resultSet.getString("validationstatus");
            String confirmationStatus = resultSet.getString("confirmationstatus");
            String hash = resultSet.getString("hash");
            String verificationResponse = resultSet.getString("verification_response");
            String confirmationResponse = resultSet.getString("confirmation_response");
            billerValidation = new BillerValidation(billerCode,
                                                    sessionID,
                                                    validationStatus,
                                                    confirmationStatus,
                                                    hash,
                                                    verificationResponse,
                                                    confirmationResponse);
        }
        connection.close();
        if (billerValidation == null) {
            return Optional.empty();
        }
        return Optional.of(billerValidation);

    }
}
