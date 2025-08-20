package org.example.newchronopos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.newchronopos.model.Product;

public class AdjustProductController {

    @FXML private ImageView productImage;
    @FXML private TextField productNameField;
    @FXML private ComboBox<String> currentStockCombo;
    @FXML private ComboBox<String> adjustmentTypeCombo;
    @FXML private TextField quantityField;
    @FXML private ComboBox<String> reasonCombo;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private Product product;

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupButtons();
        setupProductImage();
    }

    private void setupComboBoxes() {
        // Current stock dropdown
        ObservableList<String> stockOptions = FXCollections.observableArrayList(
            "120 Items", "50 Items", "200 Items", "75 Items"
        );
        currentStockCombo.setItems(stockOptions);

        // Adjustment type dropdown
        ObservableList<String> adjustmentTypes = FXCollections.observableArrayList(
            "Increase", "Decrease"
        );
        adjustmentTypeCombo.setItems(adjustmentTypes);

        // Reason dropdown
        ObservableList<String> reasons = FXCollections.observableArrayList(
            "Customer Demand", "Damaged Goods", "Expired Items", "Theft", "Inventory Correction"
        );
        reasonCombo.setItems(reasons);
        reasonCombo.setValue("Customer Demand");
    }

    private void setupButtons() {
        cancelButton.setOnAction(e -> closeDialog());
        saveButton.setOnAction(e -> saveAdjustment());
    }

    private void setupProductImage() {
        // Set default pizza image or load from product
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/pizza-icon.png"));
            productImage.setImage(defaultImage);
        } catch (Exception e) {
            // Handle image loading error
            System.out.println("Could not load product image");
        }
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            productNameField.setText(product.getName());
            // Set other fields based on product data
        }
    }

    @FXML
    private void saveAdjustment() {
        // Validate input
        if (validateInput()) {
            // Save adjustment to database
            // Update stock levels
            // Close dialog
            closeDialog();
        }
    }

    private boolean validateInput() {
        if (productNameField.getText().isEmpty()) {
            showAlert("Please enter product name");
            return false;
        }
        if (adjustmentTypeCombo.getValue() == null) {
            showAlert("Please select adjustment type");
            return false;
        }
        if (quantityField.getText().isEmpty()) {
            showAlert("Please enter quantity");
            return false;
        }
        try {
            Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid quantity");
            return false;
        }
        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
