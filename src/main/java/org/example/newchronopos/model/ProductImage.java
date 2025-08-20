package org.example.newchronopos.model;

public class ProductImage {
    private int id;
    private int productId;
    private String imageName;
    private String imageUrl;
    private String imageType; // main, gallery, thumbnail
    private boolean isPrimary;
    private int displayOrder;
    private String status;
    private java.time.LocalDateTime createdAt;

    // Constructors
    public ProductImage() {
        this.status = "Active";
        this.createdAt = java.time.LocalDateTime.now();
        this.isPrimary = false;
        this.displayOrder = 0;
    }

    public ProductImage(int productId, String imageName, String imageUrl, String imageType, boolean isPrimary) {
        this();
        this.productId = productId;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.imageType = imageType;
        this.isPrimary = isPrimary;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }

    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean primary) { isPrimary = primary; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}
