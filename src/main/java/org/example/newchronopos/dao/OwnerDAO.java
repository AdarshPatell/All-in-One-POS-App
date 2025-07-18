package org.example.newchronopos.dao;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.Owner;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class OwnerDAO {
    public Owner findByUsername(String username) {
        String sql = "SELECT * FROM owner WHERE username = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Owner o = new Owner();
                o.setId(rs.getInt("id"));
                o.setName(rs.getString("name"));
                o.setUsername(rs.getString("username"));
                o.setPassword(rs.getString("password"));
                o.setCreatedAt(rs.getTimestamp("created_at"));
                return o;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkPassword(Owner owner, String plainPassword) {
        return BCrypt.checkpw(plainPassword, owner.getPassword());
    }
}
