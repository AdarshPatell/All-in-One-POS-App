package org.example.newchronopos.dao;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.Brand;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BrandDAO {

    public List<Brand> getAllBrands() {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT * FROM brands WHERE status = 'Active' ORDER BY brand_name";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                brands.add(mapResultSetToBrand(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching all brands", e);
        }
        return brands;
    }

    public Optional<Brand> getBrandById(int id) {
        String sql = "SELECT * FROM brands WHERE brand_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToBrand(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching brand by ID", e);
        }
        return Optional.empty();
    }

    public boolean addBrand(Brand brand) {
        String sql = "INSERT INTO brands (brand_name, description, logo_url, website, status, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, brand.getName());
            ps.setString(2, brand.getDescription());
            ps.setString(3, brand.getLogoUrl());
            ps.setString(4, brand.getWebsite());
            ps.setString(5, brand.getStatus() != null ? brand.getStatus() : "Active");
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while adding brand", e);
        }
    }

    public boolean updateBrand(Brand brand) {
        String sql = "UPDATE brands SET brand_name = ?, description = ?, logo_url = ?, website = ?, status = ?, updated_at = ? WHERE brand_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, brand.getName());
            ps.setString(2, brand.getDescription());
            ps.setString(3, brand.getLogoUrl());
            ps.setString(4, brand.getWebsite());
            ps.setString(5, brand.getStatus());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(7, brand.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while updating brand", e);
        }
    }

    public boolean deleteBrand(int id) {
        String sql = "UPDATE brands SET status = 'Inactive', deleted_at = ? WHERE brand_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while deleting brand", e);
        }
    }

    public List<Brand> searchBrands(String searchTerm) {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT * FROM brands WHERE (brand_name LIKE ? OR description LIKE ?) AND status = 'Active' ORDER BY brand_name";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                brands.add(mapResultSetToBrand(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while searching brands", e);
        }
        return brands;
    }

    private Brand mapResultSetToBrand(ResultSet rs) throws SQLException {
        Brand brand = new Brand();
        brand.setId(rs.getInt("brand_id"));
        brand.setName(rs.getString("brand_name"));
        brand.setDescription(rs.getString("description"));
        brand.setLogoUrl(rs.getString("logo_url"));
        brand.setWebsite(rs.getString("website"));
        brand.setStatus(rs.getString("status"));
        brand.setCreatedAt(rs.getTimestamp("created_at") != null ?
            rs.getTimestamp("created_at").toLocalDateTime() : null);
        brand.setUpdatedAt(rs.getTimestamp("updated_at") != null ?
            rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return brand;
    }
}
