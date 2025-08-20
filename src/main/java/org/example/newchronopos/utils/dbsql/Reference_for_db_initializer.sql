-- This SQL script is not for use directly(this is the db design for this project i have created and exported as sql for database initializer) create the db just for reference consider it as db diagram using this sql initialize the databaseinitializer class




CREATE TABLE `activity_logs` (
  `log_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `timestamp` datetime DEFAULT (current_timestamp),
  `log_level` varchar(20),
  `user_id` bigint,
  `module` varchar(50),
  `action` varchar(100),
  `description` text,
  `ip_address` varchar(45),
  `device_info` varchar(255),
  `status` varchar(20) DEFAULT 'SUCCESS',
  `reference_id` bigint,
  `old_values` json,
  `new_values` json
);

CREATE TABLE `language` (
  `id` int PRIMARY KEY,
  `language_name` varchar(255),
  `language_code` varchar(255),
  `is_rtl` boolean,
  `status` varchar(255),
  `created_by` varchar(255),
  `created_at` timestamp,
  `updated_by` varchar(255),
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` varchar(255)
);

CREATE TABLE `language_keyword` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `key` varchar(100) UNIQUE,
  `description` text
);

CREATE TABLE `label_translation` (
  `id` int PRIMARY KEY,
  `language_id` int UNIQUE,
  `translation_key` varchar(255),
  `value` varchar(255),
  `status` varchar(255),
  `created_by` varchar(255),
  `created_at` timestamp,
  `updated_by` varchar(255),
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` varchar(255)
);

CREATE TABLE `industry_type` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) UNIQUE,
  `description` text,
  `status` bool,
  `created_at` timestamp,
  `updated_by` varchar(255),
  `updated_at` timestamp,
  `deleted_at` timestamp
);

CREATE TABLE `industry_type_access` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `industry_type_id` int UNIQUE,
  `dashboard_access` boolean DEFAULT false,
  `reports_access` boolean DEFAULT false,
  `client_office_access` boolean DEFAULT false,
  `transactions_access` boolean DEFAULT false,
  `reservation_access` boolean DEFAULT false,
  `notifications_access` boolean DEFAULT false,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `owner` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100),
  `email` varchar(100) UNIQUE,
  `password` varchar(255),
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `plan` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100),
  `description` text,
  `price` decimal(10,2),
  `billing_cycle` enum(monthly,yearly) DEFAULT 'monthly',
  `max_users` int,
  `max_outlets` int,
  `max_products` int,
  `max_invoices` int,
  `features` text,
  `is_active` boolean DEFAULT true,
  `priority` int,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `company_settings` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `company_id` int,
  `currency_id` int NOT NULL,
  `stock_value_id` int,
  `primary_color` varchar(20),
  `secondary_color` varchar(20),
  `client_backup_frequency` varchar(50),
  `atmacss_backup_frequency` varchar(50),
  `refund_type` varchar(50),
  `period_of_validity` int,
  `allow_return_cash` boolean DEFAULT false,
  `allow_credit_note` boolean DEFAULT false,
  `allow_exchange_transaction` boolean DEFAULT false,
  `has_sku_format` boolean DEFAULT false,
  `has_invoice_format` boolean DEFAULT false,
  `company_subscription_type` varchar(50),
  `invoice_default_language_id` int,
  `number_of_users` int DEFAULT 1,
  `invoice_printers` int,
  `barcode_scanners` int,
  `normal_printer` int,
  `barcode_printer` int,
  `weighing_machine` int,
  `selling_type` varchar(50),
  `status` varchar(20),
  `created_by` int,
  `created_at` datetime,
  `updated_by` int,
  `updated_at` datetime,
  `deleted_by` int,
  `deleted_at` datetime
);

CREATE TABLE `currencies` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `currency_code` varchar(3) UNIQUE NOT NULL,
  `currency_name` varchar(100) NOT NULL,
  `symbol` varchar(10) NOT NULL,
  `decimal_places` int NOT NULL DEFAULT 2,
  `country` varchar(100) NOT NULL,
  `status` boolean NOT NULL DEFAULT true,
  `created_at` datetime DEFAULT (now()),
  `updated_at` datetime,
  `deleted_at` datetime
);

CREATE TABLE `countries` (
  `country_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `country_name` varchar(100) NOT NULL,
  `isd` varchar(10),
  `currency_code` varchar(3),
  `currency_symbol` varchar(10),
  `flag_icon` varchar(255),
  `country_code4` varchar(4),
  `status` boolean DEFAULT true,
  `created_by` bigint,
  `created_at` datetime DEFAULT (current_timestamp),
  `updated_by` bigint,
  `updated_at` datetime,
  `deleted_by` bigint,
  `deleted_at` datetime
);

CREATE TABLE `states` (
  `state_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `country_id` bigint NOT NULL,
  `state_name` varchar(100) NOT NULL,
  `state_code` varchar(10),
  `status` boolean DEFAULT true,
  `created_by` bigint,
  `created_at` datetime DEFAULT (current_timestamp),
  `updated_by` bigint,
  `updated_at` datetime,
  `deleted_by` bigint,
  `deleted_at` datetime
);

CREATE TABLE `cities` (
  `city_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `state_id` bigint NOT NULL,
  `city_name` varchar(100) NOT NULL,
  `postal_code` varchar(20),
  `status` boolean DEFAULT true,
  `created_by` bigint,
  `created_at` datetime DEFAULT (current_timestamp),
  `updated_by` bigint,
  `updated_at` datetime,
  `deleted_by` bigint,
  `deleted_at` datetime
);

CREATE TABLE `users` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `deleted` boolean DEFAULT false,
  `owner_id` bigint,
  `full_name` varchar(100),
  `email` varchar(100) UNIQUE,
  `password` varchar(255),
  `role` varchar(50),
  `phone_no` varchar(20),
  `salary` decimal(10,2),
  `dob` date,
  `nationality_status` enum(inactive,active),
  `role_permission_id` int NOT NULL,
  `shopid` int NOT NULL,
  `change_access` boolean DEFAULT false,
  `shift_type_id` bigint,
  `address` text,
  `additional_details` text,
  `uae_id` varchar(50) UNIQUE,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `roles` (
  `role_id` int PRIMARY KEY AUTO_INCREMENT,
  `role_name` varchar(100) NOT NULL,
  `description` text,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `permissions` (
  `permission_id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `code` varchar(50) UNIQUE NOT NULL,
  `screen_name` varchar(100) COMMENT 'UI screen this applies to',
  `type_matrix` varchar(20) COMMENT 'CRUD operations: Create,Read,Update,Delete',
  `is_parent` boolean DEFAULT false,
  `parent_permission_id` int COMMENT 'For hierarchical permissions',
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `roles_permission` (
  `role_permission_id` int PRIMARY KEY AUTO_INCREMENT,
  `role_id` int NOT NULL,
  `permission_id` int NOT NULL,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `user_permission_overrides` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `permission_id` int NOT NULL,
  `is_allowed` boolean DEFAULT true,
  `reason` text,
  `valid_from` timestamp,
  `valid_to` timestamp,
  `created_by` int,
  `created_at` timestamp DEFAULT (now())
);
--category module

CREATE TABLE `category` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `name_ar` varchar(100),
  `parent_id` int,
  `description` text,
  `image_url` text,
  `display_order` int DEFAULT 0,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now()),
  `updated_at` timestamp DEFAULT (now())
);
CREATE TABLE `category_translation` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `category_id` int NOT NULL,
  `language_code` varchar(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text,
  `created_at` timestamp DEFAULT (now())
);
--product module
CREATE TABLE `product` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `deleted` int NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT (now()),
  `updated_at` timestamp NOT NULL DEFAULT (now()),
  `status` varchar(20) DEFAULT 'Active',
  `type` varchar(20) COMMENT '''Physical'', ''Digital'', ''Service'''
);

CREATE TABLE `product_info` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int UNIQUE NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `product_name_ar` varchar(100),
  `alternate_name` varchar(100),
  `alternate_name_ar` varchar(100),
  `full_description` text,
  `full_description_ar` text,
  `short_description` text,
  `short_description_ar` text,
  `sku` varchar(100) UNIQUE NOT NULL,
  `model_number` varchar(100),
  `created_barcode` boolean NOT NULL DEFAULT false,
  `has_standard_barcode` boolean NOT NULL DEFAULT true,
  `category_id` int NOT NULL,
  `sub_category_lvl1_id` int,
  `sub_category_lvl2_id` int,
  `brand_id` int,
  `product_unit` varchar(50) NOT NULL,
  `weight` decimal(10,2),
  `dimensions` varchar(100),
  `specs_flag` boolean NOT NULL DEFAULT true,
  `specs` text,
  `color` varchar(50),
  `reorder_level` int NOT NULL DEFAULT 0,
  `store_location` varchar(200),
  `can_return` boolean NOT NULL DEFAULT false,
  `country_of_origin` varchar(100),
  `supplier_id` int,
  `shop_location_id` int,
  `stock_unit_id` int,
  `purchase_unit_id` int,
  `selling_unit_id` int,
  `with_expiry_date` boolean DEFAULT false,
  `expiry_days` int COMMENT 'For products with expiry',
  `has_warranty` boolean DEFAULT false,
  `warranty_period` int COMMENT 'In months',
  `warranty_type_id` int,
  `price_type` varchar(20) COMMENT '''Fixed'', ''Variable'', ''Tiered''',
  `created_at` timestamp NOT NULL DEFAULT (now()),
  `updated_at` timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE `product_quantity_history` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int,
  `quantity` decimal(10,2),
  `reorder_level` int,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `brand` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `deleted` int,
  `name` varchar(100) UNIQUE NOT NULL,
  `name_arabic` varchar(100),
  `description` text,
  `logo_url` text,
  `created_at` timestamp DEFAULT (now()),
  `updated_at` timestamp DEFAULT (now())
);

CREATE TABLE `product_attributes` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `name_ar` varchar(100),
  `is_required` boolean DEFAULT false,
  `type` varchar(20) COMMENT '''Color'', ''Size'', ''Material'', ''Custom''',
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int NOT NULL,
  `created_at` timestamp DEFAULT (now()),
  `updated_at` timestamp DEFAULT (now())
);

CREATE TABLE `product_attribute_values` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `attribute_id` int NOT NULL,
  `value` varchar(100) NOT NULL,
  `value_ar` varchar(100),
  `status` varchar(20) DEFAULT 'Active',
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `product_attribute_options` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `attribute_id` int NOT NULL,
  `value_id` int NOT NULL,
  `price_adjustment` decimal(10,2) DEFAULT 0,
  `status` varchar(20) DEFAULT 'Active',
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `product_combinations` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `combination_string` varchar(255) NOT NULL,
  `sku` varchar(100),
  `barcode` varchar(50),
  `price` decimal(12,2),
  `cost_price` decimal(12,2),
  `quantity` decimal(12,4) DEFAULT 0,
  `image_url` text,
  `is_active` boolean DEFAULT true,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `product_combination_items` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `combination_id` int NOT NULL,
  `attribute_id` int NOT NULL,
  `value_id` int NOT NULL,
  `created_at` timestamp DEFAULT (now())
);
--auto generated barcodes
CREATE TABLE `product_barcodes` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `barcode` varchar(100) UNIQUE NOT NULL,
  `barcode_type` varchar(20) NOT NULL DEFAULT 'ean',
  `created_at` timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE `product_images` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `image_url` text NOT NULL,
  `alt_text` varchar(255),
  `sort_order` int NOT NULL DEFAULT 0,
  `is_primary` boolean DEFAULT false,
  `created_at` timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE `product_modifiers` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` text,
  `price` decimal(10,2) DEFAULT 0,
  `cost` decimal(10,2) DEFAULT 0,
  `sku` varchar(50),
  `barcode` varchar(50),
  `tax_type_id` int,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int NOT NULL,
  `created_at` timestamp DEFAULT (now()),
  `updated_at` timestamp DEFAULT (now())
);

CREATE TABLE `product_modifier_groups` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` text,
  `selection_type` varchar(20) COMMENT '''Single'', ''Multiple''',
  `required` boolean DEFAULT false,
  `min_selections` int DEFAULT 0,
  `max_selections` int,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int NOT NULL,
  `created_at` timestamp DEFAULT (now()),
  `updated_at` timestamp DEFAULT (now())
);

CREATE TABLE `product_modifier_group_items` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `group_id` int NOT NULL,
  `modifier_id` int NOT NULL,
  `price_adjustment` decimal(10,2) DEFAULT 0,
  `sort_order` int DEFAULT 0,
  `default_selection` boolean DEFAULT false,
  `status` varchar(20) DEFAULT 'Active',
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `product_modifier_links` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `modifier_group_id` int NOT NULL,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `modifiers` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` text,
  `extra_price` decimal(10,2) NOT NULL DEFAULT 0,
  `is_active` boolean NOT NULL DEFAULT true,
  `deleted` int NOT NULL DEFAULT 0,
  `created_by` int NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT (now()),
  `updated_at` timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE `product_batches` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `batch_no` varchar(50) NOT NULL,
  `manufacture_date` date,
  `expiry_date` date,
  `quantity` decimal(12,4) NOT NULL,
  `uom_id` int NOT NULL,
  `cost_price` decimal(12,2),
  `landed_cost` decimal(12,2),
  `status` varchar(20) DEFAULT 'Active',
  `created_at` timestamp DEFAULT (now())
);
--stock module
CREATE TABLE `stock_movement` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `batch_id` int,
  `uom_id` int NOT NULL,
  `movement_type` varchar(20) COMMENT '''Purchase'', ''Sale'', ''Transfer'', ''Adjustment'', ''Waste''',
  `quantity` decimal(12,4) NOT NULL,
  `reference_type` varchar(50) COMMENT '''PurchaseOrder'', ''Sale'', ''Transfer'', ''Adjustment''',
  `reference_id` int NOT NULL,
  `location_id` int,
  `notes` text,
  `created_by` int NOT NULL,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `stock_transfer` (
  `transfer_id` int PRIMARY KEY AUTO_INCREMENT,
  `transfer_no` varchar(30) UNIQUE NOT NULL,
  `transfer_date` date NOT NULL,
  `from_store_id` int NOT NULL,
  `to_store_id` int NOT NULL,
  `status` varchar(20) DEFAULT 'Pending',
  `remarks` text,
  `created_by` int NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT (now()),
  `updated_at` timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE `stock_transfer_item` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `transfer_id` int NOT NULL,
  `product_id` int NOT NULL,
  `uom_id` int NOT NULL,
  `batch_no` varchar(50),
  `expiry_date` date,
  `quantity_sent` decimal(10,3) NOT NULL,
  `quantity_received` decimal(10,3) DEFAULT 0,
  `damaged_qty` decimal(10,3) DEFAULT 0,
  `status` varchar(20) DEFAULT 'Pending',
  `remarks_line` text
);

CREATE TABLE `stock_adjustment` (
  `adjustment_id` int PRIMARY KEY AUTO_INCREMENT,
  `adjustment_no` varchar(30) UNIQUE NOT NULL,
  `adjustment_date` date NOT NULL,
  `store_location_id` int NOT NULL,
  `reason_id` int NOT NULL,
  `status` varchar(20) DEFAULT 'Pending',
  `remarks` text,
  `created_by` int NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT (now()),
  `updated_at` timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE `stock_adjustment_reasons` (
  `stock_adjustment_reasons_id` int PRIMARY KEY,
  `name` varchar(255),
  `description` varchar(255),
  `status` varchar(255),
  `created_by` int,
  `created_at` timestamp,
  `updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp
);

CREATE TABLE `stock_adjustment_item` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `adjustment_id` int NOT NULL,
  `product_id` int NOT NULL,
  `uom_id` int NOT NULL,
  `batch_no` varchar(50),
  `expiry_date` date,
  `quantity_before` decimal(10,3) NOT NULL,
  `quantity_after` decimal(10,3) NOT NULL,
  `difference_qty` decimal(10,3) NOT NULL,
  `reason_line` varchar(100),
  `remarks_line` text
);

CREATE TABLE `stock_ledger` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `unit_id` int NOT NULL,
  `movement_type` varchar(20) NOT NULL,
  `qty` decimal(10,2) NOT NULL,
  `balance` decimal(10,2) NOT NULL,
  `location` varchar(200),
  `reference_type` varchar(50),
  `reference_id` int,
  `created_at` timestamp NOT NULL DEFAULT (now())
);
--supplier module
CREATE TABLE `supplier` (
  `supplier_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `shop_id` bigint,
  `company_name` varchar(100) NOT NULL,
  `logo_picture` varchar(255),
  `license_number` varchar(50),
  `owner_name` varchar(100),
  `owner_mobile` varchar(20),
  `vat_trn_number` varchar(50),
  `email` varchar(100) UNIQUE,
  `address_line1` varchar(255) NOT NULL,
  `address_line2` varchar(255),
  `building` varchar(100),
  `area` varchar(100),
  `po_box` varchar(20),
  `city` varchar(100),
  `state` varchar(100),
  `country` varchar(100),
  `website` varchar(100),
  `key_contact_name` varchar(100),
  `key_contact_mobile` varchar(20),
  `key_contact_email` varchar(100),
  `mobile` varchar(20),
  `location_latitude` decimal(10,8),
  `location_longitude` decimal(11,8),
  `company_phone_number` varchar(20),
  `gstin` varchar(20),
  `pan` varchar(20),
  `payment_terms` varchar(50),
  `opening_balance` decimal(12,2) DEFAULT 0,
  `balance_type` enum(credit,debit) DEFAULT 'credit',
  `status` varchar(20) DEFAULT 'Active',
  `created_by` bigint,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` bigint,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` bigint
);

CREATE TABLE `supplier_translation` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `supplier_id` bigint NOT NULL,
  `language_id` int NOT NULL,
  `supplier_name_ar` varchar(100),
  `address_line1_ar` varchar(255),
  `address_line2_ar` varchar(255),
  `building_ar` varchar(100),
  `area_ar` varchar(100),
  `status` varchar(20) DEFAULT 'Active',
  `created_by` bigint,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` bigint,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` bigint
);

CREATE TABLE `supplier_bank_accounts` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `supplier_id` bigint NOT NULL,
  `bank_name` varchar(100) NOT NULL,
  `account_number` varchar(50) NOT NULL,
  `iban_ifsc` varchar(50),
  `swift_code` varchar(20),
  `branch_name` varchar(100),
  `account_type` varchar(50) COMMENT 'Current/Savings/etc.',
  `branch_address` text,
  `bank_key_person_name` varchar(100),
  `bank_key_contacts` varchar(100),
  `notes` text,
  `is_primary` boolean DEFAULT false,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` bigint,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` bigint,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` bigint
);

CREATE TABLE `supplier_purchase` (
  `purchase_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `supplier_id` bigint NOT NULL,
  `invoice_no` varchar(50) NOT NULL,
  `invoice_date` date NOT NULL,
  `due_date` date,
  `total_amount` decimal(12,2) NOT NULL,
  `tax_amount` decimal(12,2),
  `discount_amount` decimal(12,2),
  `paid_amount` decimal(12,2) DEFAULT 0,
  `due_amount` decimal(12,2),
  `status` enum(paid,partial,unpaid,cancelled) DEFAULT 'unpaid',
  `payment_terms` varchar(100),
  `delivery_terms` varchar(100),
  `remarks` text,
  `created_by` bigint,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` bigint,
  `updated_at` timestamp DEFAULT (now())
);

CREATE TABLE `supplier_purchase_item` (
  `item_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `purchase_id` bigint NOT NULL,
  `product_id` int NOT NULL,
  `product_variant_id` int,
  `quantity` decimal(10,3) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `tax_rate` decimal(5,2),
  `discount_rate` decimal(5,2),
  `total_price` decimal(12,2) NOT NULL,
  `received_quantity` decimal(10,3),
  `status` varchar(20) DEFAULT 'Active'
);
--warranty tables can be used in different tables like product, sales, etc.
CREATE TABLE `warranty_type` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `name_ar` varchar(100),
  `is_additional` boolean DEFAULT false,
  `cost` decimal(10,2) COMMENT 'Only when is_additional=true',
  `description` text,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int NOT NULL,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp NOT NULL DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `product_warranty` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `warranty_type_id` int NOT NULL,
  `duration_months` int NOT NULL,
  `terms_conditions` text,
  `is_included` boolean DEFAULT true,
  `created_by` int NOT NULL,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE `sales_warranty` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `sale_id` int NOT NULL,
  `product_id` int NOT NULL,
  `warranty_type_id` int NOT NULL,
  `duration_months` int NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `cost` decimal(10,2) DEFAULT 0,
  `status` varchar(20) DEFAULT 'Active',
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `warranty_claims` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `sales_warranty_id` int NOT NULL,
  `claim_date` date NOT NULL,
  `description` text NOT NULL,
  `resolution` text,
  `status` varchar(20) DEFAULT 'Pending',
  `resolved_by` int,
  `resolved_at` timestamp,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `product_variants` (
  `variant_id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `unit_option_id` int NOT NULL,
  `value` decimal(12,4) NOT NULL,
  `label` varchar(100) NOT NULL,
  `barcode` varchar(50) UNIQUE,
  `price` decimal(12,2) NOT NULL,
  `cost_price` decimal(12,2) NOT NULL,
  `stock_quantity` decimal(12,4) DEFAULT 0,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int NOT NULL,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);
--companies module
CREATE TABLE `companies` (
  `company_id` int PRIMARY KEY AUTO_INCREMENT,
  `company_name` varchar(100) NOT NULL,
  `logo_picture` varchar(255),
  `license_number` varchar(50) UNIQUE,
  `vat_trn_number` varchar(50),
  `phone_number` varchar(20),
  `email` varchar(100) UNIQUE,
  `website` varchar(100),
  `key_contact_name` varchar(100),
  `key_contact_mobile` varchar(20),
  `key_contact_email` varchar(100),
  `location_latitude` decimal(10,8),
  `location_longitude` decimal(11,8),
  `remarks` text,
  `status` varchar(20) DEFAULT 'active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `company_owners` (
  `owner_id` int PRIMARY KEY AUTO_INCREMENT,
  `company_id` int,
  `owner_first_name` varchar(50) NOT NULL,
  `owner_last_name` varchar(50) NOT NULL,
  `auth_number` varchar(50),
  `owner_mobile` varchar(20),
  `owner_uae_id` varchar(50),
  `owner_email` varchar(100),
  `status` varchar(20) DEFAULT 'active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `company_addresses` (
  `company_address_id` int PRIMARY KEY AUTO_INCREMENT,
  `company_id` int,
  `address_line1` varchar(255) NOT NULL,
  `address_line2` varchar(255),
  `area` varchar(100),
  `po_box` varchar(20),
  `city` varchar(100),
  `state_id` int,
  `country_id` int,
  `is_billing_address` boolean DEFAULT false,
  `location_latitude` decimal(10,8),
  `location_longitude` decimal(11,8),
  `status` varchar(20) DEFAULT 'active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `company_bank_accounts` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `company_id` int,
  `company_address_id` int,
  `account_type` varchar(50) COMMENT 'Current/Savings/etc.',
  `account_name` varchar(100) NOT NULL,
  `account_number` varchar(50) NOT NULL,
  `iban` varchar(50),
  `swift_code` varchar(20),
  `branch_name` varchar(100),
  `branch_address` text,
  `status` varchar(20) DEFAULT 'active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `license_info` (
  `id` uuid PRIMARY KEY DEFAULT (uuid_generate_v4()),
  `company_id` int,
  `license_key` varchar(100) UNIQUE NOT NULL,
  `organization_name` varchar(255),
  `issued_to` varchar(255),
  `issued_on` timestamp NOT NULL,
  `valid_until` timestamp NOT NULL,
  `device_limit` int,
  `allowed_macs` text COMMENT 'Comma-separated MAC addresses',
  `allowed_ips` text COMMENT 'Comma-separated IP addresses',
  `license_type` varchar(50) COMMENT 'PER_DEVICE/PER_LOCATION/UNLIMITED',
  `is_active` boolean DEFAULT true,
  `plan_id` int,
  `notes` text,
  `created_at` timestamp DEFAULT (now()),
  `updated_at` timestamp DEFAULT (now())
);

CREATE TABLE `license_modules` (
  `id` uuid PRIMARY KEY DEFAULT (uuid_generate_v4()),
  `license_id` uuid,
  `module_name` varchar(100) NOT NULL,
  `is_enabled` boolean DEFAULT true,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `license_device_usage` (
  `id` uuid PRIMARY KEY DEFAULT (uuid_generate_v4()),
  `license_id` uuid,
  `mac_address` varchar(50) NOT NULL,
  `ip_address` varchar(50),
  `device_name` varchar(100),
  `last_checked_in` timestamp,
  `created_at` timestamp DEFAULT (now())
);
--customer module
CREATE TABLE `customer` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `customer_full_name` varchar(150),
  `business_full_name` varchar(150),
  `is_business` boolean,
  `business_type_id` int,
  `customer_balance_amount` decimal,
  `license_no` varchar(50),
  `trn_no` varchar(50),
  `mobile_no` varchar(20),
  `home_phone` varchar(20),
  `office_phone` varchar(20),
  `contact_mobile_no` varchar(20),
  `credit_allowed` boolean,
  `credit_amount_max` decimal,
  `credit_days` int,
  `credit_reference1_name` varchar(150),
  `credit_reference2_name` varchar(150),
  `key_contact_name` varchar(150),
  `key_contact_mobile` varchar(20),
  `key_contact_email` varchar(100),
  `finance_person_name` varchar(150),
  `finance_person_mobile` varchar(20),
  `finance_person_email` varchar(100),
  `official_email` varchar(100),
  `post_dated_cheques_allowed` boolean,
  `shop_id` int,
  `status` varchar(20),
  `created_by` int,
  `created_at` timestamp,
  `updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `business_type_master` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `business_type_name` varchar(100),
  `business_type_name_ar` varchar(100),
  `status` varchar(20),
  `created_by` int,
  `created_at` timestamp,
  `updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `customer_address` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `customer_id` int,
  `address_line1` varchar(255),
  `address_line2` varchar(255),
  `po_box` varchar(50),
  `area` varchar(100),
  `city` varchar(100),
  `state_id` int,
  `country_id` int,
  `landmark` varchar(255),
  `latitude` decimal,
  `longitude` decimal,
  `is_billing` boolean,
  `status` varchar(20),
  `created_by` int,
  `created_at` timestamp,
  `updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `customer_address_translation` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `customer_address_id` int,
  `language_id` int,
  `address_line1` varchar(255),
  `address_line2` varchar(255),
  `area` varchar(100),
  `status` varchar(20),
  `created_by` int,
  `created_at` timestamp,
  `updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `customers_groups` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100),
  `name_ar` varchar(100),
  `selling_price_type_id` int,
  `discount_id` int,
  `discount_value` decimal,
  `discount_max_value` decimal,
  `is_percentage` boolean,
  `status` varchar(20),
  `created_by` int,
  `created_at` timestamp,
  `updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);
--discounts tables
CREATE TABLE `discounts` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `discount_name` varchar(150),
  `discount_name_ar` varchar(150),
  `discount_code` varchar(50) UNIQUE,
  `discount_type` varchar(20),
  `discount_value` decimal(10,2),
  `max_discount_amount` decimal(10,2),
  `min_purchase_amount` decimal(10,2),
  `start_date` date,
  `end_date` date,
  `applicable_on` varchar(50),
  `applicable_id` int,
  `is_active` boolean DEFAULT true,
  `created_by` int,
  `created_at` timestamp,
  `updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `product_discount` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int,
  `discounts_id` int,
  `is_active` boolean DEFAULT true,
  `created_by` int,
  `created_at` timestamp,
  `updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `customers_group_relation` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `customer_id` int,
  `customer_group_id` int,
  `status` varchar(20),
  `created_by` int,
  `created_at` timestamp,
  `updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `customer_balance` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `customer_id` int,
  `new_balance` decimal,
  `last_update` timestamp,
  `status` varchar(20),
  `created_by` int,
  `created_at` timestamp,
  `updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);
--transaction module
CREATE TABLE `transactions` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `shift_id` int NOT NULL,
  `customer_id` int,
  `user_id` int NOT NULL,
  `shop_location_id` int,
  `selling_time` timestamp NOT NULL DEFAULT (now()),
  `total_amount` decimal(12,2) NOT NULL DEFAULT 0,
  `total_vat` decimal(12,2) NOT NULL DEFAULT 0,
  `total_discount` decimal(12,2) NOT NULL DEFAULT 0,
  `total_applied_vat` decimal(12,2) DEFAULT 0,
  `total_applied_discount_value` decimal(12,2) DEFAULT 0,
  `amount_paid_cash` decimal(12,2) DEFAULT 0,
  `amount_credit_remaining` decimal(12,2) DEFAULT 0,
  `credit_days` int DEFAULT 0,
  `is_percentage_discount` boolean DEFAULT false,
  `discount_value` decimal(12,2) DEFAULT 0,
  `discount_max_value` decimal(12,2) DEFAULT 0,
  `vat` decimal(12,2) DEFAULT 0,
  `discount_note` text,
  `invoice_number` varchar(50) UNIQUE,
  `status` enum(pending,paid,cancelled) DEFAULT 'pending',
  `created_by` int NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `transaction_products` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `transaction_id` int NOT NULL,
  `product_id` int NOT NULL,
  `shop_location_id` int,
  `buyer_cost` decimal(12,2) NOT NULL DEFAULT 0,
  `selling_price` decimal(12,2) NOT NULL,
  `product_unit_id` int,
  `is_percentage_discount` boolean DEFAULT false,
  `discount_value` decimal(12,2) DEFAULT 0,
  `discount_max_value` decimal(12,2) DEFAULT 0,
  `vat` decimal(12,2) DEFAULT 0,
  `quantity` decimal(10,3) NOT NULL,
  `status` enum(active,returned,exchanged) DEFAULT 'active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `transaction_modifiers` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `transaction_product_id` int NOT NULL,
  `product_modifier_id` int NOT NULL,
  `extra_price` decimal(10,2) NOT NULL DEFAULT 0,
  `created_by` int,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `transaction_service_charges` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `transaction_id` int NOT NULL,
  `service_charge_id` int NOT NULL,
  `total_amount` decimal(12,2) DEFAULT 0,
  `total_vat` decimal(12,2) DEFAULT 0,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `refund_transactions` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `customer_id` int,
  `selling_transaction_id` int NOT NULL,
  `shift_id` int,
  `user_id` int,
  `total_amount` decimal(12,2) DEFAULT 0,
  `total_vat` decimal(12,2) DEFAULT 0,
  `is_cash` boolean DEFAULT true,
  `refund_time` timestamp DEFAULT (now()),
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `refund_transaction_products` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `refund_transaction_id` int NOT NULL,
  `transaction_product_id` int NOT NULL,
  `total_quantity_returned` decimal(10,3) DEFAULT 0,
  `total_vat` decimal(12,2) DEFAULT 0,
  `total_amount` decimal(12,2) DEFAULT 0,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `exchange_transactions` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `customer_id` int,
  `selling_transaction_id` int NOT NULL,
  `shift_id` int,
  `total_exchanged_amount` decimal(12,2) DEFAULT 0,
  `total_exchanged_vat` decimal(12,2) DEFAULT 0,
  `product_exchanged_quantity` decimal(10,3) DEFAULT 0,
  `exchange_time` timestamp DEFAULT (now()),
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `customer_payments` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `customer_id` int,
  `shift_id` int,
  `amount_paid` decimal(12,2) DEFAULT 0,
  `cheque_number` varchar(50),
  `cheque_date` date,
  `cheque_bank_name` varchar(100),
  `payment_time` timestamp DEFAULT (now()),
  `is_paid` boolean DEFAULT true,
  `payment_type` varchar(20),
  `ref_payment_id` int,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `Payment_Options` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `payment_code` varchar(50) NOT NULL,
  `name_ar` varchar(255),
  `status` boolean NOT NULL DEFAULT true,
  `created_by` int,
  `created_at` datetime,
  `updated_by` int,
  `updated_at` datetime,
  `deleted_by` int,
  `deleted_at` datetime
);

CREATE TABLE `payment_transaction` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `sales_id` int,
  `payment_method_id` int NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `transaction_reference` varchar(100),
  `notes` text,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `tax_types` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` text,
  `value` decimal(10,4) NOT NULL,
  `included_in_price` boolean DEFAULT false,
  `is_percentage` boolean DEFAULT true,
  `applies_to_buying` boolean DEFAULT false,
  `applies_to_selling` boolean DEFAULT true,
  `status` boolean DEFAULT true,
  `created_by` bigint,
  `created_at` datetime DEFAULT (current_timestamp),
  `updated_by` bigint,
  `updated_at` datetime,
  `deleted_by` bigint,
  `deleted_at` datetime
);

CREATE TABLE `product_taxes` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `tax_type_id` bigint NOT NULL
);

CREATE TABLE `service_charges` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100),
  `description` text,
  `charge_type` enum(flat,percentage),
  `charge_value` decimal(10,2),
  `charge_value_arabic` decimal(10,2),
  `apply_to` enum(invoice,product,category,order),
  `is_taxable` boolean DEFAULT false,
  `is_active` boolean DEFAULT true,
  `created_by` varchar(255),
  `created_at` timestamp,
  `updated_by` varchar(255),
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` varchar(255)
);

CREATE TABLE `service_charge_types` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `service_charge_type_code` varchar(20) UNIQUE NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text,
  `calculation_method` varchar(20) DEFAULT 'PERCENT',
  `rate_value` decimal(10,2),
  `status` boolean DEFAULT true,
  `created_by` bigint,
  `created_at` datetime DEFAULT (current_timestamp),
  `updated_by` bigint,
  `updated_at` datetime,
  `deleted_by` bigint,
  `deleted_at` datetime
);

CREATE TABLE `service_charge_types_translations` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `language_id` bigint NOT NULL,
  `service_charge_type_id` bigint NOT NULL,
  `name` varchar(100) NOT NULL,
  `status` boolean DEFAULT true,
  `created_by` bigint,
  `created_at` datetime DEFAULT (current_timestamp),
  `updated_by` bigint,
  `updated_at` datetime,
  `deleted_by` bigint,
  `deleted_at` datetime
);

CREATE TABLE `service_charge_options` (
  `service_charge_option_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `service_charge_type_id` bigint NOT NULL,
  `cost` decimal(10,2),
  `language_id` bigint NOT NULL,
  `name` varchar(100) NOT NULL,
  `price` decimal(10,2),
  `status` boolean DEFAULT true,
  `created_by` bigint,
  `created_at` datetime DEFAULT (current_timestamp),
  `updated_by` bigint,
  `updated_at` datetime,
  `deleted_by` bigint,
  `deleted_at` datetime
);

CREATE TABLE `service_charge_options_translations` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `language_id` bigint NOT NULL,
  `service_charge_option_id` bigint NOT NULL,
  `name` varchar(100) NOT NULL,
  `status` boolean DEFAULT true,
  `created_by` bigint,
  `created_at` datetime DEFAULT (current_timestamp),
  `updated_by` bigint,
  `updated_at` datetime,
  `deleted_by` bigint,
  `deleted_at` datetime
);
--shift tables
CREATE TABLE `shift_types` (
  `shift_type_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `shift_name` varchar(100) NOT NULL,
  `shop_location_id` bigint NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `status` boolean DEFAULT true COMMENT 'Active/Inactive',
  `created_by` bigint NOT NULL,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` bigint,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` bigint
);

CREATE TABLE `shifts` (
  `shift_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `shift_type_id` bigint,
  `cashbox_id` bigint NOT NULL,
  `start_balance` decimal(12,2) DEFAULT 0,
  `end_balance` decimal(12,2) DEFAULT 0,
  `open_date_time` timestamp NOT NULL,
  `close_date_time` timestamp,
  `status` varchar(20) DEFAULT 'Scheduled' COMMENT 'Scheduled/In-Progress/Completed/Closed',
  `created_by` bigint NOT NULL,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` bigint,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` bigint
);

CREATE TABLE `shift_user` (
  `shift_user_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `shift_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `status` varchar(20) DEFAULT 'Active' COMMENT 'Active/Replaced/Cancelled',
  `created_by` bigint NOT NULL,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` bigint,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` bigint
);

CREATE TABLE `shift_transactions` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `shift_id` bigint NOT NULL,
  `transaction_type` varchar(50) NOT NULL COMMENT 'Open/Close/Adjustment',
  `amount` decimal(12,2) NOT NULL,
  `notes` text,
  `created_by` bigint NOT NULL,
  `created_at` timestamp DEFAULT (now())
);
--cashbox tables
CREATE TABLE `cash_box` (
  `cash_box_id` int PRIMARY KEY AUTO_INCREMENT,
  `shop_location_id` int NOT NULL,
  `shift_id` int NOT NULL,
  `cash_box_name` varchar(100) NOT NULL,
  `status` varchar(20) DEFAULT 'Active' COMMENT 'Open/Closed/Inactive',
  `created_by` int NOT NULL,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `cash_in_out` (
  `cash_in_id` int PRIMARY KEY AUTO_INCREMENT,
  `cash_box_id` int NOT NULL,
  `payment_id` int NOT NULL,
  `reason` varchar(255),
  `amount_out` decimal(12,2) DEFAULT 0,
  `amount_in` decimal(12,2) DEFAULT 0,
  `note` text,
  `status` varchar(20) DEFAULT 'Completed' COMMENT 'Completed/Pending/Cancelled',
  `created_by` int NOT NULL,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `cash_box_balance` (
  `cash_box_id` int PRIMARY KEY,
  `current_balance` decimal(12,2) NOT NULL,
  `last_updated` timestamp DEFAULT (now())
);
--proggram module
CREATE TABLE `Programs` (
  `Program_ID` int PRIMARY KEY AUTO_INCREMENT,
  `vat` decimal(10,2),
  `is_percentage_discount` boolean,
  `discount_value` decimal(10,2),
  `discount_max_value` decimal(10,2),
  `discount_note` varchar(255),
  `total_applied_vat` decimal(10,2),
  `total_applied_discounts_value` decimal(10,2),
  `Name` varchar(150),
  `Description` text,
  `Start_Date` date,
  `End_Date` date,
  `Program_Price` decimal(10,2),
  `Number_of_Meals` int,
  `Status` varchar(20),
  `created_by` int,
  `created_at` timestamp,
  `Updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `Program_Translations` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `Program_id` int,
  `language_id` int,
  `name` varchar(150),
  `description` text,
  `Status` varchar(20),
  `created_by` int,
  `created_at` timestamp,
  `Updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `Program_Meals` (
  `Program_Meals_ID` int PRIMARY KEY AUTO_INCREMENT,
  `Program_ID` int,
  `Name` varchar(150),
  `Time` time,
  `Status` varchar(20),
  `created_by` int,
  `created_at` timestamp,
  `Updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `Program_Products` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `program_id` int,
  `product_id` int,
  `product_unit_id` int,
  `quantity` int,
  `price` decimal(10,2),
  `vat` decimal(10,2),
  `is_percentage_discount` boolean,
  `discount_value` decimal(10,2),
  `discount_max_value` decimal(10,2),
  `discount_note` varchar(255),
  `Status` varchar(20),
  `created_by` int,
  `created_at` timestamp,
  `Updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `Program_Subscriptions` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `program_id` int,
  `Customer_ID` int,
  `Status` varchar(20),
  `created_by` int,
  `created_at` timestamp,
  `Updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `Program_Order_Delivery` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `program_id` int,
  `customer_id` int,
  `delivery_status` varchar(20),
  `send_time` datetime,
  `Status` varchar(20),
  `created_by` int,
  `created_at` timestamp,
  `Updated_by` int,
  `updated_at` timestamp,
  `deleted_at` timestamp,
  `deleted_by` int
);
--shop tables
CREATE TABLE `shop` (
  `shop_id` int PRIMARY KEY AUTO_INCREMENT,
  `company_id` int NOT NULL,
  `industry_type_id` int,
  `name` varchar(100) NOT NULL,
  `pos_id` varchar(50) COMMENT 'POS system identifier',
  `pos_name` varchar(100),
  `number_of_locations_allowed` int DEFAULT 1,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `shop_locations` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `shop_id` int NOT NULL,
  `location_type` varchar(50) NOT NULL COMMENT 'Retail/Warehouse/etc.',
  `location_name` varchar(100) NOT NULL,
  `manager_id` int,
  `address_line1` varchar(255) NOT NULL,
  `address_line2` varchar(255),
  `building` varchar(100),
  `area` varchar(100),
  `po_box` varchar(20),
  `city` varchar(100),
  `state_id` int,
  `country_id` int,
  `landline_number` varchar(20),
  `mobile_number` varchar(20),
  `location_latitude` decimal(10,8),
  `location_longitude` decimal(11,8),
  `can_sell` boolean DEFAULT true,
  `language_id` int,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `shop_location_translation_ar` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `shop_location_id` int NOT NULL,
  `language_id` int NOT NULL,
  `name_ar` varchar(100),
  `address_line1_ar` varchar(255),
  `address_line2_ar` varchar(255),
  `building_ar` varchar(100),
  `area_ar` varchar(100),
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `shop_users` (
  `shop_users_id` int PRIMARY KEY AUTO_INCREMENT,
  `shop_id` int NOT NULL,
  `user_id` int NOT NULL,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `shop_type` (
  `shop_type_id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `default_selling_price_type_id` int,
  `start_time` time COMMENT 'Default opening time',
  `end_time` time COMMENT 'Default closing time',
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `uom` (
  `id` bigint PRIMARY KEY,
  `name` varchar(50),
  `abbreviation` varchar(10),
  `base_uom_id` bigint,
  `conversion_factor` decimal(10,4),
  `is_active` boolean,
  `created_at` datetime,
  `updated_at` datetime
);

CREATE TABLE `unit_options` (
  `units_options_id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255),
  `category_title` varchar(50),
  `type` varchar(20) NOT NULL COMMENT '''Base'' or ''Derived''',
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int NOT NULL,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);

CREATE TABLE `product_unit` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `unit_name` varchar(50) NOT NULL,
  `unit_symbol` varchar(10) NOT NULL,
  `parent_unit_id` int,
  `qty_in_parent` int NOT NULL DEFAULT 1,
  `is_base` boolean NOT NULL DEFAULT false,
  `created_at` timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE `unit_prices` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `unit_id` int NOT NULL,
  `color` varchar(50),
  `cost` decimal(10,2) NOT NULL,
  `selling_price_id` int NOT NULL,
  `price_type` varchar(50) NOT NULL DEFAULT 'Retail',
  `discount_allowed` boolean NOT NULL DEFAULT false,
  `created_at` timestamp NOT NULL DEFAULT (now()),
  `updated_at` timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE `selling_price_types` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `type_name` varchar(100) NOT NULL,
  `arabic_name` varchar(100) NOT NULL,
  `description` text,
  `status` boolean DEFAULT true,
  `created_by` bigint,
  `created_at` datetime DEFAULT (current_timestamp),
  `updated_by` bigint,
  `updated_at` datetime,
  `deleted_by` bigint,
  `deleted_at` datetime
);

CREATE TABLE `unit_option_conversion` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `unit_option_id` int NOT NULL,
  `unit_value_from_id` int NOT NULL,
  `unit_value_to_id` int NOT NULL,
  `unit_conversion_rate` decimal(12,6) NOT NULL,
  `status` varchar(20) DEFAULT 'Active',
  `created_by` int NOT NULL,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` int,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` int
);
--reservation module only for restaurant type shops
CREATE TABLE `reservation` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `deleted` int,
  `customer_id` int,
  `table_number` int,
  `number_of_persons` int,
  `reservation_date` date,
  `reservation_time` time,
  `deposit_fee` decimal(10,2),
  `payment_method` int NOT NULL,
  `status` enum(confirmed,waiting,cancelled) DEFAULT 'waiting',
  `notes` text,
  `created_at` timestamp DEFAULT (now())
);

CREATE TABLE `Tranasaction_types` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `type_name` varchar(255) NOT NULL,
  `type_name_ar` varchar(255) NOT NULL,
  `description` text,
  `status` varchar(50),
  `created_by` bigint,
  `created_at` timestamp DEFAULT (now()),
  `updated_by` bigint,
  `updated_at` timestamp DEFAULT (now()),
  `deleted_at` timestamp,
  `deleted_by` bigint
);

CREATE UNIQUE INDEX `roles_permission_index_0` ON `roles_permission` (`role_id`, `permission_id`);

CREATE UNIQUE INDEX `user_permission_overrides_index_1` ON `user_permission_overrides` (`user_id`, `permission_id`);

CREATE INDEX `category_index_2` ON `category` (`parent_id`);

CREATE INDEX `category_index_3` ON `category` (`display_order`);

CREATE UNIQUE INDEX `category_translation_index_4` ON `category_translation` (`category_id`, `language_code`);

CREATE UNIQUE INDEX `product_attribute_options_index_5` ON `product_attribute_options` (`product_id`, `attribute_id`, `value_id`);

CREATE INDEX `product_combinations_index_6` ON `product_combinations` (`product_id`);

CREATE INDEX `product_combinations_index_7` ON `product_combinations` (`sku`);

CREATE INDEX `product_combinations_index_8` ON `product_combinations` (`barcode`);

CREATE INDEX `product_combination_items_index_9` ON `product_combination_items` (`combination_id`);

CREATE UNIQUE INDEX `product_combination_items_index_10` ON `product_combination_items` (`combination_id`, `attribute_id`);

CREATE UNIQUE INDEX `product_modifier_links_index_11` ON `product_modifier_links` (`product_id`, `modifier_group_id`);

CREATE UNIQUE INDEX `product_batches_index_12` ON `product_batches` (`product_id`, `batch_no`);

CREATE INDEX `product_batches_index_13` ON `product_batches` (`expiry_date`);

CREATE INDEX `stock_movement_index_14` ON `stock_movement` (`product_id`);

CREATE INDEX `stock_movement_index_15` ON `stock_movement` (`reference_type`);

CREATE INDEX `stock_movement_index_16` ON `stock_movement` (`reference_id`);

CREATE INDEX `supplier_index_17` ON `supplier` (`company_name`);

CREATE INDEX `supplier_index_18` ON `supplier` (`vat_trn_number`);

CREATE INDEX `supplier_index_19` ON `supplier` (`email`);

CREATE UNIQUE INDEX `supplier_translation_index_20` ON `supplier_translation` (`supplier_id`, `language_id`);

CREATE INDEX `supplier_bank_accounts_index_21` ON `supplier_bank_accounts` (`supplier_id`, `account_number`);

CREATE UNIQUE INDEX `supplier_purchase_index_22` ON `supplier_purchase` (`invoice_no`);

CREATE INDEX `supplier_purchase_index_23` ON `supplier_purchase` (`supplier_id`);

CREATE INDEX `supplier_purchase_index_24` ON `supplier_purchase` (`invoice_date`);

CREATE UNIQUE INDEX `product_warranty_index_25` ON `product_warranty` (`product_id`, `warranty_type_id`);

CREATE INDEX `sales_warranty_index_26` ON `sales_warranty` (`sale_id`, `product_id`);

CREATE INDEX `sales_warranty_index_27` ON `sales_warranty` (`end_date`);

CREATE INDEX `product_variants_index_28` ON `product_variants` (`product_id`);

CREATE INDEX `product_variants_index_29` ON `product_variants` (`unit_option_id`);

CREATE INDEX `product_variants_index_30` ON `product_variants` (`barcode`);

CREATE UNIQUE INDEX `shift_user_index_31` ON `shift_user` (`shift_id`, `user_id`);

CREATE INDEX `cash_box_index_32` ON `cash_box` (`shop_location_id`);

CREATE INDEX `cash_box_index_33` ON `cash_box` (`shift_id`);

CREATE INDEX `cash_in_out_index_34` ON `cash_in_out` (`cash_box_id`);

CREATE INDEX `cash_in_out_index_35` ON `cash_in_out` (`payment_id`);

CREATE INDEX `cash_in_out_index_36` ON `cash_in_out` (`created_at`);

CREATE INDEX `shop_locations_index_37` ON `shop_locations` (`shop_id`);

CREATE INDEX `shop_locations_index_38` ON `shop_locations` (`location_type`);

CREATE UNIQUE INDEX `shop_location_translation_ar_index_39` ON `shop_location_translation_ar` (`shop_location_id`, `language_id`);

CREATE UNIQUE INDEX `shop_users_index_40` ON `shop_users` (`shop_id`, `user_id`);

CREATE UNIQUE INDEX `unit_option_conversion_index_41` ON `unit_option_conversion` (`unit_value_from_id`, `unit_value_to_id`);

CREATE INDEX `unit_option_conversion_index_42` ON `unit_option_conversion` (`unit_option_id`);

ALTER TABLE `language` COMMENT = 'Supported languages with full audit tracking';

ALTER TABLE `label_translation` COMMENT = 'Stores translated labels/text for multilingual support with full audit tracking';

ALTER TABLE `roles` COMMENT = 'Defines different user roles in the system';

ALTER TABLE `permissions` COMMENT = 'Individual system permissions';

ALTER TABLE `roles_permission` COMMENT = 'Links roles to their permissions';

ALTER TABLE `user_permission_overrides` COMMENT = 'Temporary permission exceptions for users';

ALTER TABLE `category` COMMENT = 'Product category hierarchy';

ALTER TABLE `category_translation` COMMENT = 'Multilingual category names';

ALTER TABLE `product` COMMENT = 'Core product table with minimal fields';

ALTER TABLE `product_info` COMMENT = 'Extended product information';

ALTER TABLE `product_attributes` COMMENT = 'Defines product attributes like size, color etc.';

ALTER TABLE `product_attribute_values` COMMENT = 'Possible values for each attribute';

ALTER TABLE `product_attribute_options` COMMENT = 'Links products to their attribute options';

ALTER TABLE `product_combinations` COMMENT = 'Predefined combinations of attributes';

ALTER TABLE `product_combination_items` COMMENT = 'Components of each product combination';

ALTER TABLE `product_barcodes` COMMENT = 'Multiple barcode support per product';

ALTER TABLE `product_images` COMMENT = 'Product images with ordering support';

ALTER TABLE `product_modifiers` COMMENT = 'Individual product modifiers/add-ons';

ALTER TABLE `product_modifier_groups` COMMENT = 'Groups of modifiers (e.g., Pizza Toppings)';

ALTER TABLE `product_modifier_group_items` COMMENT = 'Links modifiers to modifier groups';

ALTER TABLE `product_modifier_links` COMMENT = 'Links products to modifier groups';

ALTER TABLE `product_batches` COMMENT = 'Batch tracking for inventory';

ALTER TABLE `stock_movement` COMMENT = 'Detailed stock movement tracking';

ALTER TABLE `stock_transfer` COMMENT = 'Inventory transfers between locations';

ALTER TABLE `stock_transfer_item` COMMENT = 'Line items for stock transfers';

ALTER TABLE `stock_adjustment` COMMENT = 'Records for inventory adjustments';

ALTER TABLE `stock_adjustment_reasons` COMMENT = 'Tracks reasons for inventory adjustments';

ALTER TABLE `stock_adjustment_item` COMMENT = 'Line items for stock adjustments';

ALTER TABLE `stock_ledger` COMMENT = 'Stock movement history ledger';

ALTER TABLE `supplier` COMMENT = 'Supplier master data with complete contact and financial information';

ALTER TABLE `supplier_translation` COMMENT = 'Multilingual supplier information';

ALTER TABLE `supplier_bank_accounts` COMMENT = 'Supplier banking information with multiple account support';

ALTER TABLE `supplier_purchase` COMMENT = 'Supplier purchase orders/invoices';

ALTER TABLE `warranty_type` COMMENT = 'Master table for all warranty types';

ALTER TABLE `product_warranty` COMMENT = 'Links products to their available warranties';

ALTER TABLE `sales_warranty` COMMENT = 'Tracks warranties actually sold to customers';

ALTER TABLE `warranty_claims` COMMENT = 'Tracks customer warranty claims';

ALTER TABLE `product_variants` COMMENT = 'Different packaging/sizing variants of products';

ALTER TABLE `companies` COMMENT = 'Main company information table';

ALTER TABLE `company_owners` COMMENT = 'Company owners/partners information';

ALTER TABLE `company_addresses` COMMENT = 'Physical addresses for companies';

ALTER TABLE `company_bank_accounts` COMMENT = 'Bank account information for companies';

ALTER TABLE `license_info` COMMENT = 'License information for software access';

ALTER TABLE `license_modules` COMMENT = 'Enabled modules for each license';

ALTER TABLE `license_device_usage` COMMENT = 'Track per-device usage for licensing compliance';

ALTER TABLE `shift_types` COMMENT = 'Template shifts for locations';

ALTER TABLE `shifts` COMMENT = 'Actual shift instances with financial tracking';

ALTER TABLE `shift_user` COMMENT = 'Assigns users to specific shifts';

ALTER TABLE `shift_transactions` COMMENT = 'Detailed shift financial transactions';

ALTER TABLE `cash_box` COMMENT = 'Physical cash boxes at each location';

ALTER TABLE `cash_in_out` COMMENT = 'Records all cash movements in/out of boxes';

ALTER TABLE `cash_box_balance` COMMENT = 'Calculated current balance for each cash box';

ALTER TABLE `shop` COMMENT = 'Main shop/store information';

ALTER TABLE `shop_locations` COMMENT = 'Physical locations for shops';

ALTER TABLE `shop_location_translation_ar` COMMENT = 'Arabic translations for location information';

ALTER TABLE `shop_users` COMMENT = 'Assigns users to specific shops';

ALTER TABLE `shop_type` COMMENT = 'Defines different types of shops/stores';

ALTER TABLE `uom` COMMENT = 'Units of measurement with conversion support';

ALTER TABLE `unit_options` COMMENT = 'Master table for all unit types (kg, liter, etc.)';

ALTER TABLE `unit_option_conversion` COMMENT = 'Defines conversion rules between product variants';

ALTER TABLE `activity_logs` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `label_translation` ADD FOREIGN KEY (`language_id`) REFERENCES `language` (`id`);

ALTER TABLE `industry_type_access` ADD FOREIGN KEY (`industry_type_id`) REFERENCES `industry_type` (`id`);

ALTER TABLE `company_settings` ADD FOREIGN KEY (`currency_id`) REFERENCES `currencies` (`id`);

ALTER TABLE `company_settings` ADD FOREIGN KEY (`invoice_default_language_id`) REFERENCES `language` (`id`);

ALTER TABLE `company_settings` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `company_settings` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `company_settings` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `countries` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `countries` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `countries` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `states` ADD FOREIGN KEY (`country_id`) REFERENCES `countries` (`country_id`);

ALTER TABLE `states` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `states` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `states` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `cities` ADD FOREIGN KEY (`state_id`) REFERENCES `states` (`state_id`);

ALTER TABLE `cities` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `cities` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `cities` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `users` ADD FOREIGN KEY (`owner_id`) REFERENCES `owner` (`id`);

ALTER TABLE `users` ADD FOREIGN KEY (`role_permission_id`) REFERENCES `roles_permission` (`role_permission_id`);

ALTER TABLE `users` ADD FOREIGN KEY (`shopid`) REFERENCES `shop` (`shop_id`);

ALTER TABLE `users` ADD FOREIGN KEY (`shift_type_id`) REFERENCES `shift_types` (`shift_type_id`);

ALTER TABLE `roles` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `roles` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `roles` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `permissions` ADD FOREIGN KEY (`parent_permission_id`) REFERENCES `permissions` (`permission_id`);

ALTER TABLE `permissions` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `permissions` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `permissions` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `roles_permission` ADD FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`);

ALTER TABLE `roles_permission` ADD FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`permission_id`);

ALTER TABLE `roles_permission` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `roles_permission` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `roles_permission` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `user_permission_overrides` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `user_permission_overrides` ADD FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`permission_id`);

ALTER TABLE `user_permission_overrides` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `category` ADD FOREIGN KEY (`parent_id`) REFERENCES `category` (`id`);

ALTER TABLE `category` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `category_translation` ADD FOREIGN KEY (`category_id`) REFERENCES `category` (`id`);

ALTER TABLE `product_info` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `product_info` ADD FOREIGN KEY (`category_id`) REFERENCES `category` (`id`);

ALTER TABLE `product_info` ADD FOREIGN KEY (`sub_category_lvl1_id`) REFERENCES `category` (`id`);

ALTER TABLE `product_info` ADD FOREIGN KEY (`sub_category_lvl2_id`) REFERENCES `category` (`id`);

ALTER TABLE `product_info` ADD FOREIGN KEY (`brand_id`) REFERENCES `brand` (`id`);

ALTER TABLE `product_info` ADD FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`);

ALTER TABLE `product_info` ADD FOREIGN KEY (`shop_location_id`) REFERENCES `shop_locations` (`id`);

ALTER TABLE `product_info` ADD FOREIGN KEY (`stock_unit_id`) REFERENCES `uom` (`id`);

ALTER TABLE `product_info` ADD FOREIGN KEY (`purchase_unit_id`) REFERENCES `uom` (`id`);

ALTER TABLE `product_info` ADD FOREIGN KEY (`selling_unit_id`) REFERENCES `uom` (`id`);

ALTER TABLE `product_info` ADD FOREIGN KEY (`warranty_type_id`) REFERENCES `warranty_type` (`id`);

ALTER TABLE `product_quantity_history` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `product_attributes` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_attribute_values` ADD FOREIGN KEY (`attribute_id`) REFERENCES `product_attributes` (`id`);

ALTER TABLE `product_attribute_options` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `product_attribute_options` ADD FOREIGN KEY (`attribute_id`) REFERENCES `product_attributes` (`id`);

ALTER TABLE `product_attribute_options` ADD FOREIGN KEY (`value_id`) REFERENCES `product_attribute_values` (`id`);

ALTER TABLE `product_combinations` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `product_combination_items` ADD FOREIGN KEY (`combination_id`) REFERENCES `product_combinations` (`id`);

ALTER TABLE `product_combination_items` ADD FOREIGN KEY (`attribute_id`) REFERENCES `product_attributes` (`id`);

ALTER TABLE `product_combination_items` ADD FOREIGN KEY (`value_id`) REFERENCES `product_attribute_values` (`id`);

ALTER TABLE `product_barcodes` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `product_images` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `product_modifiers` ADD FOREIGN KEY (`tax_type_id`) REFERENCES `tax_types` (`id`);

ALTER TABLE `product_modifiers` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_modifier_groups` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_modifier_group_items` ADD FOREIGN KEY (`group_id`) REFERENCES `product_modifier_groups` (`id`);

ALTER TABLE `product_modifier_group_items` ADD FOREIGN KEY (`modifier_id`) REFERENCES `product_modifiers` (`id`);

ALTER TABLE `product_modifier_links` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `product_modifier_links` ADD FOREIGN KEY (`modifier_group_id`) REFERENCES `product_modifier_groups` (`id`);

ALTER TABLE `modifiers` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_batches` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `product_batches` ADD FOREIGN KEY (`uom_id`) REFERENCES `uom` (`id`);

ALTER TABLE `stock_movement` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `stock_movement` ADD FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`id`);

ALTER TABLE `stock_movement` ADD FOREIGN KEY (`uom_id`) REFERENCES `uom` (`id`);

ALTER TABLE `stock_movement` ADD FOREIGN KEY (`location_id`) REFERENCES `shop_locations` (`id`);

ALTER TABLE `stock_movement` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `stock_transfer` ADD FOREIGN KEY (`from_store_id`) REFERENCES `shop` (`shop_id`);

ALTER TABLE `stock_transfer` ADD FOREIGN KEY (`to_store_id`) REFERENCES `shop` (`shop_id`);

ALTER TABLE `stock_transfer` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `stock_transfer_item` ADD FOREIGN KEY (`transfer_id`) REFERENCES `stock_transfer` (`transfer_id`);

ALTER TABLE `stock_transfer_item` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `stock_transfer_item` ADD FOREIGN KEY (`uom_id`) REFERENCES `uom` (`id`);

ALTER TABLE `stock_adjustment` ADD FOREIGN KEY (`store_location_id`) REFERENCES `shop` (`shop_id`);

ALTER TABLE `stock_adjustment` ADD FOREIGN KEY (`reason_id`) REFERENCES `stock_adjustment_reasons` (`stock_adjustment_reasons_id`);

ALTER TABLE `stock_adjustment` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `stock_adjustment_item` ADD FOREIGN KEY (`adjustment_id`) REFERENCES `stock_adjustment` (`adjustment_id`);

ALTER TABLE `stock_adjustment_item` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `stock_adjustment_item` ADD FOREIGN KEY (`uom_id`) REFERENCES `uom` (`id`);

ALTER TABLE `stock_ledger` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `stock_ledger` ADD FOREIGN KEY (`unit_id`) REFERENCES `product_unit` (`id`);

ALTER TABLE `supplier` ADD FOREIGN KEY (`shop_id`) REFERENCES `shop` (`shop_id`);

ALTER TABLE `supplier` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `supplier` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `supplier` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `supplier_translation` ADD FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`);

ALTER TABLE `supplier_translation` ADD FOREIGN KEY (`language_id`) REFERENCES `language` (`id`);

ALTER TABLE `supplier_translation` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `supplier_translation` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `supplier_translation` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `supplier_bank_accounts` ADD FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`);

ALTER TABLE `supplier_bank_accounts` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `supplier_bank_accounts` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `supplier_bank_accounts` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `supplier_purchase` ADD FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`);

ALTER TABLE `supplier_purchase` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `supplier_purchase` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `supplier_purchase_item` ADD FOREIGN KEY (`purchase_id`) REFERENCES `supplier_purchase` (`purchase_id`);

ALTER TABLE `supplier_purchase_item` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `supplier_purchase_item` ADD FOREIGN KEY (`product_variant_id`) REFERENCES `product_variants` (`variant_id`);

ALTER TABLE `warranty_type` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `warranty_type` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `warranty_type` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_warranty` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `product_warranty` ADD FOREIGN KEY (`warranty_type_id`) REFERENCES `warranty_type` (`id`);

ALTER TABLE `product_warranty` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_warranty` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `sales_warranty` ADD FOREIGN KEY (`sale_id`) REFERENCES `transactions` (`id`);

ALTER TABLE `sales_warranty` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `sales_warranty` ADD FOREIGN KEY (`warranty_type_id`) REFERENCES `warranty_type` (`id`);

ALTER TABLE `warranty_claims` ADD FOREIGN KEY (`sales_warranty_id`) REFERENCES `sales_warranty` (`id`);

ALTER TABLE `warranty_claims` ADD FOREIGN KEY (`resolved_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_variants` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `product_variants` ADD FOREIGN KEY (`unit_option_id`) REFERENCES `unit_options` (`units_options_id`);

ALTER TABLE `product_variants` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_variants` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_variants` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `companies` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `companies` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `companies` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `company_owners` ADD FOREIGN KEY (`company_id`) REFERENCES `companies` (`company_id`);

ALTER TABLE `company_owners` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `company_owners` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `company_owners` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `company_addresses` ADD FOREIGN KEY (`company_id`) REFERENCES `companies` (`company_id`);

ALTER TABLE `company_addresses` ADD FOREIGN KEY (`state_id`) REFERENCES `states` (`state_id`);

ALTER TABLE `company_addresses` ADD FOREIGN KEY (`country_id`) REFERENCES `countries` (`country_id`);

ALTER TABLE `company_addresses` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `company_addresses` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `company_addresses` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `company_bank_accounts` ADD FOREIGN KEY (`company_id`) REFERENCES `companies` (`company_id`);

ALTER TABLE `company_bank_accounts` ADD FOREIGN KEY (`company_address_id`) REFERENCES `company_addresses` (`company_address_id`);

ALTER TABLE `company_bank_accounts` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `company_bank_accounts` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `company_bank_accounts` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `license_info` ADD FOREIGN KEY (`company_id`) REFERENCES `companies` (`company_id`);

ALTER TABLE `license_info` ADD FOREIGN KEY (`plan_id`) REFERENCES `plan` (`id`);

ALTER TABLE `license_modules` ADD FOREIGN KEY (`license_id`) REFERENCES `license_info` (`id`);

ALTER TABLE `license_device_usage` ADD FOREIGN KEY (`license_id`) REFERENCES `license_info` (`id`);

ALTER TABLE `customer` ADD FOREIGN KEY (`business_type_id`) REFERENCES `business_type_master` (`id`);

ALTER TABLE `customer` ADD FOREIGN KEY (`shop_id`) REFERENCES `shop` (`shop_id`);

ALTER TABLE `customer` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `customer` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `customer` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `business_type_master` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `business_type_master` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `business_type_master` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `customer_address` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `customer_address` ADD FOREIGN KEY (`state_id`) REFERENCES `states` (`state_id`);

ALTER TABLE `customer_address` ADD FOREIGN KEY (`country_id`) REFERENCES `countries` (`country_id`);

ALTER TABLE `customer_address` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `customer_address` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `customer_address` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `customer_address_translation` ADD FOREIGN KEY (`customer_address_id`) REFERENCES `customer_address` (`id`);

ALTER TABLE `customer_address_translation` ADD FOREIGN KEY (`language_id`) REFERENCES `language` (`id`);

ALTER TABLE `customer_address_translation` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `customer_address_translation` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `customer_address_translation` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `customers_groups` ADD FOREIGN KEY (`selling_price_type_id`) REFERENCES `selling_price_types` (`id`);

ALTER TABLE `customers_groups` ADD FOREIGN KEY (`discount_id`) REFERENCES `discounts` (`id`);

ALTER TABLE `customers_groups` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `customers_groups` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `customers_groups` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `discounts` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `discounts` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `discounts` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_discount` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `product_discount` ADD FOREIGN KEY (`discounts_id`) REFERENCES `discounts` (`id`);

ALTER TABLE `product_discount` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_discount` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_discount` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `customers_group_relation` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `customers_group_relation` ADD FOREIGN KEY (`customer_group_id`) REFERENCES `customers_groups` (`id`);

ALTER TABLE `customers_group_relation` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `customers_group_relation` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `customers_group_relation` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `customer_balance` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `customer_balance` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `customer_balance` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `customer_balance` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `transactions` ADD FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`shift_id`);

ALTER TABLE `transactions` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `transactions` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `transactions` ADD FOREIGN KEY (`shop_location_id`) REFERENCES `shop` (`shop_id`);

ALTER TABLE `transactions` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `transactions` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `transactions` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `transaction_products` ADD FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`id`);

ALTER TABLE `transaction_products` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `transaction_products` ADD FOREIGN KEY (`shop_location_id`) REFERENCES `shop` (`shop_id`);

ALTER TABLE `transaction_products` ADD FOREIGN KEY (`product_unit_id`) REFERENCES `unit_options` (`units_options_id`);

ALTER TABLE `transaction_products` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `transaction_products` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `transaction_products` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `transaction_modifiers` ADD FOREIGN KEY (`transaction_product_id`) REFERENCES `transaction_products` (`id`);

ALTER TABLE `transaction_modifiers` ADD FOREIGN KEY (`product_modifier_id`) REFERENCES `modifiers` (`id`);

ALTER TABLE `transaction_modifiers` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `transaction_service_charges` ADD FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`id`);

ALTER TABLE `transaction_service_charges` ADD FOREIGN KEY (`service_charge_id`) REFERENCES `service_charges` (`id`);

ALTER TABLE `transaction_service_charges` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `refund_transactions` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `refund_transactions` ADD FOREIGN KEY (`selling_transaction_id`) REFERENCES `transactions` (`id`);

ALTER TABLE `refund_transactions` ADD FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`shift_id`);

ALTER TABLE `refund_transactions` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `refund_transactions` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `refund_transaction_products` ADD FOREIGN KEY (`refund_transaction_id`) REFERENCES `refund_transactions` (`id`);

ALTER TABLE `refund_transaction_products` ADD FOREIGN KEY (`transaction_product_id`) REFERENCES `transaction_products` (`id`);

ALTER TABLE `refund_transaction_products` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `exchange_transactions` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `exchange_transactions` ADD FOREIGN KEY (`selling_transaction_id`) REFERENCES `transactions` (`id`);

ALTER TABLE `exchange_transactions` ADD FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`shift_id`);

ALTER TABLE `exchange_transactions` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `customer_payments` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `customer_payments` ADD FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`shift_id`);

ALTER TABLE `customer_payments` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `payment_transaction` ADD FOREIGN KEY (`sales_id`) REFERENCES `transactions` (`id`);

ALTER TABLE `payment_transaction` ADD FOREIGN KEY (`payment_method_id`) REFERENCES `Payment_Options` (`id`);

ALTER TABLE `tax_types` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `tax_types` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `tax_types` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_taxes` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `product_taxes` ADD FOREIGN KEY (`tax_type_id`) REFERENCES `tax_types` (`id`);

ALTER TABLE `service_charge_types` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `service_charge_types` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `service_charge_types` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `service_charge_types_translations` ADD FOREIGN KEY (`language_id`) REFERENCES `language` (`id`);

ALTER TABLE `service_charge_types_translations` ADD FOREIGN KEY (`service_charge_type_id`) REFERENCES `service_charge_types` (`id`);

ALTER TABLE `service_charge_types_translations` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `service_charge_types_translations` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `service_charge_types_translations` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `service_charge_options` ADD FOREIGN KEY (`service_charge_type_id`) REFERENCES `service_charge_types` (`id`);

ALTER TABLE `service_charge_options` ADD FOREIGN KEY (`language_id`) REFERENCES `language` (`id`);

ALTER TABLE `service_charge_options` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `service_charge_options` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `service_charge_options` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `service_charge_options_translations` ADD FOREIGN KEY (`language_id`) REFERENCES `language` (`id`);

ALTER TABLE `service_charge_options_translations` ADD FOREIGN KEY (`service_charge_option_id`) REFERENCES `service_charge_options` (`service_charge_option_id`);

ALTER TABLE `service_charge_options_translations` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `service_charge_options_translations` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `service_charge_options_translations` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `shift_types` ADD FOREIGN KEY (`shop_location_id`) REFERENCES `shop_locations` (`id`);

ALTER TABLE `shift_types` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `shift_types` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `shift_types` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `shifts` ADD FOREIGN KEY (`shift_type_id`) REFERENCES `shift_types` (`shift_type_id`);

ALTER TABLE `shifts` ADD FOREIGN KEY (`cashbox_id`) REFERENCES `cash_box` (`cash_box_id`);

ALTER TABLE `shifts` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `shifts` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `shifts` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `shift_user` ADD FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`shift_id`);

ALTER TABLE `shift_user` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `shift_user` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `shift_user` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `shift_user` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `shift_transactions` ADD FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`shift_id`);

ALTER TABLE `shift_transactions` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `cash_box` ADD FOREIGN KEY (`shop_location_id`) REFERENCES `shop_locations` (`id`);

ALTER TABLE `cash_box` ADD FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`shift_id`);

ALTER TABLE `cash_box` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `cash_box` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `cash_box` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `cash_in_out` ADD FOREIGN KEY (`cash_box_id`) REFERENCES `cash_box` (`cash_box_id`);

ALTER TABLE `cash_in_out` ADD FOREIGN KEY (`payment_id`) REFERENCES `Payment_Options` (`id`);

ALTER TABLE `cash_in_out` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `cash_in_out` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `cash_in_out` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `cash_box_balance` ADD FOREIGN KEY (`cash_box_id`) REFERENCES `cash_box` (`cash_box_id`);

ALTER TABLE `Programs` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `Programs` ADD FOREIGN KEY (`Updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `Programs` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Translations` ADD FOREIGN KEY (`Program_id`) REFERENCES `Programs` (`Program_ID`);

ALTER TABLE `Program_Translations` ADD FOREIGN KEY (`language_id`) REFERENCES `language` (`id`);

ALTER TABLE `Program_Translations` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Translations` ADD FOREIGN KEY (`Updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Translations` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Meals` ADD FOREIGN KEY (`Program_ID`) REFERENCES `Programs` (`Program_ID`);

ALTER TABLE `Program_Meals` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Meals` ADD FOREIGN KEY (`Updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Meals` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Products` ADD FOREIGN KEY (`program_id`) REFERENCES `Programs` (`Program_ID`);

ALTER TABLE `Program_Products` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `Program_Products` ADD FOREIGN KEY (`product_unit_id`) REFERENCES `product_unit` (`id`);

ALTER TABLE `Program_Products` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Products` ADD FOREIGN KEY (`Updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Products` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Subscriptions` ADD FOREIGN KEY (`program_id`) REFERENCES `Programs` (`Program_ID`);

ALTER TABLE `Program_Subscriptions` ADD FOREIGN KEY (`Customer_ID`) REFERENCES `customer` (`id`);

ALTER TABLE `Program_Subscriptions` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Subscriptions` ADD FOREIGN KEY (`Updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Subscriptions` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Order_Delivery` ADD FOREIGN KEY (`program_id`) REFERENCES `Programs` (`Program_ID`);

ALTER TABLE `Program_Order_Delivery` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `Program_Order_Delivery` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Order_Delivery` ADD FOREIGN KEY (`Updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `Program_Order_Delivery` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop` ADD FOREIGN KEY (`company_id`) REFERENCES `companies` (`company_id`);

ALTER TABLE `shop` ADD FOREIGN KEY (`industry_type_id`) REFERENCES `industry_type` (`id`);

ALTER TABLE `shop` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop_locations` ADD FOREIGN KEY (`shop_id`) REFERENCES `shop` (`shop_id`);

ALTER TABLE `shop_locations` ADD FOREIGN KEY (`manager_id`) REFERENCES `users` (`id`);

ALTER TABLE `shop_locations` ADD FOREIGN KEY (`state_id`) REFERENCES `states` (`state_id`);

ALTER TABLE `shop_locations` ADD FOREIGN KEY (`country_id`) REFERENCES `countries` (`country_id`);

ALTER TABLE `shop_locations` ADD FOREIGN KEY (`language_id`) REFERENCES `language` (`id`);

ALTER TABLE `shop_locations` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop_locations` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop_locations` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop_location_translation_ar` ADD FOREIGN KEY (`shop_location_id`) REFERENCES `shop_locations` (`id`);

ALTER TABLE `shop_location_translation_ar` ADD FOREIGN KEY (`language_id`) REFERENCES `language` (`id`);

ALTER TABLE `shop_location_translation_ar` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop_location_translation_ar` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop_location_translation_ar` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop_users` ADD FOREIGN KEY (`shop_id`) REFERENCES `shop` (`shop_id`);

ALTER TABLE `shop_users` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `shop_users` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop_users` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop_users` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop_type` ADD FOREIGN KEY (`default_selling_price_type_id`) REFERENCES `selling_price_types` (`id`);

ALTER TABLE `shop_type` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop_type` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `shop_type` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `uom` ADD FOREIGN KEY (`base_uom_id`) REFERENCES `uom` (`id`);

ALTER TABLE `unit_options` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `unit_options` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `unit_options` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `product_unit` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `product_unit` ADD FOREIGN KEY (`parent_unit_id`) REFERENCES `product_unit` (`id`);

ALTER TABLE `unit_prices` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `unit_prices` ADD FOREIGN KEY (`unit_id`) REFERENCES `product_unit` (`id`);

ALTER TABLE `unit_prices` ADD FOREIGN KEY (`selling_price_id`) REFERENCES `selling_price_types` (`id`);

ALTER TABLE `selling_price_types` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `selling_price_types` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `selling_price_types` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `unit_option_conversion` ADD FOREIGN KEY (`unit_option_id`) REFERENCES `unit_options` (`units_options_id`);

ALTER TABLE `unit_option_conversion` ADD FOREIGN KEY (`unit_value_from_id`) REFERENCES `product_variants` (`variant_id`);

ALTER TABLE `unit_option_conversion` ADD FOREIGN KEY (`unit_value_to_id`) REFERENCES `product_variants` (`variant_id`);

ALTER TABLE `unit_option_conversion` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `unit_option_conversion` ADD FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);

ALTER TABLE `unit_option_conversion` ADD FOREIGN KEY (`deleted_by`) REFERENCES `users` (`id`);

ALTER TABLE `reservation` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `reservation` ADD FOREIGN KEY (`payment_method`) REFERENCES `Payment_Options` (`id`);
