package org.example.newchronopos.db;
import org.example.newchronopos.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create product table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS product (
                    id IDENTITY PRIMARY KEY,
                    name VARCHAR(100),
                    price DECIMAL(10, 2)
                )
            """);

            // Create other tables here...
            System.out.println("Database initialized.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

