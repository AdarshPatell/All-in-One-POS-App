package org.example.newchronopos.service.settings;

import org.example.newchronopos.model.settings.SystemSettings;
import org.example.newchronopos.model.settings.CompanySettings;
import org.example.newchronopos.model.settings.UserSettings;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Settings Service Interface - Manages system, company, and user settings with hierarchical priority
 */
public interface SettingsService {

    // =====================================================
    // SYSTEM SETTINGS
    // =====================================================
    
    /**
     * Get system setting value by key
     */
    Optional<String> getSystemSetting(String key);
    
    /**
     * Get system setting with type conversion
     */
    boolean getSystemBooleanSetting(String key, boolean defaultValue);
    int getSystemIntegerSetting(String key, int defaultValue);
    double getSystemDecimalSetting(String key, double defaultValue);
    
    /**
     * Set system setting
     */
    void setSystemSetting(String key, String value);
    void setSystemSetting(String key, String value, String category);
    void setSystemSetting(String key, String value, String category, String description, String dataType);
    
    /**
     * Get system settings by category
     */
    List<SystemSettings> getSystemSettingsByCategory(String category);
    
    /**
     * Get all system settings
     */
    List<SystemSettings> getAllSystemSettings();

    // =====================================================
    // COMPANY SETTINGS
    // =====================================================
    
    /**
     * Get company setting value by key
     */
    Optional<String> getCompanySetting(Long companyId, String key);
    
    /**
     * Get company setting with type conversion
     */
    boolean getCompanyBooleanSetting(Long companyId, String key, boolean defaultValue);
    int getCompanyIntegerSetting(Long companyId, String key, int defaultValue);
    double getCompanyDecimalSetting(Long companyId, String key, double defaultValue);
    
    /**
     * Set company setting
     */
    void setCompanySetting(Long companyId, String key, String value);
    void setCompanySetting(Long companyId, String key, String value, String category);
    void setCompanySetting(Long companyId, String key, String value, String category, String description, String dataType);
    
    /**
     * Get company settings by category
     */
    List<CompanySettings> getCompanySettingsByCategory(Long companyId, String category);
    
    /**
     * Get all company settings
     */
    List<CompanySettings> getAllCompanySettings(Long companyId);

    // =====================================================
    // USER SETTINGS
    // =====================================================
    
    /**
     * Get user setting value by key
     */
    Optional<String> getUserSetting(Long userId, String key);
    
    /**
     * Get user setting with type conversion
     */
    boolean getUserBooleanSetting(Long userId, String key, boolean defaultValue);
    int getUserIntegerSetting(Long userId, String key, int defaultValue);
    double getUserDecimalSetting(Long userId, String key, double defaultValue);
    
    /**
     * Set user setting
     */
    void setUserSetting(Long userId, String key, String value);
    void setUserSetting(Long userId, String key, String value, String category);
    void setUserSetting(Long userId, String key, String value, String category, String description, String dataType);
    
    /**
     * Get user settings by category
     */
    List<UserSettings> getUserSettingsByCategory(Long userId, String category);
    
    /**
     * Get all user settings
     */
    List<UserSettings> getAllUserSettings(Long userId);

    // =====================================================
    // HIERARCHICAL SETTINGS (User -> Company -> System)
    // =====================================================
    
    /**
     * Get setting value with hierarchical lookup (User -> Company -> System)
     */
    Optional<String> getSetting(Long userId, Long companyId, String key);
    
    /**
     * Get setting with type conversion and hierarchical lookup
     */
    boolean getBooleanSetting(Long userId, Long companyId, String key, boolean defaultValue);
    int getIntegerSetting(Long userId, Long companyId, String key, int defaultValue);
    double getDecimalSetting(Long userId, Long companyId, String key, double defaultValue);
    String getStringSetting(Long userId, Long companyId, String key, String defaultValue);

    // =====================================================
    // BULK OPERATIONS
    // =====================================================
    
    /**
     * Import settings from map
     */
    void importSystemSettings(Map<String, String> settings);
    void importCompanySettings(Long companyId, Map<String, String> settings);
    void importUserSettings(Long userId, Map<String, String> settings);
    
    /**
     * Export settings to map
     */
    Map<String, String> exportSystemSettings();
    Map<String, String> exportCompanySettings(Long companyId);
    Map<String, String> exportUserSettings(Long userId);

    // =====================================================
    // VALIDATION & UTILITIES
    // =====================================================
    
    /**
     * Validate setting value
     */
    boolean isValidSettingValue(String key, String value, String dataType);
    
    /**
     * Get available categories
     */
    List<String> getSystemCategories();
    List<String> getCompanyCategories(Long companyId);
    List<String> getUserCategories(Long userId);
    
    /**
     * Initialize default settings
     */
    void initializeDefaultSettings();
    
    /**
     * Reset settings to defaults
     */
    void resetSystemSettingsToDefault();
    void resetCompanySettingsToDefault(Long companyId);
    void resetUserSettingsToDefault(Long userId);

    // =====================================================
    // CACHE MANAGEMENT
    // =====================================================
    
    /**
     * Clear settings cache
     */
    void clearCache();
    void clearSystemCache();
    void clearCompanyCache(Long companyId);
    void clearUserCache(Long userId);
}
