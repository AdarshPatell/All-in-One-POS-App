package org.example.newchronopos.db;

import org.example.newchronopos.config.DatabaseConfig;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Starting database initialization...");

            // Create all tables based on your comprehensive schema
            createSystemTables(stmt);
            createUserManagementTables(stmt);
            createProductTables(stmt);
            createStockManagementTables(stmt);
            createSupplierTables(stmt);
            createCustomerTables(stmt);
            createTransactionTables(stmt);
            createShopTables(stmt);

            // Insert initial data
            insertInitialData(stmt);

            // Verify critical tables exist
            verifyCriticalTables(conn);

            System.out.println("Database initialized successfully with all stock management tables!");
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Database initialization failed!");
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private static void verifyCriticalTables(Connection conn) throws Exception {
        String[] criticalTables = {"category", "product", "supplier", "users"};

        for (String table : criticalTables) {
            try (var stmt = conn.prepareStatement("SELECT COUNT(*) FROM " + table)) {
                stmt.executeQuery();
                System.out.println("✓ Table '" + table + "' verified successfully");
            } catch (Exception e) {
                throw new Exception("Critical table '" + table + "' is missing or inaccessible", e);
            }
        }
    }

    private static void createSystemTables(Statement stmt) throws Exception {
        // Countries table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS countries (
                country_id IDENTITY PRIMARY KEY,
                country_name VARCHAR(100) NOT NULL,
                isd VARCHAR(10),
                currency_code VARCHAR(3),
                currency_symbol VARCHAR(10),
                flag_icon VARCHAR(255),
                country_code4 VARCHAR(4),
                status BOOLEAN DEFAULT TRUE,
                created_by BIGINT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_by BIGINT,
                updated_at TIMESTAMP,
                deleted_by BIGINT,
                deleted_at TIMESTAMP
            )
        """);

        // States table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS states (
                state_id IDENTITY PRIMARY KEY,
                country_id BIGINT NOT NULL,
                state_name VARCHAR(100) NOT NULL,
                state_code VARCHAR(10),
                status BOOLEAN DEFAULT TRUE,
                created_by BIGINT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_by BIGINT,
                updated_at TIMESTAMP,
                deleted_by BIGINT,
                deleted_at TIMESTAMP
            )
        """);

        // Cities table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS cities (
                city_id IDENTITY PRIMARY KEY,
                state_id BIGINT NOT NULL,
                city_name VARCHAR(100) NOT NULL,
                postal_code VARCHAR(20),
                status BOOLEAN DEFAULT TRUE,
                created_by BIGINT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_by BIGINT,
                updated_at TIMESTAMP,
                deleted_by BIGINT,
                deleted_at TIMESTAMP
            )
        """);

        // Currencies table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS currencies (
                id IDENTITY PRIMARY KEY,
                currency_code VARCHAR(3) UNIQUE NOT NULL,
                currency_name VARCHAR(100) NOT NULL,
                symbol VARCHAR(10) NOT NULL,
                decimal_places INT NOT NULL DEFAULT 2,
                country VARCHAR(100) NOT NULL,
                status BOOLEAN NOT NULL DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP,
                deleted_at TIMESTAMP
            )
        """);

        // Languages table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS language (
                id IDENTITY PRIMARY KEY,
                language_name VARCHAR(255),
                language_code VARCHAR(255),
                is_rtl BOOLEAN,
                status VARCHAR(255),
                created_by VARCHAR(255),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_by VARCHAR(255),
                updated_at TIMESTAMP,
                deleted_at TIMESTAMP,
                deleted_by VARCHAR(255)
            )
        """);
    }

    private static void createUserManagementTables(Statement stmt) throws Exception {
        // Owner table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS owner (
                id IDENTITY PRIMARY KEY,
                name VARCHAR(100),
                email VARCHAR(100) UNIQUE,
                password VARCHAR(255),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Roles table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS roles (
                role_id IDENTITY PRIMARY KEY,
                role_name VARCHAR(100) NOT NULL,
                description TEXT,
                status VARCHAR(20) DEFAULT 'Active',
                created_by INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_by INT,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                deleted_at TIMESTAMP,
                deleted_by INT
            )
        """);

        // Users table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id IDENTITY PRIMARY KEY,
                deleted BOOLEAN DEFAULT FALSE,
                owner_id BIGINT,
                full_name VARCHAR(100),
                email VARCHAR(100) UNIQUE,
                password VARCHAR(255),
                role VARCHAR(50),
                phone_no VARCHAR(20),
                salary DECIMAL(10,2),
                dob DATE,
                nationality_status VARCHAR(20) DEFAULT 'active',
                role_permission_id INT NOT NULL DEFAULT 1,
                shopid INT NOT NULL DEFAULT 1,
                change_access BOOLEAN DEFAULT FALSE,
                shift_type_id BIGINT,
                address TEXT,
                additional_details TEXT,
                uae_id VARCHAR(50) UNIQUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);
    }

    private static void createProductTables(Statement stmt) throws Exception {
        // Category table
        System.out.println("Creating category table...");
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS category (
                id IDENTITY PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                parent_id INT,
                description TEXT,
                image_url TEXT,
                display_order INT DEFAULT 0,
                status VARCHAR(20) DEFAULT 'Active',
                created_by INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);
        System.out.println("✓ Category table created successfully");

        // Brand table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS brand (
                id IDENTITY PRIMARY KEY,
                deleted INT DEFAULT 0,
                name VARCHAR(100) UNIQUE NOT NULL,
                name_arabic VARCHAR(100),
                description TEXT,
                logo_url TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Product table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS product (
                id IDENTITY PRIMARY KEY,
                deleted INT NOT NULL DEFAULT 0,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                status VARCHAR(20) DEFAULT 'Active',
                type VARCHAR(20) DEFAULT 'Physical'
            )
        """);

        // Product info table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS product_info (
                id IDENTITY PRIMARY KEY,
                product_id INT UNIQUE NOT NULL,
                product_name VARCHAR(100) NOT NULL,
                product_name_ar VARCHAR(100),
                alternate_name VARCHAR(100),
                alternate_name_ar VARCHAR(100),
                full_description TEXT,
                full_description_ar TEXT,
                short_description TEXT,
                short_description_ar TEXT,
                sku VARCHAR(100) UNIQUE NOT NULL,
                model_number VARCHAR(100),
                created_barcode BOOLEAN NOT NULL DEFAULT FALSE,
                has_standard_barcode BOOLEAN NOT NULL DEFAULT TRUE,
                category_id INT NOT NULL DEFAULT 1,
                sub_category_lvl1_id INT,
                sub_category_lvl2_id INT,
                brand_id INT,
                product_unit VARCHAR(50) NOT NULL DEFAULT 'pcs',
                weight DECIMAL(10,2),
                dimensions VARCHAR(100),
                specs_flag BOOLEAN NOT NULL DEFAULT TRUE,
                specs TEXT,
                color VARCHAR(50),
                reorder_level INT NOT NULL DEFAULT 0,
                store_location VARCHAR(200),
                can_return BOOLEAN NOT NULL DEFAULT FALSE,
                country_of_origin VARCHAR(100),
                supplier_id INT,
                shop_location_id INT,
                stock_unit_id INT,
                purchase_unit_id INT,
                selling_unit_id INT,
                with_expiry_date BOOLEAN DEFAULT FALSE,
                expiry_days INT,
                has_warranty BOOLEAN DEFAULT FALSE,
                warranty_period INT,
                warranty_type_id INT,
                price_type VARCHAR(20) DEFAULT 'Fixed',
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // UOM table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS uom (
                id IDENTITY PRIMARY KEY,
                name VARCHAR(50),
                abbreviation VARCHAR(10),
                base_uom_id BIGINT,
                conversion_factor DECIMAL(10,4),
                is_active BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);
    }

    private static void createStockManagementTables(Statement stmt) throws Exception {
        // Stock adjustment reasons
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS stock_adjustment_reasons (
                stock_adjustment_reasons_id IDENTITY PRIMARY KEY,
                name VARCHAR(255),
                description VARCHAR(255),
                status VARCHAR(255) DEFAULT 'Active',
                created_by INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_by INT,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                deleted_at TIMESTAMP
            )
        """);

        // Stock adjustment
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS stock_adjustment (
                adjustment_id IDENTITY PRIMARY KEY,
                adjustment_no VARCHAR(30) UNIQUE NOT NULL,
                adjustment_date DATE NOT NULL,
                store_location_id INT NOT NULL,
                reason_id INT NOT NULL,
                status VARCHAR(20) DEFAULT 'Pending',
                remarks TEXT,
                created_by INT NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Stock adjustment item
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS stock_adjustment_item (
                id IDENTITY PRIMARY KEY,
                adjustment_id INT NOT NULL,
                product_id INT NOT NULL,
                uom_id INT NOT NULL,
                batch_no VARCHAR(50),
                expiry_date DATE,
                quantity_before DECIMAL(10,3) NOT NULL,
                quantity_after DECIMAL(10,3) NOT NULL,
                difference_qty DECIMAL(10,3) NOT NULL,
                reason_line VARCHAR(100),
                remarks_line TEXT
            )
        """);

        // Stock transfer
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS stock_transfer (
                transfer_id IDENTITY PRIMARY KEY,
                transfer_no VARCHAR(30) UNIQUE NOT NULL,
                transfer_date DATE NOT NULL,
                from_store_id INT NOT NULL,
                to_store_id INT NOT NULL,
                status VARCHAR(20) DEFAULT 'Pending',
                remarks TEXT,
                created_by INT NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Stock transfer item
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS stock_transfer_item (
                id IDENTITY PRIMARY KEY,
                transfer_id INT NOT NULL,
                product_id INT NOT NULL,
                uom_id INT NOT NULL,
                batch_no VARCHAR(50),
                expiry_date DATE,
                quantity_sent DECIMAL(10,3) NOT NULL,
                quantity_received DECIMAL(10,3) DEFAULT 0,
                damaged_qty DECIMAL(10,3) DEFAULT 0,
                status VARCHAR(20) DEFAULT 'Pending',
                remarks_line TEXT
            )
        """);

        // Goods received (supplier purchases)
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS goods_received (
                received_id IDENTITY PRIMARY KEY,
                received_no VARCHAR(30) UNIQUE NOT NULL,
                received_date DATE NOT NULL,
                supplier_id INT NOT NULL,
                store_location_id INT NOT NULL,
                status VARCHAR(20) DEFAULT 'Pending',
                remarks TEXT,
                total_amount DECIMAL(12,2) DEFAULT 0,
                created_by INT NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Goods received item
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS goods_received_item (
                id IDENTITY PRIMARY KEY,
                received_id INT NOT NULL,
                product_id INT NOT NULL,
                quantity DECIMAL(10,3) NOT NULL,
                unit_price DECIMAL(10,2) NOT NULL,
                total_price DECIMAL(12,2) NOT NULL,
                status VARCHAR(20) DEFAULT 'Active'
            )
        """);

        // Goods replaced
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS goods_replaced (
                replaced_id IDENTITY PRIMARY KEY,
                replaced_no VARCHAR(30) UNIQUE NOT NULL,
                replaced_date DATE NOT NULL,
                customer_id INT,
                store_location_id INT NOT NULL,
                status VARCHAR(20) DEFAULT 'Pending',
                remarks TEXT,
                reason VARCHAR(255),
                created_by INT NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Goods replaced item
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS goods_replaced_item (
                id IDENTITY PRIMARY KEY,
                replaced_id INT NOT NULL,
                product_id INT NOT NULL,
                quantity DECIMAL(10,3) NOT NULL,
                reason VARCHAR(255),
                status VARCHAR(20) DEFAULT 'Active'
            )
        """);

        // Goods return
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS goods_return (
                return_id IDENTITY PRIMARY KEY,
                return_no VARCHAR(30) UNIQUE NOT NULL,
                return_date DATE NOT NULL,
                customer_id INT,
                store_location_id INT NOT NULL,
                status VARCHAR(20) DEFAULT 'Pending',
                remarks TEXT,
                reason VARCHAR(255),
                total_amount DECIMAL(12,2) DEFAULT 0,
                created_by INT NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Goods return item
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS goods_return_item (
                id IDENTITY PRIMARY KEY,
                return_id INT NOT NULL,
                product_id INT NOT NULL,
                quantity DECIMAL(10,3) NOT NULL,
                refund_amount DECIMAL(10,2) NOT NULL,
                reason VARCHAR(255),
                status VARCHAR(20) DEFAULT 'Active'
            )
        """);

        // Stock movement tracking
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS stock_movement (
                id IDENTITY PRIMARY KEY,
                product_id INT NOT NULL,
                batch_id INT,
                uom_id INT NOT NULL,
                movement_type VARCHAR(20) NOT NULL,
                quantity DECIMAL(12,4) NOT NULL,
                reference_type VARCHAR(50),
                reference_id INT NOT NULL,
                location_id INT,
                notes TEXT,
                created_by INT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Stock ledger
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS stock_ledger (
                id IDENTITY PRIMARY KEY,
                product_id INT NOT NULL,
                unit_id INT NOT NULL,
                movement_type VARCHAR(20) NOT NULL,
                qty DECIMAL(10,2) NOT NULL,
                balance DECIMAL(10,2) NOT NULL,
                location VARCHAR(200),
                reference_type VARCHAR(50),
                reference_id INT,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);
    }

    private static void createSupplierTables(Statement stmt) throws Exception {
        // Supplier table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS supplier (
                supplier_id IDENTITY PRIMARY KEY,
                shop_id BIGINT,
                company_name VARCHAR(100) NOT NULL,
                logo_picture VARCHAR(255),
                license_number VARCHAR(50),
                owner_name VARCHAR(100),
                owner_mobile VARCHAR(20),
                vat_trn_number VARCHAR(50),
                email VARCHAR(100) UNIQUE,
                address_line1 VARCHAR(255) NOT NULL,
                address_line2 VARCHAR(255),
                building VARCHAR(100),
                area VARCHAR(100),
                po_box VARCHAR(20),
                city VARCHAR(100),
                state VARCHAR(100),
                country VARCHAR(100),
                website VARCHAR(100),
                key_contact_name VARCHAR(100),
                key_contact_mobile VARCHAR(20),
                key_contact_email VARCHAR(100),
                mobile VARCHAR(20),
                location_latitude DECIMAL(10,8),
                location_longitude DECIMAL(11,8),
                company_phone_number VARCHAR(20),
                gstin VARCHAR(20),
                pan VARCHAR(20),
                payment_terms VARCHAR(50),
                opening_balance DECIMAL(12,2) DEFAULT 0,
                balance_type VARCHAR(10) DEFAULT 'credit',
                status VARCHAR(20) DEFAULT 'Active',
                created_by BIGINT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_by BIGINT,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                deleted_at TIMESTAMP,
                deleted_by BIGINT
            )
        """);
    }

    private static void createCustomerTables(Statement stmt) throws Exception {
        // Business type master
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS business_type_master (
                id IDENTITY PRIMARY KEY,
                business_type_name VARCHAR(100),
                business_type_name_ar VARCHAR(100),
                status VARCHAR(20) DEFAULT 'Active',
                created_by INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_by INT,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                deleted_at TIMESTAMP,
                deleted_by INT
            )
        """);

        // Customer table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS customer (
                id IDENTITY PRIMARY KEY,
                customer_full_name VARCHAR(150),
                business_full_name VARCHAR(150),
                is_business BOOLEAN DEFAULT FALSE,
                business_type_id INT,
                customer_balance_amount DECIMAL(12,2) DEFAULT 0,
                license_no VARCHAR(50),
                trn_no VARCHAR(50),
                mobile_no VARCHAR(20),
                home_phone VARCHAR(20),
                office_phone VARCHAR(20),
                contact_mobile_no VARCHAR(20),
                credit_allowed BOOLEAN DEFAULT FALSE,
                credit_amount_max DECIMAL(12,2) DEFAULT 0,
                credit_days INT DEFAULT 0,
                credit_reference1_name VARCHAR(150),
                credit_reference2_name VARCHAR(150),
                key_contact_name VARCHAR(150),
                key_contact_mobile VARCHAR(20),
                key_contact_email VARCHAR(100),
                finance_person_name VARCHAR(150),
                finance_person_mobile VARCHAR(20),
                finance_person_email VARCHAR(100),
                official_email VARCHAR(100),
                post_dated_cheques_allowed BOOLEAN DEFAULT FALSE,
                shop_id INT,
                status VARCHAR(20) DEFAULT 'Active',
                created_by INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_by INT,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                deleted_at TIMESTAMP,
                deleted_by INT
            )
        """);
    }

    private static void createTransactionTables(Statement stmt) throws Exception {
        // Payment options
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS payment_options (
                id IDENTITY PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                payment_code VARCHAR(50) NOT NULL,
                name_ar VARCHAR(255),
                status BOOLEAN NOT NULL DEFAULT TRUE,
                created_by INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_by INT,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                deleted_by INT,
                deleted_at TIMESTAMP
            )
        """);

        // Service charges
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS service_charges (
                id IDENTITY PRIMARY KEY,
                name VARCHAR(100),
                description TEXT,
                charge_type VARCHAR(20) DEFAULT 'percentage',
                charge_value DECIMAL(10,2),
                charge_value_arabic DECIMAL(10,2),
                apply_to VARCHAR(20) DEFAULT 'invoice',
                is_taxable BOOLEAN DEFAULT FALSE,
                is_active BOOLEAN DEFAULT TRUE,
                created_by VARCHAR(255),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_by VARCHAR(255),
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                deleted_at TIMESTAMP,
                deleted_by VARCHAR(255)
            )
        """);
    }

    private static void createShopTables(Statement stmt) throws Exception {
        // Companies table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS companies (
                company_id IDENTITY PRIMARY KEY,
                company_name VARCHAR(100) NOT NULL,
                logo_picture VARCHAR(255),
                license_number VARCHAR(50) UNIQUE,
                vat_trn_number VARCHAR(50),
                phone_number VARCHAR(20),
                email VARCHAR(100) UNIQUE,
                website VARCHAR(100),
                key_contact_name VARCHAR(100),
                key_contact_mobile VARCHAR(20),
                key_contact_email VARCHAR(100),
                location_latitude DECIMAL(10,8),
                location_longitude DECIMAL(11,8),
                remarks TEXT,
                status VARCHAR(20) DEFAULT 'active',
                created_by INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_by INT,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                deleted_at TIMESTAMP,
                deleted_by INT
            )
        """);

        // Shop table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS shop (
                shop_id IDENTITY PRIMARY KEY,
                company_id INT NOT NULL DEFAULT 1,
                industry_type_id INT,
                name VARCHAR(100) NOT NULL,
                pos_id VARCHAR(50),
                pos_name VARCHAR(100),
                number_of_locations_allowed INT DEFAULT 1,
                status VARCHAR(20) DEFAULT 'Active',
                created_by INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_by INT,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                deleted_at TIMESTAMP,
                deleted_by INT
            )
        """);

        // Shop locations table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS shop_locations (
                id IDENTITY PRIMARY KEY,
                shop_id INT NOT NULL,
                location_type VARCHAR(50) NOT NULL DEFAULT 'Retail',
                location_name VARCHAR(100) NOT NULL,
                manager_id INT,
                address_line1 VARCHAR(255) NOT NULL,
                address_line2 VARCHAR(255),
                building VARCHAR(100),
                area VARCHAR(100),
                po_box VARCHAR(20),
                city VARCHAR(100),
                state_id INT,
                country_id INT,
                landline_number VARCHAR(20),
                mobile_number VARCHAR(20),
                location_latitude DECIMAL(10,8),
                location_longitude DECIMAL(11,8),
                can_sell BOOLEAN DEFAULT TRUE,
                language_id INT,
                status VARCHAR(20) DEFAULT 'Active',
                created_by INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_by INT,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                deleted_at TIMESTAMP,
                deleted_by INT
            )
        """);
    }

    private static void insertInitialData(Statement stmt) throws Exception {
        // Insert default owner (only if not exists)
        String hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());
        stmt.execute(String.format("""
            INSERT INTO owner (name, email, password) 
            SELECT 'ChronoPOS Admin', 'admin@chronopos.com', '%s'
            WHERE NOT EXISTS (SELECT 1 FROM owner WHERE email = 'admin@chronopos.com')
            """, hashedPassword));

        // Insert default company (only if not exists)
        stmt.execute("""
            INSERT INTO companies (company_name, email, license_number, phone_number, status) 
            SELECT 'ChronoPOS Company', 'info@chronopos.com', 'LIC001', '+971-555-0123', 'active'
            WHERE NOT EXISTS (SELECT 1 FROM companies WHERE email = 'info@chronopos.com')
            """);

        // Insert default shop (only if not exists)
        stmt.execute("""
            INSERT INTO shop (company_id, name, pos_name, status) 
            SELECT 1, 'Main Store', 'ChronoPOS Main', 'Active'
            WHERE NOT EXISTS (SELECT 1 FROM shop WHERE name = 'Main Store')
            """);

        // Insert default shop location
        stmt.execute("""
            INSERT INTO shop_locations (shop_id, location_name, address_line1, city, status) 
            SELECT 1, 'Main Store Location', '123 Business Street', 'Dubai', 'Active'
            WHERE NOT EXISTS (SELECT 1 FROM shop_locations WHERE location_name = 'Main Store Location')
            """);

        // Insert default role
        stmt.execute("""
            INSERT INTO roles (role_name, description, status) 
            SELECT 'Administrator', 'Full system access', 'Active'
            WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'Administrator')
            """);

        // Insert default admin user
        String adminPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());
        stmt.execute(String.format("""
            INSERT INTO users (full_name, email, password, role, phone_no)
            SELECT 'System Administrator', 'admin@chronopos.com', '%s', 'Administrator', '+971-555-0001'
            WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@chronopos.com')
            """, adminPassword));

        // Insert default category
        stmt.execute("""
            INSERT INTO category (name, description, status, created_by) 
            SELECT 'Food & Beverages', 'Food and beverage items', 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Food & Beverages')
            """);

        stmt.execute("""
            INSERT INTO category (name, description, status, created_by) 
            SELECT 'Electronics', 'Electronic items', 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Electronics')
            """);

        stmt.execute("""
            INSERT INTO category (name, description, status, created_by) 
            SELECT 'General', 'General items', 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'General')
            """);

        // Insert default brand
        stmt.execute("""
            INSERT INTO brand (name, name_arabic, description) 
            SELECT 'ChronoPOS Brand', 'علامة كرونو بوس التجارية', 'Default brand for products'
            WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name = 'ChronoPOS Brand')
            """);

        // Insert default UOMs
        stmt.execute("""
            INSERT INTO uom (name, abbreviation, is_active) 
            SELECT 'Pieces', 'pcs', TRUE
            WHERE NOT EXISTS (SELECT 1 FROM uom WHERE name = 'Pieces')
            """);
        
        stmt.execute("""
            INSERT INTO uom (name, abbreviation, is_active) 
            SELECT 'Kilograms', 'kg', TRUE
            WHERE NOT EXISTS (SELECT 1 FROM uom WHERE name = 'Kilograms')
            """);
        
        stmt.execute("""
            INSERT INTO uom (name, abbreviation, is_active) 
            SELECT 'Liters', 'L', TRUE
            WHERE NOT EXISTS (SELECT 1 FROM uom WHERE name = 'Liters')
            """);
        
        stmt.execute("""
            INSERT INTO uom (name, abbreviation, is_active) 
            SELECT 'Meters', 'm', TRUE
            WHERE NOT EXISTS (SELECT 1 FROM uom WHERE name = 'Meters')
            """);
        
        stmt.execute("""
            INSERT INTO uom (name, abbreviation, is_active) 
            SELECT 'Boxes', 'box', TRUE
            WHERE NOT EXISTS (SELECT 1 FROM uom WHERE name = 'Boxes')
            """);

        // Insert stock adjustment reasons
        stmt.execute("""
            INSERT INTO stock_adjustment_reasons (name, description, status, created_by) 
            SELECT 'Customer Demand', 'Adjustment due to customer demand changes', 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM stock_adjustment_reasons WHERE name = 'Customer Demand')
            """);

        stmt.execute("""
            INSERT INTO stock_adjustment_reasons (name, description, status, created_by) 
            SELECT 'Damaged Goods', 'Items damaged during handling or storage', 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM stock_adjustment_reasons WHERE name = 'Damaged Goods')
            """);

        stmt.execute("""
            INSERT INTO stock_adjustment_reasons (name, description, status, created_by) 
            SELECT 'Expired Items', 'Items that have passed expiry date', 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM stock_adjustment_reasons WHERE name = 'Expired Items')
            """);

        stmt.execute("""
            INSERT INTO stock_adjustment_reasons (name, description, status, created_by) 
            SELECT 'Theft', 'Items lost due to theft', 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM stock_adjustment_reasons WHERE name = 'Theft')
            """);

        stmt.execute("""
            INSERT INTO stock_adjustment_reasons (name, description, status, created_by) 
            SELECT 'Inventory Correction', 'Physical count corrections', 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM stock_adjustment_reasons WHERE name = 'Inventory Correction')
            """);

        stmt.execute("""
            INSERT INTO stock_adjustment_reasons (name, description, status, created_by) 
            SELECT 'Supplier Return', 'Items returned to supplier', 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM stock_adjustment_reasons WHERE name = 'Supplier Return')
            """);

        // Insert default supplier
        stmt.execute("""
            INSERT INTO supplier (shop_id, company_name, email, address_line1, city, country, status, created_by) 
            SELECT 1, 'Default Supplier Co.', 'supplier@example.com', '456 Supplier Street', 'Dubai', 'UAE', 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM supplier WHERE company_name = 'Default Supplier Co.')
            """);

        // Insert business types
        stmt.execute("""
            INSERT INTO business_type_master (business_type_name, business_type_name_ar, status, created_by) 
            SELECT 'Restaurant', 'مطعم', 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM business_type_master WHERE business_type_name = 'Restaurant')
            """);
        
        stmt.execute("""
            INSERT INTO business_type_master (business_type_name, business_type_name_ar, status, created_by) 
            SELECT 'Retail', 'بيع بالتجزئة', 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM business_type_master WHERE business_type_name = 'Retail')
            """);
        
        stmt.execute("""
            INSERT INTO business_type_master (business_type_name, business_type_name_ar, status, created_by) 
            SELECT 'Wholesale', 'بيع بالجملة', 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM business_type_master WHERE business_type_name = 'Wholesale')
            """);
        
        stmt.execute("""
            INSERT INTO business_type_master (business_type_name, business_type_name_ar, status, created_by) 
            SELECT 'Corporate', 'شركة', 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM business_type_master WHERE business_type_name = 'Corporate')
            """);

        // Insert default customer
        stmt.execute("""
            INSERT INTO customer (customer_full_name, mobile_no, official_email, business_type_id, shop_id, status, created_by) 
            SELECT 'Walk-in Customer', '+971-555-0000', 'walkin@customer.com', 1, 1, 'Active', 1
            WHERE NOT EXISTS (SELECT 1 FROM customer WHERE customer_full_name = 'Walk-in Customer')
            """);

        // Insert payment options
        stmt.execute("""
            INSERT INTO payment_options (name, payment_code, name_ar, status, created_by) 
            SELECT 'Cash', 'CASH', 'نقدا', TRUE, 1
            WHERE NOT EXISTS (SELECT 1 FROM payment_options WHERE payment_code = 'CASH')
            """);
        
        stmt.execute("""
            INSERT INTO payment_options (name, payment_code, name_ar, status, created_by) 
            SELECT 'Credit Card', 'CREDIT', 'بطاقة ائتمان', TRUE, 1
            WHERE NOT EXISTS (SELECT 1 FROM payment_options WHERE payment_code = 'CREDIT')
            """);
        
        stmt.execute("""
            INSERT INTO payment_options (name, payment_code, name_ar, status, created_by) 
            SELECT 'Debit Card', 'DEBIT', 'بطاقة خصم', TRUE, 1
            WHERE NOT EXISTS (SELECT 1 FROM payment_options WHERE payment_code = 'DEBIT')
            """);
        
        stmt.execute("""
            INSERT INTO payment_options (name, payment_code, name_ar, status, created_by) 
            SELECT 'Bank Transfer', 'TRANSFER', 'تحويل بنكي', TRUE, 1
            WHERE NOT EXISTS (SELECT 1 FROM payment_options WHERE payment_code = 'TRANSFER')
            """);
        
        stmt.execute("""
            INSERT INTO payment_options (name, payment_code, name_ar, status, created_by) 
            SELECT 'Mobile Payment', 'MOBILE', 'الدفع عبر الهاتف المحمول', TRUE, 1
            WHERE NOT EXISTS (SELECT 1 FROM payment_options WHERE payment_code = 'MOBILE')
            """);

        // Insert service charges
        stmt.execute("""
            INSERT INTO service_charges (name, description, charge_type, charge_value, apply_to, is_active, created_by) 
            SELECT 'Service Charge', 'Standard service charge', 'percentage', 10.00, 'invoice', TRUE, 'admin'
            WHERE NOT EXISTS (SELECT 1 FROM service_charges WHERE name = 'Service Charge')
            """);
        
        stmt.execute("""
            INSERT INTO service_charges (name, description, charge_type, charge_value, apply_to, is_active, created_by) 
            SELECT 'Delivery Charge', 'Delivery service charge', 'flat', 5.00, 'invoice', TRUE, 'admin'
            WHERE NOT EXISTS (SELECT 1 FROM service_charges WHERE name = 'Delivery Charge')
            """);
        
        stmt.execute("""
            INSERT INTO service_charges (name, description, charge_type, charge_value, apply_to, is_active, created_by) 
            SELECT 'VAT', 'Value Added Tax', 'percentage', 5.00, 'invoice', TRUE, 'admin'
            WHERE NOT EXISTS (SELECT 1 FROM service_charges WHERE name = 'VAT')
            """);

        // Insert currencies
        stmt.execute("""
            INSERT INTO currencies (currency_code, currency_name, symbol, decimal_places, country, status) 
            SELECT 'AED', 'UAE Dirham', 'د.إ', 2, 'United Arab Emirates', TRUE
            WHERE NOT EXISTS (SELECT 1 FROM currencies WHERE currency_code = 'AED')
            """);
        
        stmt.execute("""
            INSERT INTO currencies (currency_code, currency_name, symbol, decimal_places, country, status) 
            SELECT 'USD', 'US Dollar', '$', 2, 'United States', TRUE
            WHERE NOT EXISTS (SELECT 1 FROM currencies WHERE currency_code = 'USD')
            """);
        
        stmt.execute("""
            INSERT INTO currencies (currency_code, currency_name, symbol, decimal_places, country, status) 
            SELECT 'EUR', 'Euro', '€', 2, 'European Union', TRUE
            WHERE NOT EXISTS (SELECT 1 FROM currencies WHERE currency_code = 'EUR')
            """);
        
        stmt.execute("""
            INSERT INTO currencies (currency_code, currency_name, symbol, decimal_places, country, status) 
            SELECT 'SAR', 'Saudi Riyal', 'ر.س', 2, 'Saudi Arabia', TRUE
            WHERE NOT EXISTS (SELECT 1 FROM currencies WHERE currency_code = 'SAR')
            """);

        // Insert languages
        stmt.execute("""
            INSERT INTO language (language_name, language_code, is_rtl, status, created_by) 
            SELECT 'English', 'en', FALSE, 'Active', 'admin'
            WHERE NOT EXISTS (SELECT 1 FROM language WHERE language_code = 'en')
            """);
        
        stmt.execute("""
            INSERT INTO language (language_name, language_code, is_rtl, status, created_by) 
            SELECT 'Arabic', 'ar', TRUE, 'Active', 'admin'
            WHERE NOT EXISTS (SELECT 1 FROM language WHERE language_code = 'ar')
            """);

        // Insert countries
        stmt.execute("""
            INSERT INTO countries (country_name, isd, currency_code, currency_symbol, country_code4, status, created_by) 
            SELECT 'United Arab Emirates', '+971', 'AED', 'د.إ', 'UAE', TRUE, 1
            WHERE NOT EXISTS (SELECT 1 FROM countries WHERE country_code4 = 'UAE')
            """);
        
        stmt.execute("""
            INSERT INTO countries (country_name, isd, currency_code, currency_symbol, country_code4, status, created_by) 
            SELECT 'Saudi Arabia', '+966', 'SAR', 'ر.س', 'SAU', TRUE, 1
            WHERE NOT EXISTS (SELECT 1 FROM countries WHERE country_code4 = 'SAU')
            """);
        
        stmt.execute("""
            INSERT INTO countries (country_name, isd, currency_code, currency_symbol, country_code4, status, created_by) 
            SELECT 'United States', '+1', 'USD', '$', 'USA', TRUE, 1
            WHERE NOT EXISTS (SELECT 1 FROM countries WHERE country_code4 = 'USA')
            """);
    }
}
