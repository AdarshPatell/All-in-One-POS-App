package org.example.newchronopos.dao;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.*;
import org.example.newchronopos.service.BarcodeGeneratorService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ProductDAO {
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final BarcodeGeneratorService barcodeService = new BarcodeGeneratorService();

    // Create a new product with all related data
    public boolean saveProduct(Product product) {
        String productSql = """
            INSERT INTO product (deleted, status, type, created_at, updated_at) 
            VALUES (?, ?, ?, ?, ?)
            """;
        
        String productInfoSql = """
            INSERT INTO product_info (product_id, product_name, product_name_ar, alternate_name, 
            alternate_name_ar, full_description, full_description_ar, short_description, 
            short_description_ar, sku, model_number, created_barcode, has_standard_barcode, 
            category_id, sub_category_lvl1_id, sub_category_lvl2_id, brand_id, product_unit, 
            weight, dimensions, specs_flag, specs, color, reorder_level, store_location, 
            can_return, country_of_origin, supplier_id, shop_location_id, stock_unit_id, 
            purchase_unit_id, selling_unit_id, with_expiry_date, expiry_days, has_warranty, 
            warranty_period, warranty_type_id, price_type, created_at, updated_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Insert into product table
                int productId;
                try (PreparedStatement pstmt = conn.prepareStatement(productSql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, product.getDeleted());
                    pstmt.setString(2, product.getStatus());
                    pstmt.setString(3, product.getType());
                    pstmt.setTimestamp(4, Timestamp.valueOf(product.getCreatedAt()));
                    pstmt.setTimestamp(5, Timestamp.valueOf(product.getUpdatedAt()));
                    
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Creating product failed, no rows affected.");
                    }
                    
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            productId = generatedKeys.getInt(1);
                            product.setId(productId);
                        } else {
                            throw new SQLException("Creating product failed, no ID obtained.");
                        }
                    }
                }

                // Insert into product_info table
                try (PreparedStatement pstmt = conn.prepareStatement(productInfoSql)) {
                    pstmt.setInt(1, productId);
                    pstmt.setString(2, product.getProductName());
                    pstmt.setString(3, product.getProductNameAr());
                    pstmt.setString(4, product.getAlternateName());
                    pstmt.setString(5, product.getAlternateNameAr());
                    pstmt.setString(6, product.getFullDescription());
                    pstmt.setString(7, product.getFullDescriptionAr());
                    pstmt.setString(8, product.getShortDescription());
                    pstmt.setString(9, product.getShortDescriptionAr());
                    pstmt.setString(10, product.getSku());
                    pstmt.setString(11, product.getModelNumber());
                    pstmt.setBoolean(12, product.isCreatedBarcode());
                    pstmt.setBoolean(13, product.isHasStandardBarcode());
                    pstmt.setInt(14, product.getCategoryId());
                    pstmt.setObject(15, product.getSubCategoryLvl1Id() == 0 ? null : product.getSubCategoryLvl1Id());
                    pstmt.setObject(16, product.getSubCategoryLvl2Id() == 0 ? null : product.getSubCategoryLvl2Id());
                    pstmt.setObject(17, product.getBrandId() == 0 ? null : product.getBrandId());
                    pstmt.setString(18, product.getProductUnit());
                    pstmt.setDouble(19, product.getWeight());
                    pstmt.setString(20, product.getDimensions());
                    pstmt.setBoolean(21, product.isSpecsFlag());
                    pstmt.setString(22, product.getSpecs());
                    pstmt.setString(23, product.getColor());
                    pstmt.setInt(24, product.getReorderLevel());
                    pstmt.setString(25, product.getStoreLocation());
                    pstmt.setBoolean(26, product.isCanReturn());
                    pstmt.setString(27, product.getCountryOfOrigin());
                    pstmt.setObject(28, product.getSupplierId() == 0 ? null : product.getSupplierId());
                    pstmt.setObject(29, product.getShopLocationId() == 0 ? null : product.getShopLocationId());
                    pstmt.setObject(30, product.getStockUnitId() == 0 ? null : product.getStockUnitId());
                    pstmt.setObject(31, product.getPurchaseUnitId() == 0 ? null : product.getPurchaseUnitId());
                    pstmt.setObject(32, product.getSellingUnitId() == 0 ? null : product.getSellingUnitId());
                    pstmt.setBoolean(33, product.isWithExpiryDate());
                    pstmt.setObject(34, product.getExpiryDays() == 0 ? null : product.getExpiryDays());
                    pstmt.setBoolean(35, product.isHasWarranty());
                    pstmt.setObject(36, product.getWarrantyPeriod() == 0 ? null : product.getWarrantyPeriod());
                    pstmt.setObject(37, product.getWarrantyTypeId() == 0 ? null : product.getWarrantyTypeId());
                    pstmt.setString(38, product.getPriceType());
                    pstmt.setTimestamp(39, Timestamp.valueOf(product.getCreatedAt()));
                    pstmt.setTimestamp(40, Timestamp.valueOf(product.getUpdatedAt()));
                    
                    pstmt.executeUpdate();
                }

                // Save related data
                if (product.getBarcodes() != null && !product.getBarcodes().isEmpty()) {
                    saveProductBarcodes(conn, productId, product.getBarcodes());
                }
                
                if (product.getAttributes() != null && !product.getAttributes().isEmpty()) {
                    saveProductAttributes(conn, productId, product.getAttributes());
                }
                
                if (product.getPrices() != null && !product.getPrices().isEmpty()) {
                    saveProductPrices(conn, productId, product.getPrices());
                }
                
                if (product.getTaxes() != null && !product.getTaxes().isEmpty()) {
                    saveProductTaxes(conn, productId, product.getTaxes());
                }
                
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    saveProductImages(conn, productId, product.getImages());
                }

                conn.commit();
                return true;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all products with complete information
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.*, pi.* 
            FROM product p 
            LEFT JOIN product_info pi ON p.id = pi.product_id 
            WHERE p.deleted = 0 AND p.status = 'Active'
            ORDER BY pi.product_name
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                
                // Load related data
                product.setBarcodes(getProductBarcodes(product.getId()));
                product.setAttributes(getProductAttributes(product.getId()));
                product.setPrices(getProductPrices(product.getId()));
                product.setTaxes(getProductTaxes(product.getId()));
                product.setImages(getProductImages(product.getId()));
                
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Get product by ID
    public Product getProductById(int id) {
        String sql = """
            SELECT p.*, pi.* 
            FROM product p 
            LEFT JOIN product_info pi ON p.id = pi.product_id 
            WHERE p.id = ? AND p.deleted = 0
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Product product = mapResultSetToProduct(rs);
                    
                    // Load related data
                    product.setBarcodes(getProductBarcodes(product.getId()));
                    product.setAttributes(getProductAttributes(product.getId()));
                    product.setPrices(getProductPrices(product.getId()));
                    product.setTaxes(getProductTaxes(product.getId()));
                    product.setImages(getProductImages(product.getId()));
                    
                    return product;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get products by category
    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.*, pi.* FROM product p 
            LEFT JOIN product_info pi ON p.id = pi.product_id 
            WHERE pi.category_id = ? AND p.deleted = 0 AND p.status = 'Active'
            ORDER BY pi.product_name
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = mapResultSetToProduct(rs);
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Search products by term
    public List<Product> searchProducts(String searchTerm) {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.*, pi.* FROM product p 
            LEFT JOIN product_info pi ON p.id = pi.product_id 
            WHERE (pi.product_name LIKE ? OR pi.sku LIKE ? OR pi.model_number LIKE ?) 
            AND p.deleted = 0 AND p.status = 'Active'
            ORDER BY pi.product_name
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = mapResultSetToProduct(rs);
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Delete product by ID
    public boolean deleteProduct(int productId) {
        String sql = "UPDATE product SET deleted = 1, status = 'Inactive', updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to map ResultSet to Product
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        
        // Product table fields
        product.setId(rs.getInt("id"));
        product.setDeleted(rs.getInt("deleted"));
        product.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        product.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        product.setStatus(rs.getString("status"));
        product.setType(rs.getString("type"));
        
        // Product info fields (if available)
        if (rs.getString("product_name") != null) {
            product.setProductName(rs.getString("product_name"));
            product.setProductNameAr(rs.getString("product_name_ar"));
            product.setAlternateName(rs.getString("alternate_name"));
            product.setAlternateNameAr(rs.getString("alternate_name_ar"));
            product.setFullDescription(rs.getString("full_description"));
            product.setFullDescriptionAr(rs.getString("full_description_ar"));
            product.setShortDescription(rs.getString("short_description"));
            product.setShortDescriptionAr(rs.getString("short_description_ar"));
            product.setSku(rs.getString("sku"));
            product.setModelNumber(rs.getString("model_number"));
            product.setCreatedBarcode(rs.getBoolean("created_barcode"));
            product.setHasStandardBarcode(rs.getBoolean("has_standard_barcode"));
            product.setCategoryId(rs.getInt("category_id"));
            product.setSubCategoryLvl1Id(rs.getInt("sub_category_lvl1_id"));
            product.setSubCategoryLvl2Id(rs.getInt("sub_category_lvl2_id"));
            product.setBrandId(rs.getInt("brand_id"));
            product.setProductUnit(rs.getString("product_unit"));
            product.setWeight(rs.getDouble("weight"));
            product.setDimensions(rs.getString("dimensions"));
            product.setSpecsFlag(rs.getBoolean("specs_flag"));
            product.setSpecs(rs.getString("specs"));
            product.setColor(rs.getString("color"));
            product.setReorderLevel(rs.getInt("reorder_level"));
            product.setStoreLocation(rs.getString("store_location"));
            product.setCanReturn(rs.getBoolean("can_return"));
            product.setCountryOfOrigin(rs.getString("country_of_origin"));
            product.setSupplierId(rs.getInt("supplier_id"));
            product.setShopLocationId(rs.getInt("shop_location_id"));
            product.setStockUnitId(rs.getInt("stock_unit_id"));
            product.setPurchaseUnitId(rs.getInt("purchase_unit_id"));
            product.setSellingUnitId(rs.getInt("selling_unit_id"));
            product.setWithExpiryDate(rs.getBoolean("with_expiry_date"));
            product.setExpiryDays(rs.getInt("expiry_days"));
            product.setHasWarranty(rs.getBoolean("has_warranty"));
            product.setWarrantyPeriod(rs.getInt("warranty_period"));
            product.setWarrantyTypeId(rs.getInt("warranty_type_id"));
            product.setPriceType(rs.getString("price_type"));
        }
        
        return product;
    }

    // Helper methods for related data
    private List<ProductBarcode> getProductBarcodes(int productId) {
        List<ProductBarcode> barcodes = new ArrayList<>();
        String sql = "SELECT * FROM product_barcodes WHERE product_id = ? AND status = 'Active'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProductBarcode barcode = new ProductBarcode();
                    barcode.setId(rs.getInt("id"));
                    barcode.setProductId(rs.getInt("product_id"));
                    barcode.setName(rs.getString("name"));
                    barcode.setBarcode(rs.getString("barcode"));
                    barcode.setStandard(rs.getBoolean("is_standard"));
                    barcode.setDefault(rs.getBoolean("is_default"));
                    barcode.setStatus(rs.getString("status"));
                    barcode.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    barcodes.add(barcode);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return barcodes;
    }

    private List<ProductAttribute> getProductAttributes(int productId) {
        // Similar implementation for attributes...
        return new ArrayList<>();
    }

    private List<ProductPrice> getProductPrices(int productId) {
        // Similar implementation for prices...
        return new ArrayList<>();
    }

    private List<ProductTax> getProductTaxes(int productId) {
        // Similar implementation for taxes...
        return new ArrayList<>();
    }

    private List<ProductImage> getProductImages(int productId) {
        // Similar implementation for images...
        return new ArrayList<>();
    }

    private void saveProductBarcodes(Connection conn, int productId, List<ProductBarcode> barcodes) throws SQLException {
        String sql = "INSERT INTO product_barcodes (product_id, name, barcode, is_standard, is_default, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (ProductBarcode barcode : barcodes) {
                pstmt.setInt(1, productId);
                pstmt.setString(2, barcode.getName());
                pstmt.setString(3, barcode.getBarcode());
                pstmt.setBoolean(4, barcode.isStandard());
                pstmt.setBoolean(5, barcode.isDefault());
                pstmt.setString(6, barcode.getStatus());
                pstmt.setTimestamp(7, Timestamp.valueOf(barcode.getCreatedAt()));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void saveProductAttributes(Connection conn, int productId, List<ProductAttribute> attributes) throws SQLException {
        // Similar implementation for attributes...
    }

    private void saveProductPrices(Connection conn, int productId, List<ProductPrice> prices) throws SQLException {
        // Similar implementation for prices...
    }

    private void saveProductTaxes(Connection conn, int productId, List<ProductTax> taxes) throws SQLException {
        // Similar implementation for taxes...
    }

    private void saveProductImages(Connection conn, int productId, List<ProductImage> images) throws SQLException {
        // Similar implementation for images...
    }

    // Generate SKU automatically
    public String generateSKU(String productName, int categoryId) {
        String prefix = productName.substring(0, Math.min(3, productName.length())).toUpperCase();
        String categoryCode = String.format("%02d", categoryId);
        
        // Get next sequence number
        String sql = "SELECT COUNT(*) FROM product_info WHERE sku LIKE ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, prefix + categoryCode + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1) + 1;
                    return prefix + categoryCode + String.format("%04d", count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return prefix + categoryCode + "0001";
    }
}
