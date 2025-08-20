package org.example.newchronopos.model;

import java.math.BigDecimal;

public class ProductTax {
    private int id;
    private int productId;
    private String taxType; // VAT 5%, VAT 15%, Exempt
    private BigDecimal taxRate;
    private boolean appliedToSelling;
    private boolean appliedToBuying;
    private boolean includeInPrice;
    private String status;
    private java.time.LocalDateTime createdAt;

    // Constructors
    public ProductTax() {
        this.status = "Active";
        this.createdAt = java.time.LocalDateTime.now();
    }

    public ProductTax(int productId, String taxType, BigDecimal taxRate, boolean appliedToSelling,
                     boolean appliedToBuying, boolean includeInPrice) {
        this();
        this.productId = productId;
        this.taxType = taxType;
        this.taxRate = taxRate;
        this.appliedToSelling = appliedToSelling;
        this.appliedToBuying = appliedToBuying;
        this.includeInPrice = includeInPrice;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getTaxType() { return taxType; }
    public void setTaxType(String taxType) { this.taxType = taxType; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public boolean isAppliedToSelling() { return appliedToSelling; }
    public void setAppliedToSelling(boolean appliedToSelling) { this.appliedToSelling = appliedToSelling; }

    public boolean isAppliedToBuying() { return appliedToBuying; }
    public void setAppliedToBuying(boolean appliedToBuying) { this.appliedToBuying = appliedToBuying; }

    public boolean isIncludeInPrice() { return includeInPrice; }
    public void setIncludeInPrice(boolean includeInPrice) { this.includeInPrice = includeInPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}
