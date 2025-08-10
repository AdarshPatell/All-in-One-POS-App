package org.example.newchronopos.model.settings;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * System-level settings that apply globally across the application
 */
public class SystemSettings {
    private Long id;
    private String settingKey;
    private String settingValue;
    private String category;
    private String description;
    private String dataType; // STRING, INTEGER, BOOLEAN, DECIMAL, DATE
    private String defaultValue;
    private boolean isRequired;
    private boolean isEncrypted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public SystemSettings() {}

    public SystemSettings(String settingKey, String settingValue, String category, String dataType) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.category = category;
        this.dataType = dataType;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public SystemSettings(String settingKey, String settingValue, String category, String description, 
                         String dataType, String defaultValue, boolean isRequired) {
        this(settingKey, settingValue, category, dataType);
        this.description = description;
        this.defaultValue = defaultValue;
        this.isRequired = isRequired;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String settingKey) { this.settingKey = settingKey; }

    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String settingValue) { 
        this.settingValue = settingValue;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    public boolean isRequired() { return isRequired; }
    public void setRequired(boolean required) { isRequired = required; }

    public boolean isEncrypted() { return isEncrypted; }
    public void setEncrypted(boolean encrypted) { isEncrypted = encrypted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Utility methods
    public boolean hasValue() {
        return settingValue != null && !settingValue.trim().isEmpty();
    }

    public String getValueOrDefault() {
        return hasValue() ? settingValue : defaultValue;
    }

    // Type conversion helpers
    public boolean getBooleanValue() {
        String value = getValueOrDefault();
        return value != null && ("true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value));
    }

    public int getIntegerValue() {
        try {
            return Integer.parseInt(getValueOrDefault());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public double getDecimalValue() {
        try {
            return Double.parseDouble(getValueOrDefault());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemSettings that = (SystemSettings) o;
        return Objects.equals(settingKey, that.settingKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(settingKey);
    }

    @Override
    public String toString() {
        return "SystemSettings{" +
                "id=" + id +
                ", settingKey='" + settingKey + '\'' +
                ", settingValue='" + settingValue + '\'' +
                ", category='" + category + '\'' +
                ", dataType='" + dataType + '\'' +
                '}';
    }
}
