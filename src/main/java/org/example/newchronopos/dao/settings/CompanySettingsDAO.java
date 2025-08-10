package org.example.newchronopos.dao.settings;

import org.example.newchronopos.config.DatabaseConfig;
import org.example.newchronopos.model.settings.CompanySettings;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Company Settings
 */
public class CompanySettingsDAO {

    // Create company_settings table if not exists
    public void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS company_settings (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                company_id BIGINT NOT NULL,
                setting_key VARCHAR(100) NOT NULL,
                setting_value TEXT,
                category VARCHAR(50) NOT NULL,
                description TEXT,
                data_type VARCHAR(20) DEFAULT 'STRING',
                is_active BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                CONSTRAINT unique_company_setting UNIQUE (company_id, setting_key)
            )
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create company_settings table", e);
        }
    }    // Save or update company setting
    public CompanySettings save(CompanySettings setting) {
        if (setting.getId() == null) {
            return insert(setting);
        } else {
            return update(setting);
        }
    }

    // Insert new company setting
    private CompanySettings insert(CompanySettings setting) {
        String sql = """
            INSERT INTO company_settings (company_id, setting_key, setting_value, category, 
                                        description, data_type, is_active, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, setting.getCompanyId());
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
            throw new RuntimeException("Failed to insert company setting", e);
        }
    }

    // Update existing company setting
    private CompanySettings update(CompanySettings setting) {
        String sql = """
            UPDATE company_settings 
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
            throw new RuntimeException("Failed to update company setting", e);
        }
    }

    // Find setting by company ID and key
    public Optional<CompanySettings> findByCompanyAndKey(Long companyId, String settingKey) {
        String sql = "SELECT * FROM company_settings WHERE company_id = ? AND setting_key = ? AND is_active = TRUE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, companyId);
            pstmt.setString(2, settingKey);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompanySettings(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find company setting", e);
        }
    }

    // Get all settings for a company
    public List<CompanySettings> findByCompany(Long companyId) {
        String sql = "SELECT * FROM company_settings WHERE company_id = ? AND is_active = TRUE ORDER BY category, setting_key";
        List<CompanySettings> settings = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, companyId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    settings.add(mapResultSetToCompanySettings(rs));
                }
            }
            
            return settings;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find company settings", e);
        }
    }

    // Get settings by company and category
    public List<CompanySettings> findByCompanyAndCategory(Long companyId, String category) {
        String sql = "SELECT * FROM company_settings WHERE company_id = ? AND category = ? AND is_active = TRUE ORDER BY setting_key";
        List<CompanySettings> settings = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, companyId);
            pstmt.setString(2, category);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    settings.add(mapResultSetToCompanySettings(rs));
                }
            }
            
            return settings;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find company settings by category", e);
        }
    }

    // Update setting value only
    public boolean updateValue(Long companyId, String settingKey, String newValue) {
        String sql = "UPDATE company_settings SET setting_value = ?, updated_at = ? WHERE company_id = ? AND setting_key = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newValue);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setLong(3, companyId);
            pstmt.setString(4, settingKey);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update company setting value", e);
        }
    }

    // Deactivate setting (soft delete)
    public boolean deactivateSetting(Long companyId, String settingKey) {
        String sql = "UPDATE company_settings SET is_active = FALSE, updated_at = ? WHERE company_id = ? AND setting_key = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setLong(2, companyId);
            pstmt.setString(3, settingKey);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to deactivate company setting", e);
        }
    }

    // Get distinct categories for a company
    public List<String> getCategoriesByCompany(Long companyId) {
        String sql = "SELECT DISTINCT category FROM company_settings WHERE company_id = ? AND is_active = TRUE ORDER BY category";
        List<String> categories = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, companyId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(rs.getString("category"));
                }
            }
            
            return categories;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve company setting categories", e);
        }
    }

    // Helper method to map ResultSet to CompanySettings object
    private CompanySettings mapResultSetToCompanySettings(ResultSet rs) throws SQLException {
        CompanySettings setting = new CompanySettings();
        setting.setId(rs.getLong("id"));
        setting.setCompanyId(rs.getLong("company_id"));
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
