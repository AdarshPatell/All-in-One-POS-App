package org.example.newchronopos.model;

import javafx.beans.property.*;
import javafx.scene.control.CheckBox;
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
    private final StringProperty description;
    private final StringProperty availability;
    private final ObjectProperty<CheckBox> select;

    // Default constructor
    public ProductView() {
        this.id = new SimpleIntegerProperty();
        this.image = new SimpleObjectProperty<>(createDefaultImage());
        this.productName = new SimpleStringProperty();
        this.sku = new SimpleStringProperty();
        this.category = new SimpleStringProperty();
        this.brand = new SimpleStringProperty();
        this.price = new SimpleStringProperty();
        this.stock = new SimpleStringProperty();
        this.status = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.availability = new SimpleStringProperty();
        this.select = new SimpleObjectProperty<>(new CheckBox());
    }

    // Constructor with parameters
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
        this.description = new SimpleStringProperty();
        this.availability = new SimpleStringProperty();
        this.select = new SimpleObjectProperty<>(new CheckBox());
    }

    private ImageView createDefaultImage() {
        try {
            Image defaultImg = new Image(getClass().getResourceAsStream("/images/product-placeholder.png"));
            ImageView imageView = new ImageView(defaultImg);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (Exception e) {
            // If image not found, create empty ImageView
            ImageView imageView = new ImageView();
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            return imageView;
        }
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

    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }

    public String getAvailability() { return availability.get(); }
    public StringProperty availabilityProperty() { return availability; }

    public CheckBox getSelect() { return select.get(); }
    public ObjectProperty<CheckBox> selectProperty() { return select; }

    // Setters
    public void setId(String id) {
        try {
            this.id.set(Integer.parseInt(id.replace("#", "")));
        } catch (NumberFormatException e) {
            this.id.set(0);
        }
    }

    public void setImage(ImageView image) { this.image.set(image); }

    public void setName(String name) { this.productName.set(name); }
    public void setProductName(String productName) { this.productName.set(productName); }

    public void setSku(String sku) { this.sku.set(sku); }

    public void setCategory(String category) { this.category.set(category); }

    public void setBrand(String brand) { this.brand.set(brand); }

    public void setPrice(String price) { this.price.set(price); }

    public void setStock(String stock) { this.stock.set(stock); }

    public void setStatus(String status) { this.status.set(status); }

    public void setDescription(String description) { this.description.set(description); }

    public void setAvailability(String availability) { this.availability.set(availability); }

    // Convenience getters for backward compatibility
    public String getName() { return getProductName(); }
}