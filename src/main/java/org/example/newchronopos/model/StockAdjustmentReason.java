
package org.example.newchronopos.model;

public class StockAdjustmentReason {
    private int stockAdjustmentReasonsId;
    private String name;
    private String description;
    private String status;
    private int createdBy;
    private java.time.LocalDateTime createdAt;
    private int updatedBy;
    private java.time.LocalDateTime updatedAt;
    private java.time.LocalDateTime deletedAt;

    // Constructors
    public StockAdjustmentReason() {}

    public StockAdjustmentReason(String name, String description, String status, int createdBy) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = java.time.LocalDateTime.now();
        this.updatedAt = java.time.LocalDateTime.now();
    }

    // Getters and Setters
    public int getStockAdjustmentReasonsId() { return stockAdjustmentReasonsId; }
    public void setStockAdjustmentReasonsId(int stockAdjustmentReasonsId) { this.stockAdjustmentReasonsId = stockAdjustmentReasonsId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(int updatedBy) { this.updatedBy = updatedBy; }

    public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public java.time.LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(java.time.LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
