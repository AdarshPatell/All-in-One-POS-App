package org.example.newchronopos.dao;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.StockTransfer;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StockTransferDAO {

    public List<StockTransfer> getAllTransfers() {
        List<StockTransfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM stock_transfer ORDER BY created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transfers.add(mapResultSetToStockTransfer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching all transfers", e);
        }
        return transfers;
    }

    public Optional<StockTransfer> getTransferById(int id) {
        String sql = "SELECT * FROM stock_transfer WHERE transfer_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToStockTransfer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching transfer by ID", e);
        }
        return Optional.empty();
    }

    public boolean addTransfer(StockTransfer transfer) {
        String sql = """
            INSERT INTO stock_transfer (transfer_no, product_id, from_location, to_location, 
            quantity, transfer_date, status, notes, created_by, created_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, generateTransferNo());
            ps.setInt(2, transfer.getProductId());
            ps.setString(3, transfer.getFromLocation());
            ps.setString(4, transfer.getToLocation());
            ps.setInt(5, transfer.getQuantity());
            ps.setDate(6, Date.valueOf(transfer.getTransferDate() != null ? transfer.getTransferDate() : LocalDate.now()));
            ps.setString(7, transfer.getStatus());
            ps.setString(8, transfer.getNotes());
            ps.setInt(9, transfer.getCreatedBy() != 0 ? transfer.getCreatedBy() : 1); // Default user ID
            ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transfer.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while adding transfer", e);
        }
        return false;
    }

    public boolean updateTransfer(StockTransfer transfer) {
        String sql = """
            UPDATE stock_transfer SET product_id = ?, from_location = ?, to_location = ?, 
            quantity = ?, transfer_date = ?, status = ?, notes = ?, updated_at = ? 
            WHERE transfer_id = ?
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, transfer.getProductId());
            ps.setString(2, transfer.getFromLocation());
            ps.setString(3, transfer.getToLocation());
            ps.setInt(4, transfer.getQuantity());
            ps.setDate(5, Date.valueOf(transfer.getTransferDate() != null ? transfer.getTransferDate() : LocalDate.now()));
            ps.setString(6, transfer.getStatus());
            ps.setString(7, transfer.getNotes());
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(9, transfer.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while updating transfer", e);
        }
    }

    public boolean deleteTransfer(int id) {
        String sql = "DELETE FROM stock_transfer WHERE transfer_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while deleting transfer", e);
        }
    }

    public List<StockTransfer> searchTransfers(String searchTerm) {
        List<StockTransfer> transfers = new ArrayList<>();
        String sql = """
            SELECT st.* FROM stock_transfer st 
            LEFT JOIN products p ON st.product_id = p.product_id 
            WHERE st.transfer_no LIKE ? OR st.from_location LIKE ? OR st.to_location LIKE ? 
            OR st.status LIKE ? OR p.product_name LIKE ? 
            ORDER BY st.created_at DESC
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ps.setString(5, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transfers.add(mapResultSetToStockTransfer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while searching transfers", e);
        }
        return transfers;
    }

    public int getTotalTransfers() {
        String sql = "SELECT COUNT(*) FROM stock_transfer";
        
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

    private StockTransfer mapResultSetToStockTransfer(ResultSet rs) throws SQLException {
        StockTransfer transfer = new StockTransfer();
        transfer.setId(rs.getInt("transfer_id"));
        transfer.setTransferNo(rs.getString("transfer_no"));
        transfer.setProductId(rs.getInt("product_id"));
        transfer.setFromLocation(rs.getString("from_location"));
        transfer.setToLocation(rs.getString("to_location"));
        transfer.setQuantity(rs.getInt("quantity"));
        
        Date transferDate = rs.getDate("transfer_date");
        if (transferDate != null) {
            transfer.setTransferDate(transferDate.toLocalDate());
        }
        
        transfer.setStatus(rs.getString("status"));
        transfer.setNotes(rs.getString("notes"));
        transfer.setCreatedBy(rs.getInt("created_by"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            transfer.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            transfer.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return transfer;
    }

    private String generateTransferNo() {
        return "ST" + System.currentTimeMillis();
    }

    public String generateTransferNumber() {
        String prefix = "TRN";
        String datePart = LocalDate.now().toString().replace("-", "");

        String sql = "SELECT COUNT(*) + 1 as next_number FROM stock_transfer WHERE DATE(created_at) = CURRENT_DATE";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int nextNumber = rs.getInt("next_number");
                return prefix + datePart + String.format("%03d", nextNumber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Fallback to timestamp-based number if query fails
        return prefix + System.currentTimeMillis();
    }

    // Backward compatibility methods
    public boolean createStockTransfer(StockTransfer transfer) {
        return addTransfer(transfer);
    }

    public List<StockTransfer> getAllStockTransfers() {
        return getAllTransfers();
    }
}
