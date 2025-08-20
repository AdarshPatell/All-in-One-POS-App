package org.example.newchronopos.model;

public class ProductBarcode {
    private int id;
    private int productId;
    private String name;
    private String barcode;
    private boolean isStandard;
    private boolean isDefault;
    private String status;
    private java.time.LocalDateTime createdAt;

    // Constructors
    public ProductBarcode() {
        this.status = "Active";
        this.createdAt = java.time.LocalDateTime.now();
    }

    public ProductBarcode(int productId, String name, String barcode, boolean isStandard, boolean isDefault) {
        this();
        this.productId = productId;
        this.name = name;
        this.barcode = barcode;
        this.isStandard = isStandard;
        this.isDefault = isDefault;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public boolean isStandard() { return isStandard; }
    public void setStandard(boolean standard) { isStandard = standard; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}
