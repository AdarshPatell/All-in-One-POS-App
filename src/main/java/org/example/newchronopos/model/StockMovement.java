package org.example.newchronopos.model;

import java.time.LocalDateTime;

public class StockMovement {
    private int id;
    private int productId;
    private int batchId;
    private int uomId;
    private String movementType; // Purchase, Sale, Transfer, Adjustment, Waste
    private double quantity;
    private String referenceType; // PurchaseOrder, Sale, Transfer, Adjustment
    private int referenceId;
    private int locationId;
    private String notes;
    private int createdBy;
    private LocalDateTime createdAt;

    // Constructors
    public StockMovement() {}

    public StockMovement(int productId, int uomId, String movementType, double quantity, 
                        String referenceType, int referenceId, int locationId, String notes, int createdBy) {
        this.productId = productId;
        this.uomId = uomId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.locationId = locationId;
        this.notes = notes;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getBatchId() { return batchId; }
    public void setBatchId(int batchId) { this.batchId = batchId; }

    public int getUomId() { return uomId; }
    public void setUomId(int uomId) { this.uomId = uomId; }

    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public int getReferenceId() { return referenceId; }
    public void setReferenceId(int referenceId) { this.referenceId = referenceId; }

    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
