package org.example.newchronopos.model;

import java.time.LocalDateTime;

public class Category {
    private int id;
    private String name;
    private String description;
    private Integer parentId;
    private String imageUrl;
    private int displayOrder;
    private String status;
    private Integer createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Category() {}

    public Category(int id, String name, String description, Integer parentId,
                    String imageUrl, String status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parentId = parentId;
        this.imageUrl = imageUrl;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods for backward compatibility
    public boolean isActive() {
        return "Active".equals(status);
    }

    public void setActive(boolean active) {
        this.status = active ? "Active" : "Inactive";
    }

    @Override
    public String toString() {
        return name; // For ComboBox display
    }
}