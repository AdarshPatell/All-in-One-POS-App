package org.example.newchronopos.dao;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.GoodsReturn;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GoodsReturnDAO {

    public boolean createGoodsReturn(GoodsReturn goodsReturn) {
        String sql = """
            INSERT INTO goods_return (return_no, return_date, customer_id, 
            store_location_id, status, remarks, reason, total_amount, created_by) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, goodsReturn.getReturnNo());
            pstmt.setDate(2, Date.valueOf(goodsReturn.getReturnDate()));
            pstmt.setInt(3, goodsReturn.getCustomerId());
            pstmt.setInt(4, goodsReturn.getStoreLocationId());
            pstmt.setString(5, goodsReturn.getStatus());
            pstmt.setString(6, goodsReturn.getRemarks());
            pstmt.setString(7, goodsReturn.getReason());
            pstmt.setDouble(8, goodsReturn.getTotalAmount());
            pstmt.setInt(9, goodsReturn.getCreatedBy());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        goodsReturn.setReturnId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GoodsReturn> getAllGoodsReturn() {
        List<GoodsReturn> returnList = new ArrayList<>();
        String sql = "SELECT * FROM goods_return ORDER BY created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                GoodsReturn returnItem = new GoodsReturn();
                returnItem.setReturnId(rs.getInt("return_id"));
                returnItem.setReturnNo(rs.getString("return_no"));
                returnItem.setReturnDate(rs.getDate("return_date").toLocalDate());
                returnItem.setCustomerId(rs.getInt("customer_id"));
                returnItem.setStoreLocationId(rs.getInt("store_location_id"));
                returnItem.setStatus(rs.getString("status"));
                returnItem.setRemarks(rs.getString("remarks"));
                returnItem.setReason(rs.getString("reason"));
                returnItem.setTotalAmount(rs.getDouble("total_amount"));
                returnItem.setCreatedBy(rs.getInt("created_by"));
                returnItem.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                returnItem.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                returnList.add(returnItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnList;
    }

    public String generateReturnNumber() {
        String sql = "SELECT COUNT(*) FROM goods_return WHERE return_date = CURRENT_DATE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return "RET-" + LocalDate.now().toString().replace("-", "") + "-" + String.format("%04d", count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "RET-" + LocalDate.now().toString().replace("-", "") + "-0001";
    }

    public List<GoodsReturn> getAllGoodsReturns() {
        return getAllGoodsReturn();
    }

    public List<GoodsReturn> searchGoodsReturns(String searchTerm) {
        List<GoodsReturn> returnList = new ArrayList<>();
        String sql = """
            SELECT gr.*, c.name as customer_name 
            FROM goods_return gr 
            LEFT JOIN customers c ON gr.customer_id = c.id 
            WHERE gr.return_no LIKE ? OR c.name LIKE ? OR gr.reason LIKE ?
            ORDER BY gr.created_at DESC
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    GoodsReturn returnItem = mapResultSetToGoodsReturn(rs);
                    returnList.add(returnItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnList;
    }

    public boolean addGoodsReturn(GoodsReturn goodsReturn) {
        String sql = """
            INSERT INTO goods_return (return_no, product_id, quantity, refund_amount, 
            refund_method, return_date, notes, created_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, goodsReturn.getReturnNo());
            pstmt.setInt(2, goodsReturn.getProductId());
            pstmt.setInt(3, goodsReturn.getQuantity());
            pstmt.setDouble(4, goodsReturn.getRefundAmount());
            pstmt.setString(5, goodsReturn.getRefundMethod());
            pstmt.setDate(6, Date.valueOf(goodsReturn.getReturnDate()));
            pstmt.setString(7, goodsReturn.getNotes());
            pstmt.setTimestamp(8, Timestamp.valueOf(goodsReturn.getCreatedAt()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        goodsReturn.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateGoodsReturn(GoodsReturn goodsReturn) {
        String sql = """
            UPDATE goods_return SET return_no = ?, return_date = ?, 
            customer_id = ?, store_location_id = ?, status = ?, remarks = ?, 
            reason = ?, total_amount = ?, updated_at = CURRENT_TIMESTAMP 
            WHERE return_id = ?
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, goodsReturn.getReturnNo());
            pstmt.setDate(2, Date.valueOf(goodsReturn.getReturnDate()));
            pstmt.setInt(3, goodsReturn.getCustomerId());
            pstmt.setInt(4, goodsReturn.getStoreLocationId());
            pstmt.setString(5, goodsReturn.getStatus());
            pstmt.setString(6, goodsReturn.getRemarks());
            pstmt.setString(7, goodsReturn.getReason());
            pstmt.setDouble(8, goodsReturn.getTotalAmount());
            pstmt.setInt(9, goodsReturn.getReturnId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteGoodsReturn(int id) {
        String sql = "DELETE FROM goods_return WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalReturns() {
        String sql = "SELECT COUNT(*) FROM goods_return";

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

    private GoodsReturn mapResultSetToGoodsReturn(ResultSet rs) throws SQLException {
        GoodsReturn returnItem = new GoodsReturn();
        returnItem.setId(rs.getInt("id"));
        returnItem.setReturnId(rs.getInt("return_id"));
        returnItem.setReturnNo(rs.getString("return_no"));
        returnItem.setReturnDate(rs.getDate("return_date").toLocalDate());
        returnItem.setCustomerId(rs.getInt("customer_id"));
        returnItem.setStoreLocationId(rs.getInt("store_location_id"));
        returnItem.setStatus(rs.getString("status"));
        returnItem.setRemarks(rs.getString("remarks"));
        returnItem.setReason(rs.getString("reason"));
        returnItem.setTotalAmount(rs.getDouble("total_amount"));
        returnItem.setCreatedBy(rs.getInt("created_by"));

        if (rs.getTimestamp("created_at") != null) {
            returnItem.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            returnItem.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        // Set additional fields if they exist
        returnItem.setProductId(rs.getInt("product_id"));
        returnItem.setQuantity(rs.getInt("quantity"));
        returnItem.setRefundAmount(rs.getDouble("refund_amount"));
        returnItem.setRefundMethod(rs.getString("refund_method"));
        returnItem.setNotes(rs.getString("notes"));

        return returnItem;
    }
}
