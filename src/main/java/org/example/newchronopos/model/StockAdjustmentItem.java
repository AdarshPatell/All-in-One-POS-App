package org.example.newchronopos.model;

import javafx.beans.property.*;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;

import java.math.BigDecimal;

public class StockAdjustmentItem {
    private final IntegerProperty id;
    private final ObjectProperty<CheckBox> select;
    private final ObjectProperty<ImageView> image;
    private final StringProperty productName;
    private final StringProperty itemId;
    private final StringProperty currentStock;
    private final StringProperty category;
    private final StringProperty reason;
    private final StringProperty location;
    private final StringProperty adjustmentType;
    private final StringProperty quantityAdjustment;
    private final StringProperty newStock;

    public StockAdjustmentItem() {
        this.id = new SimpleIntegerProperty();
        this.select = new SimpleObjectProperty<>(new CheckBox());
        this.image = new SimpleObjectProperty<>();
        this.productName = new SimpleStringProperty();
        this.itemId = new SimpleStringProperty();
        this.currentStock = new SimpleStringProperty();
        this.category = new SimpleStringProperty();
        this.reason = new SimpleStringProperty();
        this.location = new SimpleStringProperty();
        this.adjustmentType = new SimpleStringProperty();
        this.quantityAdjustment = new SimpleStringProperty();
        this.newStock = new SimpleStringProperty();
    }

    public StockAdjustmentItem(int id, String productName, String itemId, String currentStock,
                               String category, String location) {
        this();
        this.id.set(id);
        this.productName.set(productName);
        this.itemId.set(itemId);
        this.currentStock.set(currentStock);
        this.category.set(category);
        this.location.set(location);
    }

    // Property getters
    public IntegerProperty idProperty() { return id; }
    public ObjectProperty<CheckBox> selectProperty() { return select; }
    public ObjectProperty<ImageView> imageProperty() { return image; }
    public StringProperty productNameProperty() { return productName; }
    public StringProperty itemIdProperty() { return itemId; }
    public StringProperty stockProperty() { return currentStock; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty reasonProperty() { return reason; }
    public StringProperty locationProperty() { return location; }
    public StringProperty adjustmentTypeProperty() { return adjustmentType; }
    public StringProperty quantityAdjustmentProperty() { return quantityAdjustment; }
    public StringProperty newStockProperty() { return newStock; }

    // Getters
    public int getId() { return id.get(); }
    public CheckBox getSelect() { return select.get(); }
    public ImageView getImage() { return image.get(); }
    public String getProductName() { return productName.get(); }
    public String getName() { return productName.get(); } // Alias for compatibility
    public String getItemId() { return itemId.get(); }
    public String getStock() { return currentStock.get(); }
    public String getCategory() { return category.get(); }
    public String getReason() { return reason.get(); }
    public String getLocation() { return location.get(); }
    public String getAdjustmentType() { return adjustmentType.get(); }
    public String getQuantityAdjustment() { return quantityAdjustment.get(); }
    public String getNewStock() { return newStock.get(); }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setSelect(CheckBox select) { this.select.set(select); }
    public void setImage(ImageView image) { this.image.set(image); }
    public void setProductName(String productName) { this.productName.set(productName); }
    public void setName(String name) { this.productName.set(name); } // Alias for compatibility
    public void setItemId(String itemId) { this.itemId.set(itemId); }
    public void setStock(String stock) { this.currentStock.set(stock); }
    public void setCategory(String category) { this.category.set(category); }
    public void setReason(String reason) { this.reason.set(reason); }
    public void setLocation(String location) { this.location.set(location); }
    public void setAdjustmentType(String adjustmentType) { this.adjustmentType.set(adjustmentType); }
    public void setQuantityAdjustment(String quantityAdjustment) { this.quantityAdjustment.set(quantityAdjustment); }
    public void setNewStock(String newStock) { this.newStock.set(newStock); }
}
