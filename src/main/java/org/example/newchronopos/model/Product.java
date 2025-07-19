package org.example.newchronopos.model;

public class Product {
    private int id;
    private String name;
    private String description;
    private String itemId;
    private int stock;
    private String category;
    private double price;
    private String photoPath; // Store image path or URL
    private String availability;

    public Product() {}
    public Product(int id, String name, double price) {
        this.id = id; this.name = name; this.price = price;
    }
    public Product(int id, String name, String description, String itemId, int stock, String category, double price, String photoPath, String availability) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.itemId = itemId;
        this.stock = stock;
        this.category = category;
        this.price = price;
        this.photoPath = photoPath;
        this.availability = availability;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
