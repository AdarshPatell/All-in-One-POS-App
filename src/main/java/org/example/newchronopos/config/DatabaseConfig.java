package org.example.newchronopos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String DB_URL = "jdbc:h2:./data/posdb";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, "sa", "");
    }
}