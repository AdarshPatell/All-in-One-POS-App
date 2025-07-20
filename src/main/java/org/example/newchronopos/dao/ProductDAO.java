package org.example.newchronopos.dao;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.Product; // Assuming a model with constructor Product(id, name, price)

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.newchronopos.model.Category;


public class ProductDAO {
    private final CategoryDAO categoryDAO = new CategoryDAO();

    // Product CRUD operations
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.id, pi.* FROM product p JOIN product_info pi ON p.id = pi.product_id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getString("sku"),
                        rs.getInt("category_id"),
                        rs.getString("brand"),
                        rs.getString("purchase_unit"),
                        rs.getString("selling_unit"),
                        rs.getString("supplier"),
                        rs.getString("product_group"),
                        rs.getInt("reorder_level"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );

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
            throw new RuntimeException("Database error while fetching all products", e);
        }
        return products;
    }

    public Optional<Product> getProductById(int id) {
        String sql = "SELECT p.id, pi.* FROM product p JOIN product_info pi ON p.id = pi.product_id WHERE p.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getString("sku"),
                        rs.getInt("category_id"),
                        rs.getString("brand"),
                        rs.getString("purchase_unit"),
                        rs.getString("selling_unit"),
                        rs.getString("supplier"),
                        rs.getString("product_group"),
                        rs.getInt("reorder_level"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );

                // Load related data
                product.setBarcodes(getProductBarcodes(product.getId()));
                product.setAttributes(getProductAttributes(product.getId()));
                product.setPrices(getProductPrices(product.getId()));
                product.setTaxes(getProductTaxes(product.getId()));
                product.setImages(getProductImages(product.getId()));

                return Optional.of(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while finding product by ID", e);
        }
        return Optional.empty();
    }

    public int createProduct(Product product) {
        String productSql = "INSERT INTO product DEFAULT VALUES";
        String productInfoSql = "INSERT INTO product_info (product_id, product_name, sku, category_id, brand, " +
                "purchase_unit, selling_unit, supplier, product_group, reorder_level, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement productPs = conn.prepareStatement(productSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement productInfoPs = conn.prepareStatement(productInfoSql)) {

                // Insert into product table
                productPs.executeUpdate();
                ResultSet rs = productPs.getGeneratedKeys();
                int productId = 0;
                if (rs.next()) {
                    productId = rs.getInt(1);
                }

                // Insert into product_info table
                productInfoPs.setInt(1, productId);
                productInfoPs.setString(2, product.getProductName());
                productInfoPs.setString(3, product.getSku());
                productInfoPs.setInt(4, product.getCategoryId());
                productInfoPs.setString(5, product.getBrand());
                productInfoPs.setString(6, product.getPurchaseUnit());
                productInfoPs.setString(7, product.getSellingUnit());
                productInfoPs.setString(8, product.getSupplier());
                productInfoPs.setString(9, product.getProductGroup());
                productInfoPs.setInt(10, product.getReorderLevel());
                productInfoPs.setString(11, product.getDescription());
                productInfoPs.executeUpdate();

                // Insert related data if exists
                if (product.getBarcodes() != null && !product.getBarcodes().isEmpty()) {
                    insertBarcodes(conn, productId, product.getBarcodes());
                }
                if (product.getAttributes() != null && !product.getAttributes().isEmpty()) {
                    insertAttributes(conn, productId, product.getAttributes());
                }
                if (product.getPrices() != null && !product.getPrices().isEmpty()) {
                    insertPrices(conn, productId, product.getPrices());
                }
                if (product.getTaxes() != null && !product.getTaxes().isEmpty()) {
                    insertTaxes(conn, productId, product.getTaxes());
                }
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    insertImages(conn, productId, product.getImages());
                }

                conn.commit();
                return productId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while creating product", e);
        }
    }

    public void updateProduct(Product product) {
        String productInfoSql = "UPDATE product_info SET product_name=?, sku=?, category_id=?, brand=?, " +
                "purchase_unit=?, selling_unit=?, supplier=?, product_group=?, reorder_level=?, description=? " +
                "WHERE product_id=?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(productInfoSql)) {
                ps.setString(1, product.getProductName());
                ps.setString(2, product.getSku());
                ps.setInt(3, product.getCategoryId());
                ps.setString(4, product.getBrand());
                ps.setString(5, product.getPurchaseUnit());
                ps.setString(6, product.getSellingUnit());
                ps.setString(7, product.getSupplier());
                ps.setString(8, product.getProductGroup());
                ps.setInt(9, product.getReorderLevel());
                ps.setString(10, product.getDescription());
                ps.setInt(11, product.getId());
                ps.executeUpdate();

                // Delete and re-insert related data
                deleteRelatedData(conn, product.getId());

                if (product.getBarcodes() != null && !product.getBarcodes().isEmpty()) {
                    insertBarcodes(conn, product.getId(), product.getBarcodes());
                }
                if (product.getAttributes() != null && !product.getAttributes().isEmpty()) {
                    insertAttributes(conn, product.getId(), product.getAttributes());
                }
                if (product.getPrices() != null && !product.getPrices().isEmpty()) {
                    insertPrices(conn, product.getId(), product.getPrices());
                }
                if (product.getTaxes() != null && !product.getTaxes().isEmpty()) {
                    insertTaxes(conn, product.getId(), product.getTaxes());
                }
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    insertImages(conn, product.getId(), product.getImages());
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while updating product", e);
        }
    }

    public void deleteProduct(int id) {
        String sql = "DELETE FROM product WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Delete related data first
                deleteRelatedData(conn, id);

                // Delete from product_info
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM product_info WHERE product_id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                // Delete from product
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while deleting product", e);
        }
    }

    // Helper methods for related data
    private List<Product.Barcode> getProductBarcodes(int productId) throws SQLException {
        List<Product.Barcode> barcodes = new ArrayList<>();
        String sql = "SELECT * FROM product_barcodes WHERE product_id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                barcodes.add(new Product.Barcode(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("barcode"),
                        rs.getBoolean("is_default"),
                        rs.getBoolean("is_standard"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return barcodes;
    }

    private List<Product.ProductAttribute> getProductAttributes(int productId) throws SQLException {
        List<Product.ProductAttribute> attributes = new ArrayList<>();
        String sql = "SELECT * FROM product_attributes WHERE product_id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                attributes.add(new Product.ProductAttribute(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("attribute_name"),
                        rs.getString("attribute_value"), // Fixed: changed from "value" to "attribute_value"
                        rs.getString("arabic_value"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return attributes;
    }

    private List<Product.UnitPrice> getProductPrices(int productId) throws SQLException {
        List<Product.UnitPrice> prices = new ArrayList<>();
        String sql = "SELECT * FROM unit_prices WHERE product_id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                prices.add(new Product.UnitPrice(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("price_type"),
                        rs.getString("unit_option"),
                        rs.getDouble("cost"),
                        rs.getDouble("price"),
                        rs.getString("color"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return prices;
    }

    private List<Product.ProductTax> getProductTaxes(int productId) throws SQLException {
        List<Product.ProductTax> taxes = new ArrayList<>();
        String sql = "SELECT * FROM product_taxes WHERE product_id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                taxes.add(new Product.ProductTax(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("tax_type"),
                        rs.getBoolean("applied_to_selling"),
                        rs.getBoolean("applied_to_buying"),
                        rs.getBoolean("include_in_price"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return taxes;
    }

    private List<Product.ProductImage> getProductImages(int productId) throws SQLException {
        List<Product.ProductImage> images = new ArrayList<>();
        String sql = "SELECT * FROM product_images WHERE product_id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                images.add(new Product.ProductImage(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("image_url"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return images;
    }

    private void insertBarcodes(Connection conn, int productId, List<Product.Barcode> barcodes) throws SQLException {
        String sql = "INSERT INTO product_barcodes (product_id, name, barcode, is_default, is_standard) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Product.Barcode barcode : barcodes) {
                ps.setInt(1, productId);
                ps.setString(2, barcode.getName());
                ps.setString(3, barcode.getBarcode());
                ps.setBoolean(4, barcode.isDefault());
                ps.setBoolean(5, barcode.isStandard());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertAttributes(Connection conn, int productId, List<Product.ProductAttribute> attributes) throws SQLException {
        String sql = "INSERT INTO product_attributes (product_id, attribute_name, attribute_value, arabic_value) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Product.ProductAttribute attr : attributes) {
                ps.setInt(1, productId);
                ps.setString(2, attr.getAttributeName());
                ps.setString(3, attr.getValue());
                ps.setString(4, attr.getArabicValue());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertPrices(Connection conn, int productId, List<Product.UnitPrice> prices) throws SQLException {
        String sql = "INSERT INTO unit_prices (product_id, price_type, unit_option, cost, price, color) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Product.UnitPrice price : prices) {
                ps.setInt(1, productId);
                ps.setString(2, price.getPriceType());
                ps.setString(3, price.getUnitOption());
                ps.setDouble(4, price.getCost());
                ps.setDouble(5, price.getPrice());
                ps.setString(6, price.getColor());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertTaxes(Connection conn, int productId, List<Product.ProductTax> taxes) throws SQLException {
        String sql = "INSERT INTO product_taxes (product_id, tax_type, applied_to_selling, applied_to_buying, include_in_price) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Product.ProductTax tax : taxes) {
                ps.setInt(1, productId);
                ps.setString(2, tax.getTaxType());
                ps.setBoolean(3, tax.isAppliedToSelling());
                ps.setBoolean(4, tax.isAppliedToBuying());
                ps.setBoolean(5, tax.isIncludeInPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertImages(Connection conn, int productId, List<Product.ProductImage> images) throws SQLException {
        String sql = "INSERT INTO product_images (product_id, image_url) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Product.ProductImage image : images) {
                ps.setInt(1, productId);
                ps.setString(2, image.getImageUrl());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteRelatedData(Connection conn, int productId) throws SQLException {
        String[] tables = {
                "product_barcodes", "product_attributes", "unit_prices",
                "product_taxes", "product_images"
        };

        for (String table : tables) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + " WHERE product_id=?")) {
                ps.setInt(1, productId);
                ps.executeUpdate();
            }
        }
    }

    // Category methods
    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

    public Optional<Category> getCategoryById(int id) {
        return categoryDAO.getCategoryById(id);
    }

    public int createCategory(Category category) {
        return categoryDAO.createCategory(category);
    }

    public void updateCategory(Category category) {
        categoryDAO.updateCategory(category);
    }

    public void deleteCategory(int id) {
        categoryDAO.deleteCategory(id);
    }
}

