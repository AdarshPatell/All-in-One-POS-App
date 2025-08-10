package org.example.newchronopos.dao.settings;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.settings.UserSettings;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User Settings
 */
public class UserSettingsDAO {

    // Create user_settings table if not exists
    public void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS user_settings (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                user_id BIGINT NOT NULL,
                setting_key VARCHAR(100) NOT NULL,
                setting_value TEXT,
                category VARCHAR(50) NOT NULL,
                description TEXT,
                data_type VARCHAR(20) DEFAULT 'STRING',
                is_active BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                CONSTRAINT unique_user_setting UNIQUE (user_id, setting_key)
            )
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user_settings table", e);
        }
    }

    // Save or update user setting
    public UserSettings save(UserSettings setting) {
        if (setting.getId() == null) {
            return insert(setting);
        } else {
            return update(setting);
        }
    }

    // Insert new user setting
    private UserSettings insert(UserSettings setting) {
        String sql = """
            INSERT INTO user_settings (user_id, setting_key, setting_value, category, 
                                     description, data_type, is_active, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, setting.getUserId());
            pstmt.setString(2, setting.getSettingKey());
            pstmt.setString(3, setting.getSettingValue());
            pstmt.setString(4, setting.getCategory());
            pstmt.setString(5, setting.getDescription());
            pstmt.setString(6, setting.getDataType());
            pstmt.setBoolean(7, setting.isActive());
            pstmt.setTimestamp(8, Timestamp.valueOf(setting.getCreatedAt()));
            pstmt.setTimestamp(9, Timestamp.valueOf(setting.getUpdatedAt()));

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    setting.setId(rs.getLong(1));
                }
            }

            return setting;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user setting", e);
        }
    }

    // Update existing user setting
    private UserSettings update(UserSettings setting) {
        String sql = """
            UPDATE user_settings 
            SET setting_value = ?, category = ?, description = ?, data_type = ?, 
                is_active = ?, updated_at = ?
            WHERE id = ?
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, setting.getSettingValue());
            pstmt.setString(2, setting.getCategory());
            pstmt.setString(3, setting.getDescription());
            pstmt.setString(4, setting.getDataType());
            pstmt.setBoolean(5, setting.isActive());
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setLong(7, setting.getId());

            pstmt.executeUpdate();
            setting.setUpdatedAt(LocalDateTime.now());

            return setting;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user setting", e);
        }
    }

    // Find setting by user ID and key
    public Optional<UserSettings> findByUserAndKey(Long userId, String settingKey) {
        String sql = "SELECT * FROM user_settings WHERE user_id = ? AND setting_key = ? AND is_active = TRUE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, userId);
            pstmt.setString(2, settingKey);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUserSettings(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user setting", e);
        }
    }

    // Get all settings for a user
    public List<UserSettings> findByUser(Long userId) {
        String sql = "SELECT * FROM user_settings WHERE user_id = ? AND is_active = TRUE ORDER BY category, setting_key";
        List<UserSettings> settings = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    settings.add(mapResultSetToUserSettings(rs));
                }
            }
            
            return settings;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user settings", e);
        }
    }

    // Get settings by user and category
    public List<UserSettings> findByUserAndCategory(Long userId, String category) {
        String sql = "SELECT * FROM user_settings WHERE user_id = ? AND category = ? AND is_active = TRUE ORDER BY setting_key";
        List<UserSettings> settings = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, userId);
            pstmt.setString(2, category);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    settings.add(mapResultSetToUserSettings(rs));
                }
            }
            
            return settings;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user settings by category", e);
        }
    }

    // Update setting value only
    public boolean updateValue(Long userId, String settingKey, String newValue) {
        String sql = "UPDATE user_settings SET setting_value = ?, updated_at = ? WHERE user_id = ? AND setting_key = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newValue);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setLong(3, userId);
            pstmt.setString(4, settingKey);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user setting value", e);
        }
    }

    // Deactivate setting (soft delete)
    public boolean deactivateSetting(Long userId, String settingKey) {
        String sql = "UPDATE user_settings SET is_active = FALSE, updated_at = ? WHERE user_id = ? AND setting_key = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setLong(2, userId);
            pstmt.setString(3, settingKey);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to deactivate user setting", e);
        }
    }

    // Get distinct categories for a user
    public List<String> getCategoriesByUser(Long userId) {
        String sql = "SELECT DISTINCT category FROM user_settings WHERE user_id = ? AND is_active = TRUE ORDER BY category";
        List<String> categories = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(rs.getString("category"));
                }
            }
            
            return categories;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve user setting categories", e);
        }
    }

    // Delete all settings for a user (when user is deleted)
    public boolean deleteAllByUser(Long userId) {
        String sql = "DELETE FROM user_settings WHERE user_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, userId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user settings", e);
        }
    }

    // Helper method to map ResultSet to UserSettings object
    private UserSettings mapResultSetToUserSettings(ResultSet rs) throws SQLException {
        UserSettings setting = new UserSettings();
        setting.setId(rs.getLong("id"));
        setting.setUserId(rs.getLong("user_id"));
        setting.setSettingKey(rs.getString("setting_key"));
        setting.setSettingValue(rs.getString("setting_value"));
        setting.setCategory(rs.getString("category"));
        setting.setDescription(rs.getString("description"));
        setting.setDataType(rs.getString("data_type"));
        setting.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdTimestamp = rs.getTimestamp("created_at");
        if (createdTimestamp != null) {
            setting.setCreatedAt(createdTimestamp.toLocalDateTime());
        }
        
        Timestamp updatedTimestamp = rs.getTimestamp("updated_at");
        if (updatedTimestamp != null) {
            setting.setUpdatedAt(updatedTimestamp.toLocalDateTime());
        }
        
        return setting;
    }
}
