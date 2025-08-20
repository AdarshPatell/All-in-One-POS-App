package org.example.newchronopos.model;

import java.math.BigDecimal;

public class ProductPrice {
    private int id;
    private int productId;
    private String priceType; // Online, Offline, Wholesale
    private String unitOption; // Per Piece, Per Kg, Per Liter
    private BigDecimal cost;
    private BigDecimal price;
    private String color;
    private String status;
    private java.time.LocalDateTime createdAt;

    // Constructors
    public ProductPrice() {
        this.status = "Active";
        this.createdAt = java.time.LocalDateTime.now();
    }

    public ProductPrice(int productId, String priceType, String unitOption, BigDecimal cost, BigDecimal price, String color) {
        this();
        this.productId = productId;
        this.priceType = priceType;
        this.unitOption = unitOption;
        this.cost = cost;
        this.price = price;
        this.color = color;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getPriceType() { return priceType; }
    public void setPriceType(String priceType) { this.priceType = priceType; }

    public String getUnitOption() { return unitOption; }
    public void setUnitOption(String unitOption) { this.unitOption = unitOption; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}
