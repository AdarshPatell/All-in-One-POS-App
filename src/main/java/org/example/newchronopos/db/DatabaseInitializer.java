package org.example.newchronopos.db;

import org.example.newchronopos.config.DatabaseConfig;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // ----- Owner table -----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS owner (
                    id IDENTITY PRIMARY KEY,
                    name VARCHAR(100),
                    username VARCHAR(50) UNIQUE,
                    email VARCHAR(50) UNIQUE,
                    password VARCHAR(100),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ----- Users table -----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id IDENTITY PRIMARY KEY,
                    full_name VARCHAR(100),
                    username VARCHAR(50) UNIQUE,
                    email VARCHAR(50) UNIQUE,
                    password VARCHAR(100),
                    role VARCHAR(50),
                    phone_no VARCHAR(20),
                    salary DOUBLE,
                    dob DATE,
                    change_access BOOLEAN,
                    shift_start_time TIME,
                    shift_end_time TIME,
                    address VARCHAR(255),
                    additional_details VARCHAR(255),
                    uae_id VARCHAR(50),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ----- Product table -----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS product (
                    id IDENTITY PRIMARY KEY,
                    name VARCHAR(100),
                    price DECIMAL(10, 2)
                )
            """);

            // ----- Insert default owner -----
            String ownerHash = BCrypt.hashpw("owner123", BCrypt.gensalt());
            stmt.execute(
                    "MERGE INTO owner (id, name, username, password) KEY(id) VALUES " +
                            "(1, 'Super Owner', 'owner', '" + ownerHash + "')"
            );

// ----- Insert default admin user -----
            String adminHash = BCrypt.hashpw("admin123", BCrypt.gensalt());
            stmt.execute(
                    "MERGE INTO users (id, full_name, username, email, password, role, phone_no, salary, " +
                            "dob, change_access, shift_start_time, shift_end_time, address, additional_details, uae_id) " +
                            "KEY(id) VALUES (" +
                            "1, 'Default Admin', 'admin', 'admin@example.com', '" + adminHash + "', " +
                            "'admin', '1234567890', 60000.0, DATE '1990-01-01', TRUE, " +
                            "TIME '09:00:00', TIME '18:00:00', 'Admin Lane', 'Handles system', 'UAE9999')"
            );

// ----- Insert default employee user -----
            String empHash = BCrypt.hashpw("emp123", BCrypt.gensalt());
            stmt.execute(
                    "MERGE INTO users (id, full_name, username, email, password, role, phone_no, salary, " +
                            "dob, change_access, shift_start_time, shift_end_time, address, additional_details, uae_id) " +
                            "KEY(id) VALUES (" +
                            "2, 'John Employee', 'emp', 'emp@example.com', '" + empHash + "', " +
                            "'employee', '9876543210', 30000.0, DATE '1995-05-10', FALSE, " +
                            "TIME '10:00:00', TIME '19:00:00', 'Employee St', 'POS operator', 'UAE5678')"
            );
            System.out.println("✅ Database initialized with default accounts.");

        } catch (Exception e) {
            System.err.println("❌ Database initialization failed:");
            e.printStackTrace();
        }
    }
}
