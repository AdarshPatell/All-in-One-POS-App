package org.example.newchronopos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GoodsReplaced {
    private int replacedId;
    private String replacedNo;
    private LocalDate replacedDate;
    private int customerId;
    private int storeLocationId;
    private String status;
    private String remarks;
    private String reason;
    private int createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional fields for display
    private String customer;

    // Additional fields needed for compilation
    private int id;
    private int originalProductId;
    private int replacementProductId;
    private int quantity;
    private LocalDateTime replacementDate;
    private String notes;

    public GoodsReplaced() {}

    public GoodsReplaced(String replacedNo, String replacedDate, String customer, String reason, String status) {
        this.replacedNo = replacedNo;
        this.customer = customer;
        this.reason = reason;
        this.status = status;
    }

    // Getters and setters
    public int getReplacedId() { return replacedId; }
    public void setReplacedId(int replacedId) { this.replacedId = replacedId; }

    public String getReplacedNo() { return replacedNo; }
    public void setReplacedNo(String replacedNo) { this.replacedNo = replacedNo; }

    public LocalDate getReplacedDate() { return replacedDate; }
    public void setReplacedDate(LocalDate replacedDate) { this.replacedDate = replacedDate; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getStoreLocationId() { return storeLocationId; }
    public void setStoreLocationId(int storeLocationId) { this.storeLocationId = storeLocationId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOriginalProductId() { return originalProductId; }
    public void setOriginalProductId(int originalProductId) { this.originalProductId = originalProductId; }

    public int getReplacementProductId() { return replacementProductId; }
    public void setReplacementProductId(int replacementProductId) { this.replacementProductId = replacementProductId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getReplacementDate() { return replacementDate; }
    public void setReplacementDate(LocalDateTime replacementDate) { this.replacementDate = replacementDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
