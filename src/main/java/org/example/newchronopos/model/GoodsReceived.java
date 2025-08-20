package org.example.newchronopos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GoodsReceived {
    private int id;  // Changed from receivedId to id for consistency
    private String receivedNo;
    private LocalDate receivedDate;
    private int supplierId;
    private int productId;  // Added missing field
    private int quantityReceived;  // Added missing field
    private double unitCost;  // Added missing field
    private int storeLocationId;
    private String status;
    private String notes;  // Changed from remarks to notes
    private double totalAmount;
    private int createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional fields for display compatibility
    private String supplier;
    private String receiptNo;
    private String receiptDate;

    public GoodsReceived() {}

    public GoodsReceived(String receivedNo, String receivedDate, String supplier, String totalAmount, String status) {
        this.receivedNo = receivedNo;
        this.receiptNo = receivedNo;
        this.receiptDate = receivedDate;
        this.supplier = supplier;
        this.totalAmount = Double.parseDouble(totalAmount.replace("$", ""));
        this.status = status;
    }

    // Main getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getReceivedNo() { return receivedNo; }
    public void setReceivedNo(String receivedNo) { this.receivedNo = receivedNo; }

    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantityReceived() { return quantityReceived; }
    public void setQuantityReceived(int quantityReceived) { this.quantityReceived = quantityReceived; }

    public double getUnitCost() { return unitCost; }
    public void setUnitCost(double unitCost) { this.unitCost = unitCost; }

    public int getStoreLocationId() { return storeLocationId; }
    public void setStoreLocationId(int storeLocationId) { this.storeLocationId = storeLocationId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Backward compatibility methods
    public int getReceivedId() { return id; }
    public void setReceivedId(int receivedId) { this.id = receivedId; }

    public String getRemarks() { return notes; }
    public void setRemarks(String remarks) { this.notes = remarks; }

    // Display compatibility methods
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getReceiptNo() { return receiptNo; }
    public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }

    public String getReceiptDate() { return receiptDate; }
    public void setReceiptDate(String receiptDate) { this.receiptDate = receiptDate; }
}
