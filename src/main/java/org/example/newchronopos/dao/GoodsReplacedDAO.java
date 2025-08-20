package org.example.newchronopos.dao;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.GoodsReplaced;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GoodsReplacedDAO {

    public boolean createGoodsReplaced(GoodsReplaced goodsReplaced) {
        String sql = """
            INSERT INTO goods_replaced (replaced_no, replaced_date, customer_id, 
            store_location_id, status, remarks, reason, created_by) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, goodsReplaced.getReplacedNo());
            pstmt.setDate(2, Date.valueOf(goodsReplaced.getReplacedDate()));
            pstmt.setInt(3, goodsReplaced.getCustomerId());
            pstmt.setInt(4, goodsReplaced.getStoreLocationId());
            pstmt.setString(5, goodsReplaced.getStatus());
            pstmt.setString(6, goodsReplaced.getRemarks());
            pstmt.setString(7, goodsReplaced.getReason());
            pstmt.setInt(8, goodsReplaced.getCreatedBy());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        goodsReplaced.setReplacedId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GoodsReplaced> getAllGoodsReplaced() {
        List<GoodsReplaced> replacedList = new ArrayList<>();
        String sql = "SELECT * FROM goods_replaced ORDER BY created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                GoodsReplaced goods = mapResultSetToGoodsReplaced(rs);
                replacedList.add(goods);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return replacedList;
    }

    public List<GoodsReplaced> searchGoodsReplaced(String searchTerm) {
        List<GoodsReplaced> replacedList = new ArrayList<>();
        String sql = """
            SELECT gr.*, c.name as customer_name 
            FROM goods_replaced gr 
            LEFT JOIN customers c ON gr.customer_id = c.id 
            WHERE gr.replaced_no LIKE ? OR c.name LIKE ? OR gr.reason LIKE ?
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
                    GoodsReplaced goods = mapResultSetToGoodsReplaced(rs);
                    replacedList.add(goods);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return replacedList;
    }

    public boolean addGoodsReplaced(GoodsReplaced goods) {
        String sql = """
            INSERT INTO goods_replaced (replaced_no, original_product_id, replacement_product_id, 
            quantity, replacement_date, notes, created_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, goods.getReplacedNo());
            pstmt.setInt(2, goods.getOriginalProductId());
            pstmt.setInt(3, goods.getReplacementProductId());
            pstmt.setInt(4, goods.getQuantity());
            pstmt.setTimestamp(5, Timestamp.valueOf(goods.getReplacementDate()));
            pstmt.setString(6, goods.getNotes());
            pstmt.setTimestamp(7, Timestamp.valueOf(goods.getCreatedAt()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        goods.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateReplacedNumber() {
        String sql = "SELECT COUNT(*) FROM goods_replaced WHERE replaced_date = CURRENT_DATE";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return "REP-" + LocalDate.now().toString().replace("-", "") + "-" + String.format("%04d", count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "REP-" + LocalDate.now().toString().replace("-", "") + "-0001";
    }

    public boolean updateGoodsReplaced(GoodsReplaced goodsReplaced) {
        String sql = """
            UPDATE goods_replaced SET replaced_no = ?, replaced_date = ?, 
            customer_id = ?, store_location_id = ?, status = ?, remarks = ?, 
            reason = ?, updated_at = CURRENT_TIMESTAMP 
            WHERE replaced_id = ?
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, goodsReplaced.getReplacedNo());
            pstmt.setDate(2, Date.valueOf(goodsReplaced.getReplacedDate()));
            pstmt.setInt(3, goodsReplaced.getCustomerId());
            pstmt.setInt(4, goodsReplaced.getStoreLocationId());
            pstmt.setString(5, goodsReplaced.getStatus());
            pstmt.setString(6, goodsReplaced.getRemarks());
            pstmt.setString(7, goodsReplaced.getReason());
            pstmt.setInt(8, goodsReplaced.getReplacedId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteGoodsReplaced(int id) {
        String sql = "DELETE FROM goods_replaced WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalReplaced() {
        String sql = "SELECT COUNT(*) FROM goods_replaced";

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

    private GoodsReplaced mapResultSetToGoodsReplaced(ResultSet rs) throws SQLException {
        GoodsReplaced goods = new GoodsReplaced();
        goods.setId(rs.getInt("id"));
        goods.setReplacedId(rs.getInt("replaced_id"));
        goods.setReplacedNo(rs.getString("replaced_no"));
        goods.setReplacedDate(rs.getDate("replaced_date").toLocalDate());
        goods.setCustomerId(rs.getInt("customer_id"));
        goods.setStoreLocationId(rs.getInt("store_location_id"));
        goods.setStatus(rs.getString("status"));
        goods.setRemarks(rs.getString("remarks"));
        goods.setReason(rs.getString("reason"));
        goods.setCreatedBy(rs.getInt("created_by"));

        // Handle nullable fields
        if (rs.getTimestamp("created_at") != null) {
            goods.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            goods.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        // Set additional fields if they exist
        goods.setOriginalProductId(rs.getInt("original_product_id"));
        goods.setReplacementProductId(rs.getInt("replacement_product_id"));
        goods.setQuantity(rs.getInt("quantity"));
        if (rs.getTimestamp("replacement_date") != null) {
            goods.setReplacementDate(rs.getTimestamp("replacement_date").toLocalDateTime());
        }
        goods.setNotes(rs.getString("notes"));

        return goods;
    }
}
