package org.example.newchronopos.dao;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.Category;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDAO {

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE status = 'Active'";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                category.setParentId(rs.getObject("parent_id", Integer.class));
                category.setImageUrl(rs.getString("image_url"));
                category.setDisplayOrder(rs.getInt("display_order"));
                category.setStatus(rs.getString("status"));
                category.setCreatedBy(rs.getObject("created_by", Integer.class));
                category.setCreatedAt(rs.getTimestamp("created_at") != null ?
                    rs.getTimestamp("created_at").toLocalDateTime() : null);
                category.setUpdatedAt(rs.getTimestamp("updated_at") != null ?
                    rs.getTimestamp("updated_at").toLocalDateTime() : null);

                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching all categories", e);
        }
        return categories;
    }

    public Optional<Category> getCategoryById(int id) {
        String sql = "SELECT * FROM category WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                category.setParentId(rs.getObject("parent_id", Integer.class));
                category.setImageUrl(rs.getString("image_url"));
                category.setDisplayOrder(rs.getInt("display_order"));
                category.setStatus(rs.getString("status"));
                category.setCreatedBy(rs.getObject("created_by", Integer.class));
                category.setCreatedAt(rs.getTimestamp("created_at") != null ?
                    rs.getTimestamp("created_at").toLocalDateTime() : null);
                category.setUpdatedAt(rs.getTimestamp("updated_at") != null ?
                    rs.getTimestamp("updated_at").toLocalDateTime() : null);

                return Optional.of(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while finding category by ID", e);
        }
        return Optional.empty();
    }

    public boolean addCategory(Category category) {
        String sql = "INSERT INTO category (name, description, parent_id, image_url, status, created_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setObject(3, category.getParentId());
            ps.setString(4, category.getImageUrl());
            ps.setString(5, category.getStatus() != null ? category.getStatus() : "Active");

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        category.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while adding category", e);
        }
    }

    public boolean updateCategory(Category category) {
        String sql = "UPDATE category SET name=?, description=?, parent_id=?, image_url=?, " +
                    "status=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setObject(3, category.getParentId());
            ps.setString(4, category.getImageUrl());
            ps.setString(5, category.getStatus());
            ps.setInt(6, category.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while updating category", e);
        }
    }

    public boolean deleteCategory(int id) {
        String sql = "UPDATE category SET status='Inactive' WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while deleting category", e);
        }
    }

    public List<Category> getParentCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE parent_id IS NULL AND status = 'Active'";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                category.setParentId(rs.getObject("parent_id", Integer.class));
                category.setImageUrl(rs.getString("image_url"));
                category.setDisplayOrder(rs.getInt("display_order"));
                category.setStatus(rs.getString("status"));
                category.setCreatedBy(rs.getObject("created_by", Integer.class));
                category.setCreatedAt(rs.getTimestamp("created_at") != null ?
                    rs.getTimestamp("created_at").toLocalDateTime() : null);
                category.setUpdatedAt(rs.getTimestamp("updated_at") != null ?
                    rs.getTimestamp("updated_at").toLocalDateTime() : null);

                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching parent categories", e);
        }
        return categories;
    }

    public List<Category> searchCategories(String searchTerm) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE (name LIKE ? OR description LIKE ?) AND status = 'Active'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                    category.setDescription(rs.getString("description"));
                    category.setParentId(rs.getObject("parent_id", Integer.class));
                    category.setImageUrl(rs.getString("image_url"));
                    category.setDisplayOrder(rs.getInt("display_order"));
                    category.setStatus(rs.getString("status"));
                    category.setCreatedBy(rs.getObject("created_by", Integer.class));
                    category.setCreatedAt(rs.getTimestamp("created_at") != null ?
                        rs.getTimestamp("created_at").toLocalDateTime() : null);
                    category.setUpdatedAt(rs.getTimestamp("updated_at") != null ?
                        rs.getTimestamp("updated_at").toLocalDateTime() : null);

                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while searching categories", e);
        }
        return categories;
    }
}