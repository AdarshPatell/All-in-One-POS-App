package org.example.newchronopos.dao;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.Supplier;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupplierDAO {

    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers WHERE status = 'Active' ORDER BY supplier_name";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching all suppliers", e);
        }
        return suppliers;
    }

    public Optional<Supplier> getSupplierById(int id) {
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToSupplier(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching supplier by ID", e);
        }
        return Optional.empty();
    }

    public boolean addSupplier(Supplier supplier) {
        String sql = "INSERT INTO suppliers (supplier_name, contact_person, email, phone, address, city, state, country, postal_code, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContactPerson());
            ps.setString(3, supplier.getEmail());
            ps.setString(4, supplier.getPhone());
            ps.setString(5, supplier.getAddress());
            ps.setString(6, supplier.getCity());
            ps.setString(7, supplier.getState());
            ps.setString(8, supplier.getCountry());
            ps.setString(9, supplier.getPostalCode());
            ps.setString(10, supplier.getStatus() != null ? supplier.getStatus() : "Active");
            ps.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while adding supplier", e);
        }
    }

    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE suppliers SET supplier_name = ?, contact_person = ?, email = ?, phone = ?, address = ?, city = ?, state = ?, country = ?, postal_code = ?, status = ?, updated_at = ? WHERE supplier_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContactPerson());
            ps.setString(3, supplier.getEmail());
            ps.setString(4, supplier.getPhone());
            ps.setString(5, supplier.getAddress());
            ps.setString(6, supplier.getCity());
            ps.setString(7, supplier.getState());
            ps.setString(8, supplier.getCountry());
            ps.setString(9, supplier.getPostalCode());
            ps.setString(10, supplier.getStatus());
            ps.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(12, supplier.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while updating supplier", e);
        }
    }

    public boolean deleteSupplier(int id) {
        String sql = "UPDATE suppliers SET status = 'Inactive', deleted_at = ? WHERE supplier_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while deleting supplier", e);
        }
    }

    public List<Supplier> searchSuppliers(String searchTerm) {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers WHERE (supplier_name LIKE ? OR contact_person LIKE ? OR email LIKE ?) AND status = 'Active' ORDER BY supplier_name";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while searching suppliers", e);
        }
        return suppliers;
    }

    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setId(rs.getInt("supplier_id"));
        supplier.setName(rs.getString("supplier_name"));
        supplier.setContactPerson(rs.getString("contact_person"));
        supplier.setEmail(rs.getString("email"));
        supplier.setPhone(rs.getString("phone"));
        supplier.setAddress(rs.getString("address"));
        supplier.setCity(rs.getString("city"));
        supplier.setState(rs.getString("state"));
        supplier.setCountry(rs.getString("country"));
        supplier.setPostalCode(rs.getString("postal_code"));
        supplier.setStatus(rs.getString("status"));
        supplier.setCreatedAt(rs.getTimestamp("created_at") != null ?
            rs.getTimestamp("created_at").toLocalDateTime() : null);
        supplier.setUpdatedAt(rs.getTimestamp("updated_at") != null ?
            rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return supplier;
    }
}
