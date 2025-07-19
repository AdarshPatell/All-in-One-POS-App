package org.example.newchronopos.model;

import javafx.beans.property.*;
import javafx.scene.image.ImageView;

public class ProductView {
    private final ObjectProperty<ImageView> photo;
    private final StringProperty productName;
    private final StringProperty description;
    private final StringProperty itemId;
    private final IntegerProperty stock;
    private final StringProperty category;
    private final DoubleProperty price;
    private final StringProperty availability;

    public ProductView(ImageView photo, String productName, String description, String itemId, int stock, String category, double price, String availability) {
        this.photo = new SimpleObjectProperty<>(photo);
        this.productName = new SimpleStringProperty(productName);
        this.description = new SimpleStringProperty(description);
        this.itemId = new SimpleStringProperty(itemId);
        this.stock = new SimpleIntegerProperty(stock);
        this.category = new SimpleStringProperty(category);
        this.price = new SimpleDoubleProperty(price);
        this.availability = new SimpleStringProperty(availability);
    }

    // --- Getters and Property Getters ---

    public ImageView getPhoto() { return photo.get(); }
    public ObjectProperty<ImageView> photoProperty() { return photo; }

    public String getProductName() { return productName.get(); }
    public StringProperty productNameProperty() { return productName; }

    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }

    public String getItemId() { return itemId.get(); }
    public StringProperty itemIdProperty() { return itemId; }

    public int getStock() { return stock.get(); }
    public IntegerProperty stockProperty() { return stock; }

    public String getCategory() { return category.get(); }
    public StringProperty categoryProperty() { return category; }

    public double getPrice() { return price.get(); }
    public DoubleProperty priceProperty() { return price; }

    public String getAvailability() { return availability.get(); }
    public StringProperty availabilityProperty() { return availability; }
}