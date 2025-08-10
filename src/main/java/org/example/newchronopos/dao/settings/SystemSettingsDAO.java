package org.example.newchronopos.dao.settings;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.settings.SystemSettings;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for System Settings
 */
public class SystemSettingsDAO {

    // Create system_settings table if not exists
    public void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS system_settings (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                setting_key VARCHAR(100) UNIQUE NOT NULL,
                setting_value TEXT,
                category VARCHAR(50) NOT NULL,
                description TEXT,
                data_type VARCHAR(20) DEFAULT 'STRING',
                default_value TEXT,
                is_required BOOLEAN DEFAULT FALSE,
                is_encrypted BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create system_settings table", e);
        }
    }

    // Save or update system setting
    public SystemSettings save(SystemSettings setting) {
        if (setting.getId() == null) {
            return insert(setting);
        } else {
            return update(setting);
        }
    }

    // Insert new system setting
    private SystemSettings insert(SystemSettings setting) {
        String sql = """
            INSERT INTO system_settings (setting_key, setting_value, category, description, 
                                       data_type, default_value, is_required, is_encrypted, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, setting.getSettingKey());
            pstmt.setString(2, setting.getSettingValue());
            pstmt.setString(3, setting.getCategory());
            pstmt.setString(4, setting.getDescription());
            pstmt.setString(5, setting.getDataType());
            pstmt.setString(6, setting.getDefaultValue());
            pstmt.setBoolean(7, setting.isRequired());
            pstmt.setBoolean(8, setting.isEncrypted());
            pstmt.setTimestamp(9, Timestamp.valueOf(setting.getCreatedAt()));
            pstmt.setTimestamp(10, Timestamp.valueOf(setting.getUpdatedAt()));

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    setting.setId(rs.getLong(1));
                }
            }

            return setting;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert system setting", e);
        }
    }

    // Update existing system setting
    private SystemSettings update(SystemSettings setting) {
        String sql = """
            UPDATE system_settings 
            SET setting_value = ?, category = ?, description = ?, data_type = ?, 
                default_value = ?, is_required = ?, is_encrypted = ?, updated_at = ?
            WHERE id = ?
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, setting.getSettingValue());
            pstmt.setString(2, setting.getCategory());
            pstmt.setString(3, setting.getDescription());
            pstmt.setString(4, setting.getDataType());
            pstmt.setString(5, setting.getDefaultValue());
            pstmt.setBoolean(6, setting.isRequired());
            pstmt.setBoolean(7, setting.isEncrypted());
            pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setLong(9, setting.getId());

            pstmt.executeUpdate();
            setting.setUpdatedAt(LocalDateTime.now());

            return setting;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update system setting", e);
        }
    }

    // Find setting by key
    public Optional<SystemSettings> findByKey(String settingKey) {
        String sql = "SELECT * FROM system_settings WHERE setting_key = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, settingKey);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSystemSettings(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find system setting by key: " + settingKey, e);
        }
    }

    // Get all settings by category
    public List<SystemSettings> findByCategory(String category) {
        String sql = "SELECT * FROM system_settings WHERE category = ? ORDER BY setting_key";
        List<SystemSettings> settings = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    settings.add(mapResultSetToSystemSettings(rs));
                }
            }
            
            return settings;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find system settings by category: " + category, e);
        }
    }

    // Get all system settings
    public List<SystemSettings> findAll() {
        String sql = "SELECT * FROM system_settings ORDER BY category, setting_key";
        List<SystemSettings> settings = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                settings.add(mapResultSetToSystemSettings(rs));
            }
            
            return settings;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all system settings", e);
        }
    }

    // Delete setting by key
    public boolean deleteByKey(String settingKey) {
        String sql = "DELETE FROM system_settings WHERE setting_key = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, settingKey);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete system setting: " + settingKey, e);
        }
    }

    // Update setting value only
    public boolean updateValue(String settingKey, String newValue) {
        String sql = "UPDATE system_settings SET setting_value = ?, updated_at = ? WHERE setting_key = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newValue);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(3, settingKey);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update system setting value: " + settingKey, e);
        }
    }

    // Get distinct categories
    public List<String> getCategories() {
        String sql = "SELECT DISTINCT category FROM system_settings ORDER BY category";
        List<String> categories = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
            
            return categories;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve setting categories", e);
        }
    }

    // Helper method to map ResultSet to SystemSettings object
    private SystemSettings mapResultSetToSystemSettings(ResultSet rs) throws SQLException {
        SystemSettings setting = new SystemSettings();
        setting.setId(rs.getLong("id"));
        setting.setSettingKey(rs.getString("setting_key"));
        setting.setSettingValue(rs.getString("setting_value"));
        setting.setCategory(rs.getString("category"));
        setting.setDescription(rs.getString("description"));
        setting.setDataType(rs.getString("data_type"));
        setting.setDefaultValue(rs.getString("default_value"));
        setting.setRequired(rs.getBoolean("is_required"));
        setting.setEncrypted(rs.getBoolean("is_encrypted"));
        
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
