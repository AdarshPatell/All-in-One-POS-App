package org.example.newchronopos.model;

import javafx.beans.property.*;
import javafx.scene.image.ImageView;

import javafx.beans.property.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProductView {
    private final IntegerProperty id;
    private final ObjectProperty<ImageView> image;
    private final StringProperty productName;
    private final StringProperty sku;
    private final StringProperty category;
    private final StringProperty brand;
    private final StringProperty price;
    private final StringProperty stock;
    private final StringProperty status;

    public ProductView(int id, ImageView image, String productName, String sku,
                       String category, String brand, String price, String stock, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.image = new SimpleObjectProperty<>(image);
        this.productName = new SimpleStringProperty(productName);
        this.sku = new SimpleStringProperty(sku);
        this.category = new SimpleStringProperty(category);
        this.brand = new SimpleStringProperty(brand);
        this.price = new SimpleStringProperty(price);
        this.stock = new SimpleStringProperty(stock);
        this.status = new SimpleStringProperty(status);
    }

    // Getters
    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public ImageView getImage() { return image.get(); }
    public ObjectProperty<ImageView> imageProperty() { return image; }
    public String getProductName() { return productName.get(); }
    public StringProperty productNameProperty() { return productName; }
    public String getSku() { return sku.get(); }
    public StringProperty skuProperty() { return sku; }
    public String getCategory() { return category.get(); }
    public StringProperty categoryProperty() { return category; }
    public String getBrand() { return brand.get(); }
    public StringProperty brandProperty() { return brand; }
    public String getPrice() { return price.get(); }
    public StringProperty priceProperty() { return price; }
    public String getStock() { return stock.get(); }
    public StringProperty stockProperty() { return stock; }
    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
}