package com.billercode.api.billercodeapi.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionService {

    private static final String dbURL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String username = "sanzid";
    private static final String password = "QWERTYuiop1";

    public static Connection getDBConnection() {

        Connection conn;
        try {
            conn = DriverManager.getConnection(dbURL, username, password);
        } catch (
                SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

}
