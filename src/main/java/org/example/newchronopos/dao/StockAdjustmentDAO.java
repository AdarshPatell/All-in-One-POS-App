package org.example.newchronopos.dao;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.StockAdjustment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockAdjustmentDAO {

    public boolean createStockAdjustment(StockAdjustment adjustment) {
        String sql = """
            INSERT INTO stock_adjustment (adjustment_no, adjustment_date, store_location_id, 
            reason_id, status, remarks, created_by) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, adjustment.getAdjustmentNo());
            pstmt.setDate(2, Date.valueOf(adjustment.getAdjustmentDate()));
            pstmt.setInt(3, adjustment.getStoreLocationId());
            pstmt.setInt(4, adjustment.getReasonId());
            pstmt.setString(5, adjustment.getStatus());
            pstmt.setString(6, adjustment.getRemarks());
            pstmt.setInt(7, adjustment.getCreatedBy());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        adjustment.setAdjustmentId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<StockAdjustment> getAllStockAdjustments() {
        List<StockAdjustment> adjustments = new ArrayList<>();
        String sql = "SELECT * FROM stock_adjustment ORDER BY created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                StockAdjustment adjustment = mapResultSetToStockAdjustment(rs);
                adjustments.add(adjustment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adjustments;
    }

    public List<StockAdjustment> getAllAdjustments() {
        return getAllStockAdjustments();
    }

    public List<StockAdjustment> getAdjustmentsByCategory(int categoryId) {
        List<StockAdjustment> adjustments = new ArrayList<>();
        String sql = """
            SELECT sa.*, p.category_id FROM stock_adjustment sa
            LEFT JOIN product_info p ON sa.product_id = p.product_id
            WHERE p.category_id = ? 
            ORDER BY sa.created_at DESC
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    StockAdjustment adjustment = mapResultSetToStockAdjustment(rs);
                    adjustments.add(adjustment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adjustments;
    }

    public List<StockAdjustment> searchAdjustments(String searchTerm) {
        List<StockAdjustment> adjustments = new ArrayList<>();
        String sql = """
            SELECT sa.*, p.product_name FROM stock_adjustment sa
            LEFT JOIN product_info p ON sa.product_id = p.product_id
            WHERE sa.adjustment_no LIKE ? OR p.product_name LIKE ? OR sa.reason LIKE ?
            ORDER BY sa.created_at DESC
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    StockAdjustment adjustment = mapResultSetToStockAdjustment(rs);
                    adjustments.add(adjustment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adjustments;
    }

    public boolean addAdjustment(StockAdjustment adjustment) {
        String sql = """
            INSERT INTO stock_adjustment (adjustment_no, product_id, adjustment_quantity, 
            adjustment_type, reason, adjustment_date, created_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, adjustment.getAdjustmentNo());
            pstmt.setInt(2, adjustment.getProductId());
            pstmt.setInt(3, adjustment.getAdjustmentQuantity());
            pstmt.setString(4, adjustment.getAdjustmentType());
            pstmt.setString(5, adjustment.getReason());
            pstmt.setDate(6, Date.valueOf(adjustment.getAdjustmentDate()));
            pstmt.setTimestamp(7, Timestamp.valueOf(adjustment.getCreatedAt()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        adjustment.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private StockAdjustment mapResultSetToStockAdjustment(ResultSet rs) throws SQLException {
        StockAdjustment adjustment = new StockAdjustment();
        adjustment.setId(rs.getInt("id"));
        adjustment.setAdjustmentId(rs.getInt("adjustment_id"));
        adjustment.setAdjustmentNo(rs.getString("adjustment_no"));
        adjustment.setProductId(rs.getInt("product_id"));
        adjustment.setAdjustmentQuantity(rs.getInt("adjustment_quantity"));
        adjustment.setAdjustmentType(rs.getString("adjustment_type"));
        adjustment.setReason(rs.getString("reason"));
        adjustment.setAdjustmentDate(rs.getDate("adjustment_date").toLocalDate());
        adjustment.setStatus(rs.getString("status"));
        adjustment.setRemarks(rs.getString("remarks"));
        adjustment.setCreatedBy(rs.getInt("created_by"));
        if (rs.getTimestamp("created_at") != null) {
            adjustment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            adjustment.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return adjustment;
    }

    public StockAdjustment getStockAdjustmentById(int adjustmentId) {
        String sql = "SELECT * FROM stock_adjustment WHERE adjustment_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, adjustmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    StockAdjustment adjustment = new StockAdjustment();
                    adjustment.setAdjustmentId(rs.getInt("adjustment_id"));
                    adjustment.setAdjustmentNo(rs.getString("adjustment_no"));
                    adjustment.setAdjustmentDate(rs.getDate("adjustment_date").toLocalDate());
                    adjustment.setStoreLocationId(rs.getInt("store_location_id"));
                    adjustment.setReasonId(rs.getInt("reason_id"));
                    adjustment.setStatus(rs.getString("status"));
                    adjustment.setRemarks(rs.getString("remarks"));
                    adjustment.setCreatedBy(rs.getInt("created_by"));
                    adjustment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    adjustment.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return adjustment;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateStockAdjustment(StockAdjustment adjustment) {
        String sql = """
            UPDATE stock_adjustment SET adjustment_no = ?, adjustment_date = ?, 
            store_location_id = ?, reason_id = ?, status = ?, remarks = ?, updated_at = CURRENT_TIMESTAMP 
            WHERE adjustment_id = ?
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, adjustment.getAdjustmentNo());
            pstmt.setDate(2, Date.valueOf(adjustment.getAdjustmentDate()));
            pstmt.setInt(3, adjustment.getStoreLocationId());
            pstmt.setInt(4, adjustment.getReasonId());
            pstmt.setString(5, adjustment.getStatus());
            pstmt.setString(6, adjustment.getRemarks());
            pstmt.setInt(7, adjustment.getAdjustmentId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteStockAdjustment(int adjustmentId) {
        String sql = "DELETE FROM stock_adjustment WHERE adjustment_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, adjustmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAdjustment(int id) {
        String sql = "DELETE FROM stock_adjustment WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalAdjustments() {
        String sql = "SELECT COUNT(*) FROM stock_adjustment";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String generateAdjustmentNumber() {
        String sql = "SELECT COUNT(*) FROM stock_adjustment WHERE adjustment_date = CURRENT_DATE";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return "ADJ-" + LocalDate.now().toString().replace("-", "") + "-" + String.format("%04d", count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ADJ-" + LocalDate.now().toString().replace("-", "") + "-0001";
    }
}
