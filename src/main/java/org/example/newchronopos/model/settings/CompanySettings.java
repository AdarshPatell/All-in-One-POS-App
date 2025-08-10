package org.example.newchronopos.model.settings;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Company-specific settings that apply to a particular company/organization
 */
public class CompanySettings {
    private Long id;
    private Long companyId;
    private String settingKey;
    private String settingValue;
    private String category;
    private String description;
    private String dataType; // STRING, INTEGER, BOOLEAN, DECIMAL, DATE
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public CompanySettings() {}

    public CompanySettings(Long companyId, String settingKey, String settingValue, String category, String dataType) {
        this.companyId = companyId;
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.category = category;
        this.dataType = dataType;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public CompanySettings(Long companyId, String settingKey, String settingValue, String category, 
                          String description, String dataType) {
        this(companyId, settingKey, settingValue, category, dataType);
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

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

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Utility methods
    public boolean hasValue() {
        return settingValue != null && !settingValue.trim().isEmpty();
    }

    // Type conversion helpers
    public boolean getBooleanValue() {
        return settingValue != null && ("true".equalsIgnoreCase(settingValue) || "1".equals(settingValue) || "yes".equalsIgnoreCase(settingValue));
    }

    public int getIntegerValue() {
        try {
            return settingValue != null ? Integer.parseInt(settingValue) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public double getDecimalValue() {
        try {
            return settingValue != null ? Double.parseDouble(settingValue) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanySettings that = (CompanySettings) o;
        return Objects.equals(companyId, that.companyId) && Objects.equals(settingKey, that.settingKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId, settingKey);
    }

    @Override
    public String toString() {
        return "CompanySettings{" +
                "id=" + id +
                ", companyId=" + companyId +
                ", settingKey='" + settingKey + '\'' +
                ", settingValue='" + settingValue + '\'' +
                ", category='" + category + '\'' +
                ", dataType='" + dataType + '\'' +
                '}';
    }
}
