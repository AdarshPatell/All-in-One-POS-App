package org.example.newchronopos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.newchronopos.dao.CategoryDAO;
import org.example.newchronopos.dao.ProductDAO;
import org.example.newchronopos.model.Brand;
import org.example.newchronopos.model.Category;
import org.example.newchronopos.model.Product;
import org.example.newchronopos.model.ProductView;
import org.example.newchronopos.service.BarcodeGeneratorService;

import java.io.File;
import java.util.List;
import java.util.Random;

public class AddProductController {

    // Step Navigation Sidebar
    @FXML private ToggleButton productInformationStep;
    @FXML private ToggleButton taxStep;
    @FXML private ToggleButton productBarcodesStep;
    @FXML private ToggleButton productAttributesStep;
    @FXML private ToggleButton unitPricesStep;
    @FXML private ToggleButton productPicturesStep;

    // Step Content Areas
    @FXML private VBox productInformationContent;
    @FXML private VBox taxContent;
    @FXML private VBox productBarcodesContent;
    @FXML private VBox productAttributesContent;
    @FXML private VBox unitPricesContent;
    @FXML private VBox productPicturesContent;

    // Product Information Step
    @FXML private ImageView productImageView;
    @FXML private TextField productNameField;
    @FXML private TextField skuField;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private TextField brandField;
    @FXML private TextField purchaseUnitField;
    @FXML private TextField sellingUnitField;
    @FXML private ComboBox<String> supplierCombo;
    @FXML private TextField productGroupField;
    @FXML private TextField reorderLevelField;
    @FXML private TextArea descriptionArea;
    @FXML private CheckBox canReturnCheck;
    @FXML private CheckBox inStockCheck;

    // Tax Step
    @FXML private ComboBox<String> taxTypeCombo;
    @FXML private CheckBox appliedToSellingCheck;
    @FXML private CheckBox appliedToBuyingCheck;
    @FXML private CheckBox includeInPriceCheck;
    @FXML private TableView<TaxInfo> taxTable;

    // Product Barcodes Step
    @FXML private TextField barcodeNameField;
    @FXML private TextField barcodeValueField;
    @FXML private CheckBox isStandardCheck;
    @FXML private CheckBox isDefaultCheck;
    @FXML private TableView<BarcodeInfo> barcodeTable;

    // Product Attributes Step
    @FXML private TextField attributeNameField;
    @FXML private TextField attributeValueField;
    @FXML private TextField arabicValueField;
    @FXML private TableView<AttributeInfo> attributeTable;

    // Unit Prices Step
    @FXML private ComboBox<String> priceTypeCombo;
    @FXML private ComboBox<String> unitOptionCombo;
    @FXML private TextField costField;
    @FXML private TextField priceField;
    @FXML private TextField colorField;
    @FXML private TableView<PriceInfo> priceTable;

    // Product Pictures Step
    @FXML private VBox picturesContainer;
    @FXML private Button addMorePicturesButton;

    // Action Buttons
    @FXML private Button discardChangesButton;
    @FXML private Button saveChangesButton;

    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private BarcodeGeneratorService barcodeService = new BarcodeGeneratorService();
    private ProductView editingProduct;
    private int currentStep = 0;

    // Data Collections
    private ObservableList<TaxInfo> taxes = FXCollections.observableArrayList();
    private ObservableList<BarcodeInfo> barcodes = FXCollections.observableArrayList();
    private ObservableList<AttributeInfo> attributes = FXCollections.observableArrayList();
    private ObservableList<PriceInfo> prices = FXCollections.observableArrayList();
    private ObservableList<String> productImages = FXCollections.observableArrayList();

    // Parent controller reference for communication
    private ProductsController parentController;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        setupStepNavigation();
        setupComboBoxes();
        setupTables();
        setupDefaultValues();
        showStep(0); // Show Product Information step by default
    }

    private void setupStepNavigation() {
        ToggleButton[] steps = {
            productInformationStep, taxStep, productBarcodesStep,
            productAttributesStep, unitPricesStep, productPicturesStep
        };

        for (int i = 0; i < steps.length; i++) {
            final int stepIndex = i;
            steps[i].setOnAction(e -> showStep(stepIndex));
        }

        // Set initial active step
        productInformationStep.setSelected(true);
    }

    private void setupComboBoxes() {
        // Load categories
        try {
            List<Category> categories = categoryDAO.getAllCategories();
            categoryCombo.setItems(FXCollections.observableArrayList(categories));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setup other combo boxes
        taxTypeCombo.setItems(FXCollections.observableArrayList("VAT 5%", "VAT 15%", "Exempt"));
        priceTypeCombo.setItems(FXCollections.observableArrayList("Online", "Offline", "Wholesale"));
        unitOptionCombo.setItems(FXCollections.observableArrayList("Per Piece", "Per Kg", "Per Liter"));
        supplierCombo.setItems(FXCollections.observableArrayList("Default Supplier", "Supplier 1", "Supplier 2"));
    }

    private void setupTables() {
        // Tax table setup
        TableColumn<TaxInfo, String> taxTypeCol = new TableColumn<>("Tax Type");
        taxTypeCol.setCellValueFactory(cellData -> cellData.getValue().taxTypeProperty());

        TableColumn<TaxInfo, CheckBox> appliedToSellingCol = new TableColumn<>("Applied to Selling");
        appliedToSellingCol.setCellValueFactory(cellData -> cellData.getValue().appliedToSellingProperty());

        taxTable.getColumns().addAll(taxTypeCol, appliedToSellingCol);
        taxTable.setItems(taxes);

        // Similar setup for other tables...
        setupBarcodeTable();
        setupAttributeTable();
        setupPriceTable();
    }

    private void setupBarcodeTable() {
        TableColumn<BarcodeInfo, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<BarcodeInfo, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(cellData -> cellData.getValue().barcodeProperty());

        TableColumn<BarcodeInfo, CheckBox> isStandardCol = new TableColumn<>("Is Standard");
        isStandardCol.setCellValueFactory(cellData -> cellData.getValue().isStandardProperty());

        TableColumn<BarcodeInfo, CheckBox> isDefaultCol = new TableColumn<>("Is Default");
        isDefaultCol.setCellValueFactory(cellData -> cellData.getValue().isDefaultProperty());

        barcodeTable.getColumns().addAll(nameCol, barcodeCol, isStandardCol, isDefaultCol);
        barcodeTable.setItems(barcodes);
    }

    private void setupAttributeTable() {
        TableColumn<AttributeInfo, String> attributeCol = new TableColumn<>("Attribute");
        attributeCol.setCellValueFactory(cellData -> cellData.getValue().attributeProperty());

        TableColumn<AttributeInfo, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(cellData -> cellData.getValue().valueProperty());

        TableColumn<AttributeInfo, String> arabicValueCol = new TableColumn<>("Arabic Value");
        arabicValueCol.setCellValueFactory(cellData -> cellData.getValue().arabicValueProperty());

        attributeTable.getColumns().addAll(attributeCol, valueCol, arabicValueCol);
        attributeTable.setItems(attributes);
    }

    private void setupPriceTable() {
        TableColumn<PriceInfo, String> priceTypeCol = new TableColumn<>("Select Price Type");
        priceTypeCol.setCellValueFactory(cellData -> cellData.getValue().priceTypeProperty());

        TableColumn<PriceInfo, String> unitOptionCol = new TableColumn<>("Unit Option");
        unitOptionCol.setCellValueFactory(cellData -> cellData.getValue().unitOptionProperty());

        TableColumn<PriceInfo, String> costCol = new TableColumn<>("Cost");
        costCol.setCellValueFactory(cellData -> cellData.getValue().costProperty());

        TableColumn<PriceInfo, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

        TableColumn<PriceInfo, String> colorCol = new TableColumn<>("Color");
        colorCol.setCellValueFactory(cellData -> cellData.getValue().colorProperty());

        priceTable.getColumns().addAll(priceTypeCol, unitOptionCol, costCol, priceCol, colorCol);
        priceTable.setItems(prices);
    }

    private void setupDefaultValues() {
        // Generate automatic barcode
        String autoBarcode = barcodeService.generateBarcode();
        barcodeValueField.setText(autoBarcode);

        // Set default product image
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/pizza-icon.png"));
            productImageView.setImage(defaultImage);
        } catch (Exception e) {
            System.out.println("Could not load default product image");
        }
    }

    private void showStep(int stepIndex) {
        currentStep = stepIndex;

        // Hide all content areas
        VBox[] contentAreas = {
            productInformationContent, taxContent, productBarcodesContent,
            productAttributesContent, unitPricesContent, productPicturesContent
        };

        for (VBox content : contentAreas) {
            content.setVisible(false);
            content.setManaged(false);
        }

        // Show selected content area
        if (stepIndex >= 0 && stepIndex < contentAreas.length) {
            contentAreas[stepIndex].setVisible(true);
            contentAreas[stepIndex].setManaged(true);
        }

        // Update step button styles
        updateStepButtonStyles(stepIndex);
    }

    private void updateStepButtonStyles(int activeStep) {
        ToggleButton[] steps = {
            productInformationStep, taxStep, productBarcodesStep,
            productAttributesStep, unitPricesStep, productPicturesStep
        };

        for (int i = 0; i < steps.length; i++) {
            if (i == activeStep) {
                steps[i].setSelected(true);
                steps[i].setStyle("-fx-background-color: #F4B942; -fx-text-fill: white;");
            } else {
                steps[i].setSelected(false);
                steps[i].setStyle("-fx-background-color: #E9ECEF; -fx-text-fill: #6C757D;");
            }
        }
    }

    @FXML
    private void addTax() {
        if (taxTypeCombo.getValue() != null) {
            TaxInfo tax = new TaxInfo(
                taxTypeCombo.getValue(),
                appliedToSellingCheck.isSelected(),
                appliedToBuyingCheck.isSelected(),
                includeInPriceCheck.isSelected()
            );
            taxes.add(tax);
            clearTaxFields();
        }
    }

    @FXML
    private void addBarcode() {
        if (!barcodeNameField.getText().isEmpty() && !barcodeValueField.getText().isEmpty()) {
            BarcodeInfo barcode = new BarcodeInfo(
                barcodeNameField.getText(),
                barcodeValueField.getText(),
                isStandardCheck.isSelected(),
                isDefaultCheck.isSelected()
            );
            barcodes.add(barcode);
            clearBarcodeFields();

            // Generate new barcode for next entry
            barcodeValueField.setText(barcodeService.generateBarcode());
        }
    }

    @FXML
    private void addAttribute() {
        if (!attributeNameField.getText().isEmpty() && !attributeValueField.getText().isEmpty()) {
            AttributeInfo attribute = new AttributeInfo(
                attributeNameField.getText(),
                attributeValueField.getText(),
                arabicValueField.getText()
            );
            attributes.add(attribute);
            clearAttributeFields();
        }
    }

    @FXML
    private void addPrice() {
        if (priceTypeCombo.getValue() != null && !costField.getText().isEmpty() && !priceField.getText().isEmpty()) {
            PriceInfo price = new PriceInfo(
                priceTypeCombo.getValue(),
                unitOptionCombo.getValue(),
                costField.getText(),
                priceField.getText(),
                colorField.getText()
            );
            prices.add(price);
            clearPriceFields();
        }
    }

    @FXML
    private void addProductPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(addMorePicturesButton.getScene().getWindow());
        if (selectedFile != null) {
            productImages.add(selectedFile.getAbsolutePath());
            updatePicturesDisplay();
        }
    }

    private void updatePicturesDisplay() {
        picturesContainer.getChildren().clear();
        for (String imagePath : productImages) {
            try {
                ImageView imageView = new ImageView(new Image("file:" + imagePath));
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
                picturesContainer.getChildren().add(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void saveProduct() {
        if (validateProduct()) {
            try {
                Product product = createProductFromForm();
                boolean success = productDAO.saveProduct(product);

                if (success) {
                    showSuccessMessage();
                    closeDialog();
                } else {
                    showErrorMessage("Failed to save product");
                }
            } catch (Exception e) {
                showErrorMessage("Error saving product: " + e.getMessage());
            }
        }
    }

    private Product createProductFromForm() {
        Product product = new Product();
        product.setName(productNameField.getText());
        product.setSku(skuField.getText());
        product.setCategory(categoryCombo.getValue());

        // Fix: Create a Brand object from the text field or set brandId directly
        String brandText = brandField.getText();
        if (brandText != null && !brandText.trim().isEmpty()) {
            Brand brand = new Brand();
            brand.setName(brandText.trim());
            product.setBrand(brand);
        }

        product.setDescription(descriptionArea.getText());
        product.setCanReturn(canReturnCheck.isSelected());
        // Set other properties...
        return product;
    }

    private boolean validateProduct() {
        if (productNameField.getText().isEmpty()) {
            showErrorMessage("Product name is required");
            return false;
        }
        if (skuField.getText().isEmpty()) {
            showErrorMessage("SKU is required");
            return false;
        }
        return true;
    }

    public void setProduct(ProductView product) {
        this.editingProduct = product;
        if (product != null) {
            loadProductData(product);
        }
    }

    private void loadProductData(ProductView product) {
        productNameField.setText(product.getName());
        // Load other fields...
    }

    private void clearTaxFields() {
        taxTypeCombo.setValue(null);
        appliedToSellingCheck.setSelected(false);
        appliedToBuyingCheck.setSelected(false);
        includeInPriceCheck.setSelected(false);
    }

    private void clearBarcodeFields() {
        barcodeNameField.clear();
        isStandardCheck.setSelected(false);
        isDefaultCheck.setSelected(false);
    }

    private void clearAttributeFields() {
        attributeNameField.clear();
        attributeValueField.clear();
        arabicValueField.clear();
    }

    private void clearPriceFields() {
        priceTypeCombo.setValue(null);
        unitOptionCombo.setValue(null);
        costField.clear();
        priceField.clear();
        colorField.clear();
    }

    @FXML
    private void discardChanges() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) discardChangesButton.getScene().getWindow();
        stage.close();
    }

    private void showSuccessMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Product saved successfully!");
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner classes for table data
    public static class TaxInfo {
        private final javafx.beans.property.StringProperty taxType;
        private final javafx.beans.property.ObjectProperty<CheckBox> appliedToSelling;

        public TaxInfo(String taxType, boolean appliedToSelling, boolean appliedToBuying, boolean includeInPrice) {
            this.taxType = new javafx.beans.property.SimpleStringProperty(taxType);
            this.appliedToSelling = new javafx.beans.property.SimpleObjectProperty<>(new CheckBox());
            this.appliedToSelling.get().setSelected(appliedToSelling);
        }

        public javafx.beans.property.StringProperty taxTypeProperty() { return taxType; }
        public javafx.beans.property.ObjectProperty<CheckBox> appliedToSellingProperty() { return appliedToSelling; }
    }

    public static class BarcodeInfo {
        private final javafx.beans.property.StringProperty name;
        private final javafx.beans.property.StringProperty barcode;
        private final javafx.beans.property.ObjectProperty<CheckBox> isStandard;
        private final javafx.beans.property.ObjectProperty<CheckBox> isDefault;

        public BarcodeInfo(String name, String barcode, boolean isStandard, boolean isDefault) {
            this.name = new javafx.beans.property.SimpleStringProperty(name);
            this.barcode = new javafx.beans.property.SimpleStringProperty(barcode);
            this.isStandard = new javafx.beans.property.SimpleObjectProperty<>(new CheckBox());
            this.isDefault = new javafx.beans.property.SimpleObjectProperty<>(new CheckBox());
            this.isStandard.get().setSelected(isStandard);
            this.isDefault.get().setSelected(isDefault);
        }

        public javafx.beans.property.StringProperty nameProperty() { return name; }
        public javafx.beans.property.StringProperty barcodeProperty() { return barcode; }
        public javafx.beans.property.ObjectProperty<CheckBox> isStandardProperty() { return isStandard; }
        public javafx.beans.property.ObjectProperty<CheckBox> isDefaultProperty() { return isDefault; }
    }

    public static class AttributeInfo {
        private final javafx.beans.property.StringProperty attribute;
        private final javafx.beans.property.StringProperty value;
        private final javafx.beans.property.StringProperty arabicValue;

        public AttributeInfo(String attribute, String value, String arabicValue) {
            this.attribute = new javafx.beans.property.SimpleStringProperty(attribute);
            this.value = new javafx.beans.property.SimpleStringProperty(value);
            this.arabicValue = new javafx.beans.property.SimpleStringProperty(arabicValue);
        }

        public javafx.beans.property.StringProperty attributeProperty() { return attribute; }
        public javafx.beans.property.StringProperty valueProperty() { return value; }
        public javafx.beans.property.StringProperty arabicValueProperty() { return arabicValue; }
    }

    public static class PriceInfo {
        private final javafx.beans.property.StringProperty priceType;
        private final javafx.beans.property.StringProperty unitOption;
        private final javafx.beans.property.StringProperty cost;
        private final javafx.beans.property.StringProperty price;
        private final javafx.beans.property.StringProperty color;

        public PriceInfo(String priceType, String unitOption, String cost, String price, String color) {
            this.priceType = new javafx.beans.property.SimpleStringProperty(priceType);
            this.unitOption = new javafx.beans.property.SimpleStringProperty(unitOption);
            this.cost = new javafx.beans.property.SimpleStringProperty(cost);
            this.price = new javafx.beans.property.SimpleStringProperty(price);
            this.color = new javafx.beans.property.SimpleStringProperty(color);
        }

        public javafx.beans.property.StringProperty priceTypeProperty() { return priceType; }
        public javafx.beans.property.StringProperty unitOptionProperty() { return unitOption; }
        public javafx.beans.property.StringProperty costProperty() { return cost; }
        public javafx.beans.property.StringProperty priceProperty() { return price; }
        public javafx.beans.property.StringProperty colorProperty() { return color; }
    }

    public void setParentController(ProductsController parentController) {
        this.parentController = parentController;
    }

    public void setEditMode(Product product) {
        // Convert Product to ProductView for compatibility
        if (product != null) {
            this.editingProduct = new ProductView();
            this.editingProduct.setName(product.getProductName());
            // Set other fields as needed
        }
        this.isEditMode = true;
        if (product != null) {
            populateFormWithProduct(product);
        }
    }

    private void populateFormWithProduct(Product product) {
        if (product != null) {
            productNameField.setText(product.getProductName());
            skuField.setText(product.getSku());
            brandField.setText(String.valueOf(product.getBrandId()));
            purchaseUnitField.setText(String.valueOf(product.getPurchaseUnitId()));
            sellingUnitField.setText(String.valueOf(product.getSellingUnitId()));

            // Set category if available
            if (product.getCategoryId() > 0) {
                // Find and set the category in the combo box
                for (Category category : categoryCombo.getItems()) {
                    if (category.getId() == product.getCategoryId()) {
                        categoryCombo.setValue(category);
                        break;
                    }
                }
            }

            // Set other fields as needed
            // You can add more field population here based on your Product model
        }
    }

    @FXML
    private void handleSaveProduct() {
        try {
            Product product = isEditMode ? new Product() : new Product();

            // Populate product with form data
            product.setProductName(productNameField.getText());
            product.setSku(skuField.getText());

            if (categoryCombo.getValue() != null) {
                product.setCategoryId(categoryCombo.getValue().getId());
            }

            // Set other fields from form
            if (!brandField.getText().isEmpty()) {
                product.setBrandId(Integer.parseInt(brandField.getText()));
            }
            if (!purchaseUnitField.getText().isEmpty()) {
                product.setPurchaseUnitId(Integer.parseInt(purchaseUnitField.getText()));
            }
            if (!sellingUnitField.getText().isEmpty()) {
                product.setSellingUnitId(Integer.parseInt(sellingUnitField.getText()));
            }

            // Set timestamps
            if (!isEditMode) {
                product.setCreatedAt(java.time.LocalDateTime.now());
            }
            product.setUpdatedAt(java.time.LocalDateTime.now());
            product.setStatus("Active");
            product.setDeleted(0);
            product.setType("Simple");

            // Save product
            ProductDAO productDAO = new ProductDAO();
            boolean success = productDAO.saveProduct(product);

            if (success) {
                showAlert("Success", "Product saved successfully!", Alert.AlertType.INFORMATION);

                // Refresh parent controller if available
                if (parentController != null) {
                    // parentController.refreshProductList(); // Comment out until method is implemented
                }

                // Close dialog
                closeDialog();
            } else {
                showAlert("Error", "Failed to save product.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while saving the product: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
