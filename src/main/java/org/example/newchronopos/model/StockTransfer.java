package org.example.newchronopos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StockTransfer {
    private int id;  // Changed from transferId
    private String transferNo;
    private int productId;  // Added for product reference
    private String fromLocation;  // Changed from fromStoreId to string location
    private String toLocation;    // Changed from toStoreId to string location
    private int quantity;         // Added quantity field
    private LocalDate transferDate;
    private String status;
    private String notes;         // Changed from remarks
    private int createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public StockTransfer() {}

    public StockTransfer(String transferNo, int productId, String fromLocation, String toLocation,
                        int quantity, LocalDate transferDate, String status, String notes, int createdBy) {
        this.transferNo = transferNo;
        this.productId = productId;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.quantity = quantity;
        this.transferDate = transferDate;
        this.status = status;
        this.notes = notes;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTransferNo() { return transferNo; }
    public void setTransferNo(String transferNo) { this.transferNo = transferNo; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getFromLocation() { return fromLocation; }
    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }

    public String getToLocation() { return toLocation; }
    public void setToLocation(String toLocation) { this.toLocation = toLocation; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDate getTransferDate() { return transferDate; }
    public void setTransferDate(LocalDate transferDate) { this.transferDate = transferDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Backward compatibility methods
    public int getTransferId() { return id; }
    public void setTransferId(int transferId) { this.id = transferId; }

    public int getFromStoreId() { return 0; } // For backward compatibility
    public void setFromStoreId(int fromStoreId) { /* No-op for compatibility */ }

    public int getToStoreId() { return 0; } // For backward compatibility
    public void setToStoreId(int toStoreId) { /* No-op for compatibility */ }

    public String getRemarks() { return notes; }
    public void setRemarks(String remarks) { this.notes = remarks; }
}
