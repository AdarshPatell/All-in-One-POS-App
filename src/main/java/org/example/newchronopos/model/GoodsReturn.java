package org.example.newchronopos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GoodsReturn {
    private int returnId;
    private String returnNo;
    private LocalDate returnDate;
    private int customerId;
    private int storeLocationId;
    private String status;
    private String remarks;
    private String reason;
    private double totalAmount;
    private int createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional fields for display
    private String customer;

    // Additional fields needed for compilation
    private int id;
    private int productId;
    private int quantity;
    private double refundAmount;
    private String refundMethod;
    private String notes;

    public GoodsReturn() {}

    public GoodsReturn(String returnNo, String returnDate, String customer, String reason, String totalAmount, String status) {
        this.returnNo = returnNo;
        this.customer = customer;
        this.reason = reason;
        this.totalAmount = Double.parseDouble(totalAmount.replace("$", ""));
        this.status = status;
    }

    // Getters and setters
    public int getReturnId() { return returnId; }
    public void setReturnId(int returnId) { this.returnId = returnId; }

    public String getReturnNo() { return returnNo; }
    public void setReturnNo(String returnNo) { this.returnNo = returnNo; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

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

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getTotalAmountString() { return String.format("%.2f", totalAmount); }

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

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(double refundAmount) { this.refundAmount = refundAmount; }

    public String getRefundMethod() { return refundMethod; }
    public void setRefundMethod(String refundMethod) { this.refundMethod = refundMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
