package org.example.newchronopos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StockAdjustment {
    private int adjustmentId;
    private String adjustmentNo;
    private LocalDate adjustmentDate;
    private int storeLocationId;
    private int reasonId;
    private String status;
    private String remarks;
    private int createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional fields needed for compilation
    private int id;
    private int productId;
    private int adjustmentQuantity;
    private String adjustmentType;
    private String reason;

    // Constructors
    public StockAdjustment() {}

    public StockAdjustment(String adjustmentNo, LocalDate adjustmentDate, int storeLocationId, 
                          int reasonId, String status, String remarks, int createdBy) {
        this.adjustmentNo = adjustmentNo;
        this.adjustmentDate = adjustmentDate;
        this.storeLocationId = storeLocationId;
        this.reasonId = reasonId;
        this.status = status;
        this.remarks = remarks;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getAdjustmentId() { return adjustmentId; }
    public void setAdjustmentId(int adjustmentId) { this.adjustmentId = adjustmentId; }

    public String getAdjustmentNo() { return adjustmentNo; }
    public void setAdjustmentNo(String adjustmentNo) { this.adjustmentNo = adjustmentNo; }

    public LocalDate getAdjustmentDate() { return adjustmentDate; }
    public void setAdjustmentDate(LocalDate adjustmentDate) { this.adjustmentDate = adjustmentDate; }

    public int getStoreLocationId() { return storeLocationId; }
    public void setStoreLocationId(int storeLocationId) { this.storeLocationId = storeLocationId; }

    public int getReasonId() { return reasonId; }
    public void setReasonId(int reasonId) { this.reasonId = reasonId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getAdjustmentQuantity() { return adjustmentQuantity; }
    public void setAdjustmentQuantity(int adjustmentQuantity) { this.adjustmentQuantity = adjustmentQuantity; }

    public String getAdjustmentType() { return adjustmentType; }
    public void setAdjustmentType(String adjustmentType) { this.adjustmentType = adjustmentType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
