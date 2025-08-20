package org.example.newchronopos.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.List;

public class Product {
    // Basic product table fields
    private int id;
    private int deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    private String type;

    // Product info table fields
    private int productInfoId;
    private String productName;
    private String productNameAr;
    private String alternateName;
    private String alternateNameAr;
    private String fullDescription;
    private String fullDescriptionAr;
    private String shortDescription;
    private String shortDescriptionAr;
    private String sku;
    private String modelNumber;
    private boolean createdBarcode;
    private boolean hasStandardBarcode;
    private int categoryId;
    private int subCategoryLvl1Id;
    private int subCategoryLvl2Id;
    private int brandId;
    private String productUnit;
    private double weight;
    private String dimensions;
    private boolean specsFlag;
    private String specs;
    private String color;
    private int reorderLevel;
    private String storeLocation;
    private boolean canReturn;
    private String countryOfOrigin;
    private int supplierId;
    private int shopLocationId;
    private int stockUnitId;
    private int purchaseUnitId;
    private int sellingUnitId;
    private boolean withExpiryDate;
    private int expiryDays;
    private boolean hasWarranty;
    private int warrantyPeriod;
    private int warrantyTypeId;
    private String priceType;

    // JavaFX Properties for TableView binding (needed for stock adjustment and other screens)
    private BooleanProperty select = new SimpleBooleanProperty(false);
    private StringProperty image = new SimpleStringProperty("");
    private StringProperty stock = new SimpleStringProperty("0");
    private StringProperty reason = new SimpleStringProperty("");
    private StringProperty location = new SimpleStringProperty("");
    private StringProperty price = new SimpleStringProperty("0.00");
    private StringProperty availability = new SimpleStringProperty("Available");

    // Related entities
    private Category category;
    private Brand brand;
    private List<ProductBarcode> barcodes;
    private List<ProductAttribute> attributes;
    private List<ProductPrice> prices;
    private List<ProductTax> taxes;
    private List<ProductImage> images;

    // Constructors
    public Product() {
        this.deleted = 0;
        this.status = "Active";
        this.type = "Physical";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.productUnit = "pcs";
        this.createdBarcode = false;
        this.hasStandardBarcode = true;
        this.categoryId = 1;
        this.specsFlag = true;
        this.reorderLevel = 0;
        this.canReturn = false;
        this.priceType = "Fixed";
        this.storeLocation = "";
    }

    // JavaFX Property methods for TableView
    public BooleanProperty selectProperty() { return select; }
    public boolean isSelect() { return select.get(); }
    public void setSelect(boolean select) { this.select.set(select); }

    public StringProperty imageProperty() { return image; }
    public String getImage() { return image.get(); }
    public void setImage(String image) { this.image.set(image); }

    public StringProperty stockProperty() { return stock; }
    public String getStock() { return stock.get(); }
    public void setStock(String stock) { this.stock.set(stock); }

    public StringProperty reasonProperty() { return reason; }
    public String getReason() { return reason.get(); }
    public void setReason(String reason) { this.reason.set(reason); }

    public StringProperty locationProperty() { return location; }
    public String getLocation() { return location.get(); }
    public void setLocation(String location) { this.location.set(location); }

    public StringProperty priceProperty() { return price; }
    public String getPrice() { return price.get(); }
    public void setPrice(String price) { this.price.set(price); }

    public StringProperty availabilityProperty() { return availability; }
    public String getAvailability() { return availability.get(); }
    public void setAvailability(String availability) { this.availability.set(availability); }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDeleted() { return deleted; }
    public void setDeleted(int deleted) { this.deleted = deleted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getProductInfoId() { return productInfoId; }
    public void setProductInfoId(int productInfoId) { this.productInfoId = productInfoId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductNameAr() { return productNameAr; }
    public void setProductNameAr(String productNameAr) { this.productNameAr = productNameAr; }

    public String getAlternateName() { return alternateName; }
    public void setAlternateName(String alternateName) { this.alternateName = alternateName; }

    public String getAlternateNameAr() { return alternateNameAr; }
    public void setAlternateNameAr(String alternateNameAr) { this.alternateNameAr = alternateNameAr; }

    public String getFullDescription() { return fullDescription; }
    public void setFullDescription(String fullDescription) { this.fullDescription = fullDescription; }

    public String getFullDescriptionAr() { return fullDescriptionAr; }
    public void setFullDescriptionAr(String fullDescriptionAr) { this.fullDescriptionAr = fullDescriptionAr; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getShortDescriptionAr() { return shortDescriptionAr; }
    public void setShortDescriptionAr(String shortDescriptionAr) { this.shortDescriptionAr = shortDescriptionAr; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getModelNumber() { return modelNumber; }
    public void setModelNumber(String modelNumber) { this.modelNumber = modelNumber; }

    public boolean isCreatedBarcode() { return createdBarcode; }
    public void setCreatedBarcode(boolean createdBarcode) { this.createdBarcode = createdBarcode; }

    public boolean isHasStandardBarcode() { return hasStandardBarcode; }
    public void setHasStandardBarcode(boolean hasStandardBarcode) { this.hasStandardBarcode = hasStandardBarcode; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getSubCategoryLvl1Id() { return subCategoryLvl1Id; }
    public void setSubCategoryLvl1Id(int subCategoryLvl1Id) { this.subCategoryLvl1Id = subCategoryLvl1Id; }

    public int getSubCategoryLvl2Id() { return subCategoryLvl2Id; }
    public void setSubCategoryLvl2Id(int subCategoryLvl2Id) { this.subCategoryLvl2Id = subCategoryLvl2Id; }

    public int getBrandId() { return brandId; }
    public void setBrandId(int brandId) { this.brandId = brandId; }

    public String getProductUnit() { return productUnit; }
    public void setProductUnit(String productUnit) { this.productUnit = productUnit; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public boolean isSpecsFlag() { return specsFlag; }
    public void setSpecsFlag(boolean specsFlag) { this.specsFlag = specsFlag; }

    public String getSpecs() { return specs; }
    public void setSpecs(String specs) { this.specs = specs; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public String getStoreLocation() { return storeLocation; }
    public void setStoreLocation(String storeLocation) { this.storeLocation = storeLocation; }

    public boolean isCanReturn() { return canReturn; }
    public void setCanReturn(boolean canReturn) { this.canReturn = canReturn; }

    public String getCountryOfOrigin() { return countryOfOrigin; }
    public void setCountryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public int getShopLocationId() { return shopLocationId; }
    public void setShopLocationId(int shopLocationId) { this.shopLocationId = shopLocationId; }

    public int getStockUnitId() { return stockUnitId; }
    public void setStockUnitId(int stockUnitId) { this.stockUnitId = stockUnitId; }

    public int getPurchaseUnitId() { return purchaseUnitId; }
    public void setPurchaseUnitId(int purchaseUnitId) { this.purchaseUnitId = purchaseUnitId; }

    public int getSellingUnitId() { return sellingUnitId; }
    public void setSellingUnitId(int sellingUnitId) { this.sellingUnitId = sellingUnitId; }

    public boolean isWithExpiryDate() { return withExpiryDate; }
    public void setWithExpiryDate(boolean withExpiryDate) { this.withExpiryDate = withExpiryDate; }

    public int getExpiryDays() { return expiryDays; }
    public void setExpiryDays(int expiryDays) { this.expiryDays = expiryDays; }

    public boolean isHasWarranty() { return hasWarranty; }
    public void setHasWarranty(boolean hasWarranty) { this.hasWarranty = hasWarranty; }

    public int getWarrantyPeriod() { return warrantyPeriod; }
    public void setWarrantyPeriod(int warrantyPeriod) { this.warrantyPeriod = warrantyPeriod; }

    public int getWarrantyTypeId() { return warrantyTypeId; }
    public void setWarrantyTypeId(int warrantyTypeId) { this.warrantyTypeId = warrantyTypeId; }

    public String getPriceType() { return priceType; }
    public void setPriceType(String priceType) { this.priceType = priceType; }

    // Missing stock-related methods
    private int stockQuantity = 0;
    private int minimumStock = 0;

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public int getMinimumStock() { return minimumStock; }
    public void setMinimumStock(int minimumStock) { this.minimumStock = minimumStock; }

    // Related entities
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    public List<ProductBarcode> getBarcodes() { return barcodes; }
    public void setBarcodes(List<ProductBarcode> barcodes) { this.barcodes = barcodes; }

    public List<ProductAttribute> getAttributes() { return attributes; }
    public void setAttributes(List<ProductAttribute> attributes) { this.attributes = attributes; }

    public List<ProductPrice> getPrices() { return prices; }
    public void setPrices(List<ProductPrice> prices) { this.prices = prices; }

    public List<ProductTax> getTaxes() { return taxes; }
    public void setTaxes(List<ProductTax> taxes) { this.taxes = taxes; }

    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) { this.images = images; }

    // Convenience methods for backward compatibility
    public String getName() { return productName; }
    public void setName(String name) { this.productName = name; }

    public String getDescription() { return fullDescription; }
    public void setDescription(String description) { this.fullDescription = description; }
}
