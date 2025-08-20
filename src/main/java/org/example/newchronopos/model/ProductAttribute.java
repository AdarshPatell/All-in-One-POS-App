package org.example.newchronopos.model;

public class ProductAttribute {
    private int id;
    private int productId;
    private String attributeName;
    private String attributeValue;
    private String attributeValueAr;
    private String status;
    private java.time.LocalDateTime createdAt;

    // Constructors
    public ProductAttribute() {
        this.status = "Active";
        this.createdAt = java.time.LocalDateTime.now();
    }

    public ProductAttribute(int productId, String attributeName, String attributeValue, String attributeValueAr) {
        this();
        this.productId = productId;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.attributeValueAr = attributeValueAr;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getAttributeName() { return attributeName; }
    public void setAttributeName(String attributeName) { this.attributeName = attributeName; }

    public String getAttributeValue() { return attributeValue; }
    public void setAttributeValue(String attributeValue) { this.attributeValue = attributeValue; }

    public String getAttributeValueAr() { return attributeValueAr; }
    public void setAttributeValueAr(String attributeValueAr) { this.attributeValueAr = attributeValueAr; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}
