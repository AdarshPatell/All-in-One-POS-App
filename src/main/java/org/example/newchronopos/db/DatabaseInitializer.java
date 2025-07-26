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

            // ----- Product master table -----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS product (
                    id IDENTITY PRIMARY KEY,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ----- Product info table -----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS product_info (
                    id IDENTITY PRIMARY KEY,
                    product_id INT NOT NULL,
                    product_name VARCHAR(100) NOT NULL,
                    sku VARCHAR(100) UNIQUE,
                    category_id INT,
                    brand VARCHAR(100),
                    purchase_unit VARCHAR(50),
                    selling_unit VARCHAR(50),
                    supplier VARCHAR(100),
                    product_group VARCHAR(100),
                    reorder_level INT,
                    description TEXT,
                    FOREIGN KEY (product_id) REFERENCES product(id),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ----- Product barcodes table -----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS product_barcodes (
                    id IDENTITY PRIMARY KEY,
                    product_id INT NOT NULL,
                    name VARCHAR(100),
                    barcode VARCHAR(100) UNIQUE,
                    is_default BOOLEAN DEFAULT false,
                    is_standard BOOLEAN DEFAULT false,
                    FOREIGN KEY (product_id) REFERENCES product(id),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ----- Product attributes table -----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS product_attributes (
                    id IDENTITY PRIMARY KEY,
                    product_id INT NOT NULL,
                    attribute_name VARCHAR(100),
                    attribute_value VARCHAR(100),  // Column is named "attribute_value"
                    arabic_value VARCHAR(100),
                    FOREIGN KEY (product_id) REFERENCES product(id),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ----- Unit prices table -----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS unit_prices (
                    id IDENTITY PRIMARY KEY,
                    product_id INT NOT NULL,
                    price_type VARCHAR(50),
                    unit_option VARCHAR(50),
                    cost DECIMAL(10,2),
                    price DECIMAL(10,2),
                    color VARCHAR(50),
                    FOREIGN KEY (product_id) REFERENCES product(id),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ----- Product taxes table -----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS product_taxes (
                    id IDENTITY PRIMARY KEY,
                    product_id INT NOT NULL,
                    tax_type VARCHAR(100),
                    applied_to_selling BOOLEAN DEFAULT false,
                    applied_to_buying BOOLEAN DEFAULT false,
                    include_in_price BOOLEAN DEFAULT false,
                    FOREIGN KEY (product_id) REFERENCES product(id),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ----- Product images table -----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS product_images (
                    id IDENTITY PRIMARY KEY,
                    product_id INT NOT NULL,
                    image_url TEXT,
                    FOREIGN KEY (product_id) REFERENCES product(id),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ----- Category table -----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS category (
                    id IDENTITY PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    description VARCHAR(255),
                    parent_id INT,
                    image_url VARCHAR(255),
                    is_active BOOLEAN DEFAULT true,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Insert default categories
            stmt.execute("""
                MERGE INTO category ( name, description) KEY(name) VALUES 
                ( 'Pizza', 'Various pizza items'),
                ( 'Burger', 'Different types of burgers'),
                ( 'Chicken', 'Chicken based products'),
                ( 'Bakery', 'Bakery items'),
                ( 'Beverage', 'Drinks and beverages'),
                ( 'Seafood', 'Seafood products')
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

            // ----- Reset auto-increment sequence for users table -----
            // This ensures that new users will get IDs starting from 3
            stmt.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 3");

            System.out.println("✅ Database initialized with default accounts and product tables.");

        } catch (Exception e) {
            System.err.println("❌ Database initialization failed:");
            e.printStackTrace();
        }
    }
}