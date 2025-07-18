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
}
