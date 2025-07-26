package org.example.newchronopos.dao;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserDAO {
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullName(rs.getString("full_name"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setRole(rs.getString("role"));
                u.setPhoneNo(rs.getString("phone_no"));
                u.setSalary(rs.getDouble("salary"));
                u.setDob(rs.getDate("dob"));
                u.setChangeAccess(rs.getBoolean("change_access"));
                u.setShiftStartTime(rs.getTime("shift_start_time"));
                u.setShiftEndTime(rs.getTime("shift_end_time"));
                u.setAddress(rs.getString("address"));
                u.setAdditionalDetails(rs.getString("additional_details"));
                u.setUaeId(rs.getString("uae_id"));
                u.setCreatedAt(rs.getTimestamp("created_at"));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkPassword(User user, String plainPassword) {
        return BCrypt.checkpw(plainPassword, user.getPassword());
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addUser(User user) {
        String sql = """
            INSERT INTO users (full_name, username, email, password, role, phone_no, salary, 
                              dob, change_access, shift_start_time, shift_end_time, address, 
                              additional_details, uae_id, created_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole());
            ps.setString(6, user.getPhoneNo());
            ps.setDouble(7, user.getSalary());
            ps.setDate(8, user.getDob());
            ps.setBoolean(9, user.isChangeAccess());
            ps.setTime(10, user.getShiftStartTime());
            ps.setTime(11, user.getShiftEndTime());
            ps.setString(12, user.getAddress());
            ps.setString(13, user.getAdditionalDetails());
            ps.setString(14, user.getUaeId());
            ps.setTimestamp(15, user.getCreatedAt());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
