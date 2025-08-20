package org.example.newchronopos.dao;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.GoodsReceived;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GoodsReceivedDAO {

    public boolean createGoodsReceived(GoodsReceived goodsReceived) {
        String sql = """
            INSERT INTO goods_received (received_no, received_date, supplier_id, 
            store_location_id, status, remarks, total_amount, created_by) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, goodsReceived.getReceivedNo());
            pstmt.setDate(2, Date.valueOf(goodsReceived.getReceivedDate()));
            pstmt.setInt(3, goodsReceived.getSupplierId());
            pstmt.setInt(4, goodsReceived.getStoreLocationId());
            pstmt.setString(5, goodsReceived.getStatus());
            pstmt.setString(6, goodsReceived.getRemarks());
            pstmt.setDouble(7, goodsReceived.getTotalAmount());
            pstmt.setInt(8, goodsReceived.getCreatedBy());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        goodsReceived.setReceivedId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GoodsReceived> getAllGoodsReceived() {
        List<GoodsReceived> goodsList = new ArrayList<>();
        String sql = "SELECT * FROM goods_received ORDER BY created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                GoodsReceived goods = new GoodsReceived();
                goods.setReceivedId(rs.getInt("received_id"));
                goods.setReceivedNo(rs.getString("received_no"));
                goods.setReceivedDate(rs.getDate("received_date").toLocalDate());
                goods.setSupplierId(rs.getInt("supplier_id"));
                goods.setStoreLocationId(rs.getInt("store_location_id"));
                goods.setStatus(rs.getString("status"));
                goods.setRemarks(rs.getString("remarks"));
                goods.setTotalAmount(rs.getDouble("total_amount"));
                goods.setCreatedBy(rs.getInt("created_by"));
                goods.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                goods.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                goodsList.add(goods);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goodsList;
    }

    public String generateReceivedNumber() {
        String sql = "SELECT COUNT(*) FROM goods_received WHERE received_date = CURRENT_DATE";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return "GRN-" + LocalDate.now().toString().replace("-", "") + "-" + String.format("%04d", count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "GRN-" + LocalDate.now().toString().replace("-", "") + "-0001";
    }

    public boolean updateGoodsReceived(GoodsReceived goodsReceived) {
        String sql = """
            UPDATE goods_received SET received_no = ?, received_date = ?, 
            supplier_id = ?, store_location_id = ?, status = ?, remarks = ?, 
            total_amount = ?, updated_at = CURRENT_TIMESTAMP 
            WHERE received_id = ?
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, goodsReceived.getReceivedNo());
            pstmt.setDate(2, Date.valueOf(goodsReceived.getReceivedDate()));
            pstmt.setInt(3, goodsReceived.getSupplierId());
            pstmt.setInt(4, goodsReceived.getStoreLocationId());
            pstmt.setString(5, goodsReceived.getStatus());
            pstmt.setString(6, goodsReceived.getRemarks());
            pstmt.setDouble(7, goodsReceived.getTotalAmount());
            pstmt.setInt(8, goodsReceived.getReceivedId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteGoodsReceived(int receivedId) {
        String sql = "DELETE FROM goods_received WHERE received_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, receivedId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addGoodsReceived(GoodsReceived goods) {
        String sql = "INSERT INTO goods_received (received_no, supplier_id, product_id, quantity_received, unit_cost, received_date, status, notes, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, goods.getReceivedNo());
            ps.setInt(2, goods.getSupplierId());
            ps.setInt(3, goods.getProductId());
            ps.setInt(4, goods.getQuantityReceived());
            ps.setDouble(5, goods.getUnitCost());
            ps.setDate(6, Date.valueOf(goods.getReceivedDate()));
            ps.setString(7, goods.getStatus());
            ps.setString(8, goods.getNotes());
            ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        goods.setReceivedId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GoodsReceived> searchGoodsReceived(String searchTerm) {
        List<GoodsReceived> goodsList = new ArrayList<>();
        String sql = """
            SELECT gr.*, s.name as supplier_name 
            FROM goods_received gr 
            LEFT JOIN suppliers s ON gr.supplier_id = s.id 
            WHERE gr.received_no LIKE ? OR s.name LIKE ? OR gr.status LIKE ?
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
                    GoodsReceived goods = new GoodsReceived();
                    goods.setReceivedId(rs.getInt("received_id"));
                    goods.setReceivedNo(rs.getString("received_no"));
                    goods.setReceivedDate(rs.getDate("received_date").toLocalDate());
                    goods.setSupplierId(rs.getInt("supplier_id"));
                    goods.setStoreLocationId(rs.getInt("store_location_id"));
                    goods.setStatus(rs.getString("status"));
                    goods.setRemarks(rs.getString("remarks"));
                    goods.setTotalAmount(rs.getDouble("total_amount"));
                    goods.setCreatedBy(rs.getInt("created_by"));
                    if (rs.getTimestamp("created_at") != null) {
                        goods.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                    if (rs.getTimestamp("updated_at") != null) {
                        goods.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    }
                    // Set additional fields for search compatibility
                    goods.setProductId(rs.getInt("product_id"));
                    goods.setQuantityReceived(rs.getInt("quantity_received"));
                    goods.setUnitCost(rs.getDouble("unit_cost"));
                    goods.setNotes(rs.getString("notes"));
                    goodsList.add(goods);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goodsList;
    }

    public int getTotalReceived() {
        String sql = "SELECT COUNT(*) FROM goods_received";

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
}
