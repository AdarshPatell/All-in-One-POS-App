package org.example.newchronopos.model;

import java.time.LocalDateTime;
import java.util.List;

public class Product {
    private int id;
    private String productName;
    private String sku;
    private int categoryId;
    private String brand;
    private String purchaseUnit;
    private String sellingUnit;
    private String supplier;
    private String productGroup;
    private int reorderLevel;
    private String description;
    private List<Barcode> barcodes;
    private List<ProductAttribute> attributes;
    private List<UnitPrice> prices;
    private List<ProductTax> taxes;
    private List<ProductImage> images;
    private LocalDateTime createdAt;

    // Constructors
    public Product() {}

    public Product(int id, String productName, String sku, int categoryId, String brand,
                   String purchaseUnit, String sellingUnit, String supplier,
                   String productGroup, int reorderLevel, String description,
                   LocalDateTime createdAt) {
        this.id = id;
        this.productName = productName;
        this.sku = sku;
        this.categoryId = categoryId;
        this.brand = brand;
        this.purchaseUnit = purchaseUnit;
        this.sellingUnit = sellingUnit;
        this.supplier = supplier;
        this.productGroup = productGroup;
        this.reorderLevel = reorderLevel;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getPurchaseUnit() { return purchaseUnit; }
    public void setPurchaseUnit(String purchaseUnit) { this.purchaseUnit = purchaseUnit; }
    public String getSellingUnit() { return sellingUnit; }
    public void setSellingUnit(String sellingUnit) { this.sellingUnit = sellingUnit; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public String getProductGroup() { return productGroup; }
    public void setProductGroup(String productGroup) { this.productGroup = productGroup; }
    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Barcode> getBarcodes() { return barcodes; }
    public void setBarcodes(List<Barcode> barcodes) { this.barcodes = barcodes; }
    public List<ProductAttribute> getAttributes() { return attributes; }
    public void setAttributes(List<ProductAttribute> attributes) { this.attributes = attributes; }
    public List<UnitPrice> getPrices() { return prices; }
    public void setPrices(List<UnitPrice> prices) { this.prices = prices; }
    public List<ProductTax> getTaxes() { return taxes; }
    public void setTaxes(List<ProductTax> taxes) { this.taxes = taxes; }
    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) { this.images = images; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Helper classes
    public static class Barcode {
        private int id;
        private int productId;
        private String name;
        private String barcode;
        private boolean isDefault;
        private boolean isStandard;
        private LocalDateTime createdAt;

        // Constructors, getters and setters
        public Barcode() {}

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public void setDefault(boolean aDefault) {
            isDefault = aDefault;
        }

        public boolean isStandard() {
            return isStandard;
        }

        public void setStandard(boolean standard) {
            isStandard = standard;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public Barcode(int id, int productId, String name, String barcode,
                       boolean isDefault, boolean isStandard, LocalDateTime createdAt) {
            this.id = id;
            this.productId = productId;
            this.name = name;
            this.barcode = barcode;
            this.isDefault = isDefault;
            this.isStandard = isStandard;
            this.createdAt = createdAt;
        }
    }

    public static class ProductAttribute {
        private int id;
        private int productId;
        private String attributeName;
        private String attributeValue;
        private String arabicValue;
        private LocalDateTime createdAt;

        // Constructors, getters and setters
        public ProductAttribute() {}

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public void setAttributeName(String attributeName) {
            this.attributeName = attributeName;
        }

        public String getValue() {
            return attributeValue;
        }

        public void setValue(String attributeValue) {
            this.attributeValue = attributeValue;
        }

        public String getArabicValue() {
            return arabicValue;
        }

        public void setArabicValue(String arabicValue) {
            this.arabicValue = arabicValue;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public ProductAttribute(int id, int productId, String attributeName,
                                String attributeValue, String arabicValue, LocalDateTime createdAt) {
            this.id = id;
            this.productId = productId;
            this.attributeName = attributeName;
            this.attributeValue = attributeValue;
            this.arabicValue = arabicValue;
            this.createdAt = createdAt;
        }
        // Getters and setters...
    }

    public static class UnitPrice {
        private int id;
        private int productId;
        private String priceType;
        private String unitOption;
        private double cost;
        private double price;
        private String color;
        private LocalDateTime createdAt;

        // Constructors, getters and setters
        public UnitPrice() {}
        public UnitPrice(int id, int productId, String priceType, String unitOption,
                         double cost, double price, String color, LocalDateTime createdAt) {
            this.id = id;
            this.productId = productId;
            this.priceType = priceType;
            this.unitOption = unitOption;
            this.cost = cost;
            this.price = price;
            this.color = color;
            this.createdAt = createdAt;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getPriceType() {
            return priceType;
        }

        public void setPriceType(String priceType) {
            this.priceType = priceType;
        }

        public String getUnitOption() {
            return unitOption;
        }

        public void setUnitOption(String unitOption) {
            this.unitOption = unitOption;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class ProductTax {
        private int id;
        private int productId;
        private String taxType;
        private boolean appliedToSelling;
        private boolean appliedToBuying;
        private boolean includeInPrice;
        private LocalDateTime createdAt;

        // Constructors, getters and setters
        public ProductTax() {}

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getTaxType() {
            return taxType;
        }

        public void setTaxType(String taxType) {
            this.taxType = taxType;
        }

        public boolean isAppliedToSelling() {
            return appliedToSelling;
        }

        public void setAppliedToSelling(boolean appliedToSelling) {
            this.appliedToSelling = appliedToSelling;
        }

        public boolean isAppliedToBuying() {
            return appliedToBuying;
        }

        public void setAppliedToBuying(boolean appliedToBuying) {
            this.appliedToBuying = appliedToBuying;
        }

        public boolean isIncludeInPrice() {
            return includeInPrice;
        }

        public void setIncludeInPrice(boolean includeInPrice) {
            this.includeInPrice = includeInPrice;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public ProductTax(int id, int productId, String taxType,
                          boolean appliedToSelling, boolean appliedToBuying,
                          boolean includeInPrice, LocalDateTime createdAt) {
            this.id = id;
            this.productId = productId;
            this.taxType = taxType;
            this.appliedToSelling = appliedToSelling;
            this.appliedToBuying = appliedToBuying;
            this.includeInPrice = includeInPrice;
            this.createdAt = createdAt;
        }
    }

    public static class ProductImage {
        private int id;
        private int productId;
        private String imageUrl;
        private LocalDateTime createdAt;

        // Constructors, getters and setters
        public ProductImage() {}

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public ProductImage(int id, int productId, String imageUrl, LocalDateTime createdAt) {
            this.id = id;
            this.productId = productId;
            this.imageUrl = imageUrl;
            this.createdAt = createdAt;
        }
    }
}