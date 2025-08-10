package org.example.newchronopos.service.settings;

import org.example.newchronopos.dao.settings.SystemSettingsDAO;
import org.example.newchronopos.dao.settings.CompanySettingsDAO;
import org.example.newchronopos.dao.settings.UserSettingsDAO;
import org.example.newchronopos.model.settings.SystemSettings;
import org.example.newchronopos.model.settings.CompanySettings;
import org.example.newchronopos.model.settings.UserSettings;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Settings Service Implementation with caching and hierarchical lookup
 */
public class SettingsServiceImpl implements SettingsService {

    private final SystemSettingsDAO systemSettingsDAO;
    private final CompanySettingsDAO companySettingsDAO;
    private final UserSettingsDAO userSettingsDAO;
    
    // Cache for frequently accessed settings
    private final Map<String, String> systemCache = new ConcurrentHashMap<>();
    private final Map<String, String> companyCache = new ConcurrentHashMap<>();
    private final Map<String, String> userCache = new ConcurrentHashMap<>();
    
    // Cache timeout (in milliseconds)
    private static final long CACHE_TIMEOUT = 300000; // 5 minutes
    private long lastCacheUpdate = 0;

    public SettingsServiceImpl() {
        this.systemSettingsDAO = new SystemSettingsDAO();
        this.companySettingsDAO = new CompanySettingsDAO();
        this.userSettingsDAO = new UserSettingsDAO();
        
        // Initialize database tables
        initializeTables();
    }

    private void initializeTables() {
        try {
            systemSettingsDAO.createTableIfNotExists();
            companySettingsDAO.createTableIfNotExists();
            userSettingsDAO.createTableIfNotExists();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize settings tables", e);
        }
    }

    // =====================================================
    // SYSTEM SETTINGS
    // =====================================================

    @Override
    public Optional<String> getSystemSetting(String key) {
        // Check cache first
        String cacheKey = "sys_" + key;
        if (isCacheValid() && systemCache.containsKey(cacheKey)) {
            return Optional.ofNullable(systemCache.get(cacheKey));
        }

        // Fetch from database
        Optional<SystemSettings> setting = systemSettingsDAO.findByKey(key);
        String value = setting.map(SystemSettings::getValueOrDefault).orElse(null);
        
        // Update cache
        systemCache.put(cacheKey, value);
        
        return Optional.ofNullable(value);
    }

    @Override
    public boolean getSystemBooleanSetting(String key, boolean defaultValue) {
        return getSystemSetting(key)
                .map(value -> "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value))
                .orElse(defaultValue);
    }

    @Override
    public int getSystemIntegerSetting(String key, int defaultValue) {
        return getSystemSetting(key)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    @Override
    public double getSystemDecimalSetting(String key, double defaultValue) {
        return getSystemSetting(key)
                .map(value -> {
                    try {
                        return Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    @Override
    public void setSystemSetting(String key, String value) {
        setSystemSetting(key, value, "GENERAL", null, "STRING");
    }

    @Override
    public void setSystemSetting(String key, String value, String category) {
        setSystemSetting(key, value, category, null, "STRING");
    }

    @Override
    public void setSystemSetting(String key, String value, String category, String description, String dataType) {
        Optional<SystemSettings> existingSetting = systemSettingsDAO.findByKey(key);
        
        if (existingSetting.isPresent()) {
            SystemSettings setting = existingSetting.get();
            setting.setSettingValue(value);
            if (category != null) setting.setCategory(category);
            if (description != null) setting.setDescription(description);
            if (dataType != null) setting.setDataType(dataType);
            systemSettingsDAO.save(setting);
        } else {
            SystemSettings newSetting = new SystemSettings(key, value, category != null ? category : "GENERAL", dataType != null ? dataType : "STRING");
            if (description != null) newSetting.setDescription(description);
            systemSettingsDAO.save(newSetting);
        }
        
        // Update cache
        systemCache.put("sys_" + key, value);
    }

    @Override
    public List<SystemSettings> getSystemSettingsByCategory(String category) {
        return systemSettingsDAO.findByCategory(category);
    }

    @Override
    public List<SystemSettings> getAllSystemSettings() {
        return systemSettingsDAO.findAll();
    }

    // =====================================================
    // COMPANY SETTINGS
    // =====================================================

    @Override
    public Optional<String> getCompanySetting(Long companyId, String key) {
        String cacheKey = "comp_" + companyId + "_" + key;
        if (isCacheValid() && companyCache.containsKey(cacheKey)) {
            return Optional.ofNullable(companyCache.get(cacheKey));
        }

        Optional<CompanySettings> setting = companySettingsDAO.findByCompanyAndKey(companyId, key);
        String value = setting.map(CompanySettings::getSettingValue).orElse(null);
        
        companyCache.put(cacheKey, value);
        return Optional.ofNullable(value);
    }

    @Override
    public boolean getCompanyBooleanSetting(Long companyId, String key, boolean defaultValue) {
        return getCompanySetting(companyId, key)
                .map(value -> "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value))
                .orElse(defaultValue);
    }

    @Override
    public int getCompanyIntegerSetting(Long companyId, String key, int defaultValue) {
        return getCompanySetting(companyId, key)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    @Override
    public double getCompanyDecimalSetting(Long companyId, String key, double defaultValue) {
        return getCompanySetting(companyId, key)
                .map(value -> {
                    try {
                        return Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    @Override
    public void setCompanySetting(Long companyId, String key, String value) {
        setCompanySetting(companyId, key, value, "GENERAL", null, "STRING");
    }

    @Override
    public void setCompanySetting(Long companyId, String key, String value, String category) {
        setCompanySetting(companyId, key, value, category, null, "STRING");
    }

    @Override
    public void setCompanySetting(Long companyId, String key, String value, String category, String description, String dataType) {
        Optional<CompanySettings> existingSetting = companySettingsDAO.findByCompanyAndKey(companyId, key);
        
        if (existingSetting.isPresent()) {
            CompanySettings setting = existingSetting.get();
            setting.setSettingValue(value);
            if (category != null) setting.setCategory(category);
            if (description != null) setting.setDescription(description);
            if (dataType != null) setting.setDataType(dataType);
            companySettingsDAO.save(setting);
        } else {
            CompanySettings newSetting = new CompanySettings(companyId, key, value, category != null ? category : "GENERAL", dataType != null ? dataType : "STRING");
            if (description != null) newSetting.setDescription(description);
            companySettingsDAO.save(newSetting);
        }
        
        companyCache.put("comp_" + companyId + "_" + key, value);
    }

    @Override
    public List<CompanySettings> getCompanySettingsByCategory(Long companyId, String category) {
        return companySettingsDAO.findByCompanyAndCategory(companyId, category);
    }

    @Override
    public List<CompanySettings> getAllCompanySettings(Long companyId) {
        return companySettingsDAO.findByCompany(companyId);
    }

    // =====================================================
    // USER SETTINGS
    // =====================================================

    @Override
    public Optional<String> getUserSetting(Long userId, String key) {
        String cacheKey = "user_" + userId + "_" + key;
        if (isCacheValid() && userCache.containsKey(cacheKey)) {
            return Optional.ofNullable(userCache.get(cacheKey));
        }

        Optional<UserSettings> setting = userSettingsDAO.findByUserAndKey(userId, key);
        String value = setting.map(UserSettings::getSettingValue).orElse(null);
        
        userCache.put(cacheKey, value);
        return Optional.ofNullable(value);
    }

    @Override
    public boolean getUserBooleanSetting(Long userId, String key, boolean defaultValue) {
        return getUserSetting(userId, key)
                .map(value -> "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value))
                .orElse(defaultValue);
    }

    @Override
    public int getUserIntegerSetting(Long userId, String key, int defaultValue) {
        return getUserSetting(userId, key)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    @Override
    public double getUserDecimalSetting(Long userId, String key, double defaultValue) {
        return getUserSetting(userId, key)
                .map(value -> {
                    try {
                        return Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    @Override
    public void setUserSetting(Long userId, String key, String value) {
        setUserSetting(userId, key, value, "GENERAL", null, "STRING");
    }

    @Override
    public void setUserSetting(Long userId, String key, String value, String category) {
        setUserSetting(userId, key, value, category, null, "STRING");
    }

    @Override
    public void setUserSetting(Long userId, String key, String value, String category, String description, String dataType) {
        Optional<UserSettings> existingSetting = userSettingsDAO.findByUserAndKey(userId, key);
        
        if (existingSetting.isPresent()) {
            UserSettings setting = existingSetting.get();
            setting.setSettingValue(value);
            if (category != null) setting.setCategory(category);
            if (description != null) setting.setDescription(description);
            if (dataType != null) setting.setDataType(dataType);
            userSettingsDAO.save(setting);
        } else {
            UserSettings newSetting = new UserSettings(userId, key, value, category != null ? category : "GENERAL", dataType != null ? dataType : "STRING");
            if (description != null) newSetting.setDescription(description);
            userSettingsDAO.save(newSetting);
        }
        
        userCache.put("user_" + userId + "_" + key, value);
    }

    @Override
    public List<UserSettings> getUserSettingsByCategory(Long userId, String category) {
        return userSettingsDAO.findByUserAndCategory(userId, category);
    }

    @Override
    public List<UserSettings> getAllUserSettings(Long userId) {
        return userSettingsDAO.findByUser(userId);
    }

    // =====================================================
    // HIERARCHICAL SETTINGS
    // =====================================================

    @Override
    public Optional<String> getSetting(Long userId, Long companyId, String key) {
        // User setting has highest priority
        if (userId != null) {
            Optional<String> userSetting = getUserSetting(userId, key);
            if (userSetting.isPresent()) {
                return userSetting;
            }
        }
        
        // Company setting has medium priority
        if (companyId != null) {
            Optional<String> companySetting = getCompanySetting(companyId, key);
            if (companySetting.isPresent()) {
                return companySetting;
            }
        }
        
        // System setting has lowest priority
        return getSystemSetting(key);
    }

    @Override
    public boolean getBooleanSetting(Long userId, Long companyId, String key, boolean defaultValue) {
        return getSetting(userId, companyId, key)
                .map(value -> "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value))
                .orElse(defaultValue);
    }

    @Override
    public int getIntegerSetting(Long userId, Long companyId, String key, int defaultValue) {
        return getSetting(userId, companyId, key)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    @Override
    public double getDecimalSetting(Long userId, Long companyId, String key, double defaultValue) {
        return getSetting(userId, companyId, key)
                .map(value -> {
                    try {
                        return Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    @Override
    public String getStringSetting(Long userId, Long companyId, String key, String defaultValue) {
        return getSetting(userId, companyId, key).orElse(defaultValue);
    }

    // =====================================================
    // BULK OPERATIONS
    // =====================================================

    @Override
    public void importSystemSettings(Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            setSystemSetting(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void importCompanySettings(Long companyId, Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            setCompanySetting(companyId, entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void importUserSettings(Long userId, Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            setUserSetting(userId, entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Map<String, String> exportSystemSettings() {
        Map<String, String> settings = new HashMap<>();
        getAllSystemSettings().forEach(setting -> 
            settings.put(setting.getSettingKey(), setting.getSettingValue()));
        return settings;
    }

    @Override
    public Map<String, String> exportCompanySettings(Long companyId) {
        Map<String, String> settings = new HashMap<>();
        getAllCompanySettings(companyId).forEach(setting -> 
            settings.put(setting.getSettingKey(), setting.getSettingValue()));
        return settings;
    }

    @Override
    public Map<String, String> exportUserSettings(Long userId) {
        Map<String, String> settings = new HashMap<>();
        getAllUserSettings(userId).forEach(setting -> 
            settings.put(setting.getSettingKey(), setting.getSettingValue()));
        return settings;
    }

    // =====================================================
    // VALIDATION & UTILITIES
    // =====================================================

    @Override
    public boolean isValidSettingValue(String key, String value, String dataType) {
        if (value == null) return true; // null is always valid
        
        try {
            switch (dataType.toUpperCase()) {
                case "BOOLEAN":
                    return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value) ||
                           "1".equals(value) || "0".equals(value) ||
                           "yes".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value);
                case "INTEGER":
                    Integer.parseInt(value);
                    return true;
                case "DECIMAL":
                    Double.parseDouble(value);
                    return true;
                case "DATE":
                    // Add date validation if needed
                    return true;
                case "STRING":
                default:
                    return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public List<String> getSystemCategories() {
        return systemSettingsDAO.getCategories();
    }

    @Override
    public List<String> getCompanyCategories(Long companyId) {
        return companySettingsDAO.getCategoriesByCompany(companyId);
    }

    @Override
    public List<String> getUserCategories(Long userId) {
        return userSettingsDAO.getCategoriesByUser(userId);
    }

    @Override
    public void initializeDefaultSettings() {
        System.out.println("🔧 Setting up comprehensive default settings...");
        
        // System Settings - General
        setSystemSetting("app.name", "ChronoPOS", "GENERAL", "Application name displayed in UI", "STRING");
        setSystemSetting("app.version", "1.0.0", "GENERAL", "Current application version", "STRING");
        setSystemSetting("app.description", "Professional Point of Sale System", "GENERAL", "Application description", "STRING");
        
        // System Settings - Localization  
        setSystemSetting("app.language", "en", "LOCALIZATION", "Default language", "STRING");
        setSystemSetting("app.currency", "USD", "LOCALIZATION", "Default currency", "STRING");
        setSystemSetting("app.currency_symbol", "$", "LOCALIZATION", "Currency symbol", "STRING");
        setSystemSetting("app.date_format", "MM/dd/yyyy", "LOCALIZATION", "Date format", "STRING");
        setSystemSetting("app.time_format", "HH:mm:ss", "LOCALIZATION", "Time format", "STRING");
        setSystemSetting("app.decimal_places", "2", "LOCALIZATION", "Decimal places for currency", "INTEGER");
        setSystemSetting("app.timezone", "America/New_York", "LOCALIZATION", "System timezone", "STRING");
        
        // System Settings - Security
        setSystemSetting("security.session_timeout", "60", "SECURITY", "Session timeout in minutes", "INTEGER");
        setSystemSetting("security.password_min_length", "8", "SECURITY", "Minimum password length", "INTEGER");
        setSystemSetting("security.max_login_attempts", "5", "SECURITY", "Maximum login attempts", "INTEGER");
        setSystemSetting("security.auto_logout", "true", "SECURITY", "Enable automatic logout", "BOOLEAN");
        setSystemSetting("security.require_password_change", "false", "SECURITY", "Require periodic password changes", "BOOLEAN");
        setSystemSetting("security.enable_2fa", "false", "SECURITY", "Enable two-factor authentication", "BOOLEAN");
        
        // System Settings - Backup
        setSystemSetting("backup.auto_backup", "true", "BACKUP", "Enable automatic backup", "BOOLEAN");
        setSystemSetting("backup.backup_interval", "24", "BACKUP", "Backup interval in hours", "INTEGER");
        setSystemSetting("backup.backup_location", "./backups", "BACKUP", "Backup directory path", "STRING");
        setSystemSetting("backup.retention_days", "30", "BACKUP", "Days to keep backup files", "INTEGER");
        setSystemSetting("backup.compress_backups", "true", "BACKUP", "Compress backup files", "BOOLEAN");
        
        // System Settings - UI/UX
        setSystemSetting("ui.theme", "dark", "UI", "Application theme (dark/light)", "STRING");
        setSystemSetting("ui.sidebar_expanded", "true", "UI", "Sidebar expanded by default", "BOOLEAN");
        setSystemSetting("ui.page_size", "25", "UI", "Default items per page", "INTEGER");
        setSystemSetting("ui.enable_animations", "true", "UI", "Enable UI animations", "BOOLEAN");
        setSystemSetting("ui.show_tooltips", "true", "UI", "Show helpful tooltips", "BOOLEAN");
        setSystemSetting("ui.auto_refresh", "true", "UI", "Auto-refresh data", "BOOLEAN");
        setSystemSetting("ui.refresh_interval", "30", "UI", "Auto-refresh interval in seconds", "INTEGER");
        
        // System Settings - Financial
        setSystemSetting("financial.tax_enabled", "true", "FINANCIAL", "Enable tax calculations", "BOOLEAN");
        setSystemSetting("financial.default_tax_rate", "8.25", "FINANCIAL", "Default tax rate percentage", "DECIMAL");
        setSystemSetting("financial.tax_inclusive", "false", "FINANCIAL", "Prices include tax", "BOOLEAN");
        setSystemSetting("financial.discount_enabled", "true", "FINANCIAL", "Enable discounts", "BOOLEAN");
        setSystemSetting("financial.max_discount", "50.0", "FINANCIAL", "Maximum discount percentage", "DECIMAL");
        setSystemSetting("financial.rounding_mode", "HALF_UP", "FINANCIAL", "Currency rounding mode", "STRING");
        
        // System Settings - Printing
        setSystemSetting("printing.receipt_printer", "default", "PRINTING", "Default receipt printer", "STRING");
        setSystemSetting("printing.receipt_copies", "1", "PRINTING", "Number of receipt copies", "INTEGER");
        setSystemSetting("printing.receipt_width", "80", "PRINTING", "Receipt width in characters", "INTEGER");
        setSystemSetting("printing.auto_print", "true", "PRINTING", "Auto-print receipts", "BOOLEAN");
        setSystemSetting("printing.receipt_header", "Welcome to ChronoPOS", "PRINTING", "Receipt header text", "STRING");
        setSystemSetting("printing.receipt_footer", "Thank you for your business!", "PRINTING", "Receipt footer text", "STRING");
        
        // System Settings - Inventory
        setSystemSetting("inventory.low_stock_alert", "true", "INVENTORY", "Enable low stock alerts", "BOOLEAN");
        setSystemSetting("inventory.low_stock_threshold", "10", "INVENTORY", "Low stock threshold quantity", "INTEGER");
        setSystemSetting("inventory.auto_reorder", "false", "INVENTORY", "Enable automatic reordering", "BOOLEAN");
        setSystemSetting("inventory.track_expiry", "true", "INVENTORY", "Track product expiry dates", "BOOLEAN");
        
        // Initialize default company settings (for company ID 1)
        setCompanySetting(1L, "company.name", "Default Company", "GENERAL", "Company name", "STRING");
        setCompanySetting(1L, "company.address_line1", "123 Main Street", "GENERAL", "Address line 1", "STRING");
        setCompanySetting(1L, "company.address_line2", "", "GENERAL", "Address line 2", "STRING");
        setCompanySetting(1L, "company.city", "Anytown", "GENERAL", "City", "STRING");
        setCompanySetting(1L, "company.state", "ST", "GENERAL", "State/Province", "STRING");
        setCompanySetting(1L, "company.postal_code", "12345", "GENERAL", "Postal/ZIP code", "STRING");
        setCompanySetting(1L, "company.country", "USA", "GENERAL", "Country", "STRING");
        setCompanySetting(1L, "company.phone", "(555) 123-4567", "GENERAL", "Phone number", "STRING");
        setCompanySetting(1L, "company.email", "info@company.com", "GENERAL", "Email address", "EMAIL");
        setCompanySetting(1L, "company.website", "www.company.com", "GENERAL", "Website URL", "URL");
        setCompanySetting(1L, "company.tax_id", "12-3456789", "FINANCIAL", "Tax ID number", "STRING");
        setCompanySetting(1L, "company.business_hours", "Mon-Fri: 9:00 AM - 6:00 PM", "GENERAL", "Business hours", "STRING");
        setCompanySetting(1L, "company.logo_path", "", "GENERAL", "Company logo file path", "STRING");
        
        // Initialize default user settings (for admin user ID 1)
        setUserSetting(1L, "user.dashboard_layout", "grid", "UI", "Dashboard layout preference", "STRING");
        setUserSetting(1L, "user.items_per_page", "25", "UI", "Items per page preference", "INTEGER");
        setUserSetting(1L, "user.notification_sound", "true", "UI", "Enable notification sounds", "BOOLEAN");
        setUserSetting(1L, "user.email_notifications", "true", "NOTIFICATIONS", "Enable email notifications", "BOOLEAN");
        setUserSetting(1L, "user.auto_logout_warning", "true", "UI", "Show auto-logout warning", "BOOLEAN");
        setUserSetting(1L, "user.quick_access_items", "", "WORKFLOW", "Quick access product IDs", "STRING");
        setUserSetting(1L, "user.default_customer", "Walk-in Customer", "WORKFLOW", "Default customer name", "STRING");
        setUserSetting(1L, "user.preferred_payment", "Cash", "WORKFLOW", "Preferred payment method", "STRING");
        
        System.out.println("✅ Comprehensive default settings initialized successfully");
    }

    @Override
    public void resetSystemSettingsToDefault() {
        // Implementation for resetting system settings
        initializeDefaultSettings();
    }

    @Override
    public void resetCompanySettingsToDefault(Long companyId) {
        // Implementation for resetting company settings
        // Delete all company settings and let them fall back to system defaults
    }

    @Override
    public void resetUserSettingsToDefault(Long userId) {
        // Implementation for resetting user settings
        // Delete all user settings and let them fall back to company/system defaults
    }

    // =====================================================
    // CACHE MANAGEMENT
    // =====================================================

    @Override
    public void clearCache() {
        systemCache.clear();
        companyCache.clear();
        userCache.clear();
        lastCacheUpdate = 0;
    }

    @Override
    public void clearSystemCache() {
        systemCache.clear();
    }

    @Override
    public void clearCompanyCache(Long companyId) {
        companyCache.entrySet().removeIf(entry -> 
            entry.getKey().startsWith("comp_" + companyId + "_"));
    }

    @Override
    public void clearUserCache(Long userId) {
        userCache.entrySet().removeIf(entry -> 
            entry.getKey().startsWith("user_" + userId + "_"));
    }

    private boolean isCacheValid() {
        return (System.currentTimeMillis() - lastCacheUpdate) < CACHE_TIMEOUT;
    }
}
