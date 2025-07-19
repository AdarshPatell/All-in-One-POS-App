package org.example.newchronopos.dao;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.Product; // Assuming a model with constructor Product(id, name, price)

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO {

    /**
     * Retrieves all products from the database.
     * @return A list of all products. Returns an empty list if no products are found.
     */
    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("itemId"),
                        rs.getInt("stock"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getString("photoPath"),
                        rs.getString("availability")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching all products", e);
        }
        return products;
    }

    /**
     * Finds a single product by its ID.
     * @param id The ID of the product to find.
     * @return An Optional containing the Product if found, otherwise an empty Optional.
     */
    public Optional<Product> findById(int id) {
        String sql = "SELECT * FROM product WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("itemId"),
                        rs.getInt("stock"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getString("photoPath"),
                        rs.getString("availability")
                );
                return Optional.of(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while finding product by ID", e);
        }
        return Optional.empty();
    }

    /**
     * Inserts a new product into the database.
     * @param product The Product object to insert. The ID is ignored as it's auto-generated.
     */
    public void insert(Product product) {
        String sql = "INSERT INTO product(name, description, itemId, stock, category, price, photoPath, availability) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setString(3, product.getItemId());
            ps.setInt(4, product.getStock());
            ps.setString(5, product.getCategory());
            ps.setDouble(6, product.getPrice());
            ps.setString(7, product.getPhotoPath());
            ps.setString(8, product.getAvailability());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while inserting product", e);
        }
    }

    /**
     * Updates an existing product in the database.
     * @param product The Product object containing updated values. Must include the ID of the product to update.
     */
    public void update(Product product) {
        String sql = "UPDATE product SET name=?, description=?, itemId=?, stock=?, category=?, price=?, photoPath=?, availability=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setString(3, product.getItemId());
            ps.setInt(4, product.getStock());
            ps.setString(5, product.getCategory());
            ps.setDouble(6, product.getPrice());
            ps.setString(7, product.getPhotoPath());
            ps.setString(8, product.getAvailability());
            ps.setInt(9, product.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while updating product", e);
        }
    }

    /**
     * Deletes a product from the database.
     * @param id The ID of the product to delete.
     */
    public void delete(int id) {
        String sql = "DELETE FROM product WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while deleting product", e);
        }
    }
}