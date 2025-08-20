package org.example.newchronopos.dao;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.Unit;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UnitDAO {

    public List<Unit> getAllUnits() {
        List<Unit> units = new ArrayList<>();
        String sql = "SELECT * FROM units WHERE status = 'Active' ORDER BY unit_name";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                units.add(mapResultSetToUnit(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching all units", e);
        }
        return units;
    }

    public Optional<Unit> getUnitById(int id) {
        String sql = "SELECT * FROM units WHERE unit_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUnit(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching unit by ID", e);
        }
        return Optional.empty();
    }

    public boolean addUnit(Unit unit) {
        String sql = "INSERT INTO units (unit_name, abbreviation, description, status, created_at) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, unit.getName());
            ps.setString(2, unit.getAbbreviation());
            ps.setString(3, unit.getDescription());
            ps.setString(4, unit.getStatus() != null ? unit.getStatus() : "Active");
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while adding unit", e);
        }
    }

    public boolean updateUnit(Unit unit) {
        String sql = "UPDATE units SET unit_name = ?, abbreviation = ?, description = ?, status = ?, updated_at = ? WHERE unit_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, unit.getName());
            ps.setString(2, unit.getAbbreviation());
            ps.setString(3, unit.getDescription());
            ps.setString(4, unit.getStatus());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(6, unit.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while updating unit", e);
        }
    }

    public boolean deleteUnit(int id) {
        String sql = "UPDATE units SET status = 'Inactive', deleted_at = ? WHERE unit_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while deleting unit", e);
        }
    }

    public List<Unit> searchUnits(String searchTerm) {
        List<Unit> units = new ArrayList<>();
        String sql = "SELECT * FROM units WHERE (unit_name LIKE ? OR abbreviation LIKE ? OR description LIKE ?) AND status = 'Active' ORDER BY unit_name";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                units.add(mapResultSetToUnit(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while searching units", e);
        }
        return units;
    }

    private Unit mapResultSetToUnit(ResultSet rs) throws SQLException {
        Unit unit = new Unit();
        unit.setId(rs.getInt("unit_id"));
        unit.setName(rs.getString("unit_name"));
        unit.setAbbreviation(rs.getString("abbreviation"));
        unit.setDescription(rs.getString("description"));
        unit.setStatus(rs.getString("status"));
        unit.setCreatedAt(rs.getTimestamp("created_at") != null ?
            rs.getTimestamp("created_at").toLocalDateTime() : null);
        unit.setUpdatedAt(rs.getTimestamp("updated_at") != null ?
            rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return unit;
    }
}
