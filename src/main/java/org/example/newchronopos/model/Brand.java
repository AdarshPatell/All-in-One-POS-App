package org.example.newchronopos.model;

import java.time.LocalDateTime;

public class Brand {
    private int id;
    private String name;
    private String nameArabic;
    private String description;
    private String logoUrl;
    private String website;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Brand() {}

    public Brand(int id, String name, String nameArabic, String description, String status) {
        this.id = id;
        this.name = name;
        this.nameArabic = nameArabic;
        this.description = description;
        this.status = status;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameArabic() { return nameArabic; }
    public void setNameArabic(String nameArabic) { this.nameArabic = nameArabic; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Backward compatibility methods for existing controllers
    public String getBrandId() { return String.valueOf(id); }
    public void setBrandId(String brandId) { this.id = Integer.parseInt(brandId); }

    public String getBrandName() { return name; }
    public void setBrandName(String brandName) { this.name = brandName; }

    public String getBrandNameArabic() { return nameArabic; }
    public void setBrandNameArabic(String brandNameArabic) { this.nameArabic = brandNameArabic; }
}
