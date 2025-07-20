package org.example.newchronopos.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.example.newchronopos.dao.ProductDAO;
import org.example.newchronopos.model.*;
import org.example.newchronopos.utils.AppColors;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ProductsController implements Initializable {

    // Product listing view components
    @FXML private TableView<ProductView> productTable;
    @FXML private TableColumn<ProductView, ImageView> colImage;
    @FXML private TableColumn<ProductView, String> colName;
    @FXML private TableColumn<ProductView, String> colSku;
    @FXML private TableColumn<ProductView, String> colCategory;
    @FXML private TableColumn<ProductView, String> colBrand;
    @FXML private TableColumn<ProductView, String> colPrice;
    @FXML private TableColumn<ProductView, String> colStock;
    @FXML private TableColumn<ProductView, String> colStatus;
    @FXML private TableColumn<ProductView, Void> colAction;

    @FXML private Button btnAddProduct;
    @FXML private Button btnAddCategory;
    @FXML private HBox categoryButtonsContainer;
    @FXML private HBox productTabsContainer;

    // Add product view components
    @FXML private StackPane mainContentPane;
    @FXML private VBox productListingView;
    @FXML private HBox addProductView;

    // Step sidebar
    @FXML private VBox stepSidebar;
    @FXML private ToggleGroup stepGroup;
    @FXML private ToggleButton stepInfoBtn;
    @FXML private ToggleButton stepImagesBtn;
    @FXML private ToggleButton stepPricesBtn;
    @FXML private ToggleButton stepTaxesBtn;
    @FXML private ToggleButton stepBarcodesBtn;
    @FXML private ToggleButton stepAttributesBtn;
    @FXML private Button btnCancelAddProduct;

    // Step content panes
    @FXML private StackPane stepContentPane;
    @FXML private VBox infoStep;
    @FXML private VBox imagesStep;
    @FXML private VBox pricesStep;
    @FXML private VBox taxesStep;
    @FXML private VBox barcodesStep;
    @FXML private VBox attributesStep;

    // Info step fields
    @FXML private TextField productNameField;
    @FXML private TextField skuField;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private TextField brandField;
    @FXML private TextField purchaseUnitField;
    @FXML private TextField sellingUnitField;
    @FXML private TextField supplierField;
    @FXML private TextField productGroupField;
    @FXML private Spinner<Integer> reorderLevelSpinner;
    @FXML private TextArea descriptionArea;
    @FXML private Button btnNextFromInfo;

    // Images step fields
    @FXML private TextField imageUrlField;
    @FXML private Button btnAddImage;
    @FXML private Button btnChooseImage;
    @FXML private ListView<String> imagesListView;
    @FXML private Button btnSkipImages;
    @FXML private Button btnNextFromImages;

    // Prices step fields
    @FXML private ComboBox<String> priceTypeCombo;
    @FXML private TextField unitOptionField;
    @FXML private TextField costField;
    @FXML private TextField priceField;
    @FXML private TextField colorField;
    @FXML private Button btnAddPrice;
    @FXML private TableView<Product.UnitPrice> pricesTable;
    @FXML private Button btnSkipPrices;
    @FXML private Button btnNextFromPrices;

    // Taxes step fields
    @FXML private ComboBox<String> taxTypeCombo;
    @FXML private CheckBox appliedToSellingCheck;
    @FXML private CheckBox appliedToBuyingCheck;
    @FXML private CheckBox includeInPriceCheck;
    @FXML private Button btnAddTax;
    @FXML private TableView<Product.ProductTax> taxesTable;
    @FXML private Button btnSkipTaxes;
    @FXML private Button btnNextFromTaxes;

    // Barcodes step fields
    @FXML private TextField barcodeNameField;
    @FXML private TextField barcodeField;
    @FXML private CheckBox isDefaultBarcodeCheck;
    @FXML private CheckBox isStandardBarcodeCheck;
    @FXML private Button btnAddBarcode;
    @FXML private TableView<Product.Barcode> barcodesTable;
    @FXML private Button btnSkipBarcodes;
    @FXML private Button btnNextFromBarcodes;

    // Attributes step fields
    @FXML private TextField attributeNameField;
    @FXML private TextField attributeValueField;
    @FXML private TextField arabicValueField;
    @FXML private Button btnAddAttribute;
    @FXML private TableView<Product.ProductAttribute> attributesTable;
    @FXML private Button btnSkipAttributes;
    @FXML private Button btnSubmitProduct;

    private final ObservableList<ProductView> productList = FXCollections.observableArrayList();
    private final ProductDAO productDAO = new ProductDAO();
    private int currentCategoryFilter = 0; // 0 means all categories

    // Data for new product
    private final ObservableList<String> productImages = FXCollections.observableArrayList();
    private final ObservableList<Product.UnitPrice> productPrices = FXCollections.observableArrayList();
    private final ObservableList<Product.ProductTax> productTaxes = FXCollections.observableArrayList();
    private final ObservableList<Product.Barcode> productBarcodes = FXCollections.observableArrayList();
    private final ObservableList<Product.ProductAttribute> productAttributes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadProductData();
        setupCategoryButtons();
        setupProductTabs();
        setupButtonActions();

        // Setup step buttons first to ensure proper toggle group configuration
        setupStepButtons();
        setupAddProductForm();
        setupImageListCell();
    }

    private void setupStepButtons() {
        // Ensure toggle group is properly initialized
        if (stepGroup == null) {
            stepGroup = new ToggleGroup();
        }

        // Configure toggle buttons
        stepInfoBtn.setToggleGroup(stepGroup);
        stepImagesBtn.setToggleGroup(stepGroup);
        stepPricesBtn.setToggleGroup(stepGroup);
        stepTaxesBtn.setToggleGroup(stepGroup);
        stepBarcodesBtn.setToggleGroup(stepGroup);
        stepAttributesBtn.setToggleGroup(stepGroup);

        // Set userData if not already set in FXML
        if (stepInfoBtn.getUserData() == null) stepInfoBtn.setUserData("info");
        if (stepImagesBtn.getUserData() == null) stepImagesBtn.setUserData("images");
        if (stepPricesBtn.getUserData() == null) stepPricesBtn.setUserData("prices");
        if (stepTaxesBtn.getUserData() == null) stepTaxesBtn.setUserData("taxes");
        if (stepBarcodesBtn.getUserData() == null) stepBarcodesBtn.setUserData("barcodes");
        if (stepAttributesBtn.getUserData() == null) stepAttributesBtn.setUserData("attributes");

        // Add listener for toggle group selection changes
        stepGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                // If no toggle is selected, reselect the old one
                if (oldVal != null) {
                    oldVal.setSelected(true);
                    return;
                }
                // Default to info step if nothing was selected before
                stepInfoBtn.setSelected(true);
                return;
            }

            // Get the userData string to identify which step to show
            String step = (String) newVal.getUserData();
            System.out.println("Selected step: " + step);

            // Show the selected step
            showStep(step);
        });
    }

    // Add this method to set up the image list cell factory
    private void setupImageListCell() {
        imagesListView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String imageUrl, boolean empty) {
                super.updateItem(imageUrl, empty);

                if (empty || imageUrl == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox cellContent = new HBox(10);
                    cellContent.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    ImageView imageView = new ImageView();
                    try {
                        imageView.setImage(new Image(imageUrl, 50, 50, true, true));
                    } catch (Exception e) {
                        // If image can't be loaded, use placeholder or just show URL
                    }
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    imageView.setPreserveRatio(true);

                    Label urlLabel = new Label(imageUrl);
                    urlLabel.setTextFill(javafx.scene.paint.Color.WHITE);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button removeBtn = new Button("Remove");
                    removeBtn.getStyleClass().add("delete-btn");
                    removeBtn.setOnAction(event -> productImages.remove(imageUrl));

                    cellContent.getChildren().addAll(imageView, urlLabel, spacer, removeBtn);
                    setGraphic(cellContent);
                }
            }
        });
    }

    private void setupTableColumns() {
        // Configure columns
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colSku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Custom cell factories for styling
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("In Stock".equals(status)) {
                        setTextFill(AppColors.LIGHT_GREEN);
                    } else if ("Low Stock".equals(status)) {
                        setTextFill(AppColors.LIGHT_YELLOW);
                    } else {
                        setTextFill(AppColors.MUTED_RED);
                    }
                }
            }
        });

        colPrice.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText("$" + price);
                    setTextFill(AppColors.WHITE);
                }
            }
        });

        // Action column with edit/delete buttons
        colAction.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ProductView, Void> call(final TableColumn<ProductView, Void> param) {
                return new TableCell<>() {
                    private final Button btnEdit = new Button("Edit");
                    private final Button btnDelete = new Button("Delete");
                    private final HBox pane = new HBox(5, btnEdit, btnDelete);

                    {
                        btnEdit.getStyleClass().add("edit-btn");
                        btnDelete.getStyleClass().add("delete-btn");

                        btnEdit.setOnAction(event -> {
                            ProductView product = getTableView().getItems().get(getIndex());
                            showEditProductDialog(product);
                        });

                        btnDelete.setOnAction(event -> {
                            ProductView product = getTableView().getItems().get(getIndex());
                            deleteProduct(product);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        });
    }

    private void loadProductData() {
        productList.clear();
        List<Product> products;

        if (currentCategoryFilter == 0) {
            products = productDAO.getAllProducts();
        } else {
            products = productDAO.getAllProducts().stream()
                    .filter(p -> p.getCategoryId() == currentCategoryFilter)
                    .toList();
        }

        for (Product product : products) {
            ImageView imageView = new ImageView();
            try {
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    imageView = new ImageView(new Image(
                            product.getImages().get(0).getImageUrl(),
                            40, 40, true, true));
                } else {
                    // Default product image
                    imageView = new ImageView(new Image(
                            getClass().getResourceAsStream("/images/default-product.png"),
                            40, 40, true, true));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Determine status based on stock
            String status;
            if (product.getReorderLevel() <= 0) {
                status = "In Stock";
            } else if (product.getReorderLevel() > 0 && product.getReorderLevel() <= 10) {
                status = "Low Stock";
            } else {
                status = "Out of Stock";
            }

            // Get category name
            String categoryName = productDAO.getCategoryById(product.getCategoryId())
                    .map(Category::getName)
                    .orElse("Uncategorized");

            // Get default price
            String price = "0.00";
            if (product.getPrices() != null && !product.getPrices().isEmpty()) {
                price = String.format("%.2f", product.getPrices().get(0).getPrice());
            }

            productList.add(new ProductView(
                    product.getId(),
                    imageView,
                    product.getProductName(),
                    product.getSku(),
                    categoryName,
                    product.getBrand(),
                    price,
                    String.valueOf(product.getReorderLevel()),
                    status
            ));
        }
        productTable.setItems(productList);
    }

    private void setupCategoryButtons() {
        // Clear existing buttons
        categoryButtonsContainer.getChildren().clear();

        // Add "All" button
        Button allButton = createCategoryButton("All", 0);
        categoryButtonsContainer.getChildren().add(allButton);

        // Add buttons for each category
        List<Category> categories = productDAO.getAllCategories();
        for (Category category : categories) {
            Button categoryButton = createCategoryButton(category.getName(), category.getId());
            categoryButtonsContainer.getChildren().add(categoryButton);
        }
    }

    private Button createCategoryButton(String text, int categoryId) {
        Button button = new Button(text);
        button.getStyleClass().add("category-btn");

        if (categoryId == currentCategoryFilter) {
            button.getStyleClass().add("selected");
        }

        button.setOnAction(e -> {
            currentCategoryFilter = categoryId;
            loadProductData();
            // Update button styles
            for (var child : categoryButtonsContainer.getChildren()) {
                if (child instanceof Button btn) {
                    btn.getStyleClass().remove("selected");
                }
            }
            button.getStyleClass().add("selected");
        });

        return button;
    }

    private void setupProductTabs() {
        // Clear existing buttons
        productTabsContainer.getChildren().clear();

        String[] tabs = {"All Products", "Special Deals", "New Arrivals", "Best Sellers"};

        for (String tab : tabs) {
            Button tabButton = new Button(tab);
            tabButton.getStyleClass().add("tab-btn");

            if ("All Products".equals(tab)) {
                tabButton.getStyleClass().add("selected");
            }

            tabButton.setOnAction(e -> {
                // Update tab styles
                for (var child : productTabsContainer.getChildren()) {
                    if (child instanceof Button btn) {
                        btn.getStyleClass().remove("selected");
                    }
                }
                tabButton.getStyleClass().add("selected");
                // TODO: Filter products based on tab
            });

            productTabsContainer.getChildren().add(tabButton);
        }
    }

    private void setupButtonActions() {
        btnAddProduct.setOnAction(e -> showAddProductForm());
        btnAddCategory.setOnAction(e -> showAddCategoryDialog());
        btnCancelAddProduct.setOnAction(e -> cancelAddProduct());
    }

    private void setupAddProductForm() {
        // Initialize category combo
        categoryCombo.getItems().addAll(productDAO.getAllCategories());
        categoryCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category category) {
                return category == null ? "" : category.getName();
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });

        // Initialize price type combo
        priceTypeCombo.getItems().addAll("Regular", "Sale", "Wholesale", "VIP");

        // Initialize tax type combo
        taxTypeCombo.getItems().addAll("VAT", "Service Charge", "Municipal Tax");

        // Initialize reorder level spinner
        reorderLevelSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0));

        // Initialize images list
        imagesListView.setItems(productImages);

        // Initialize prices table
        setupPricesTable();

        // Initialize taxes table
        setupTaxesTable();

        // Initialize barcodes table
        setupBarcodesTable();

        // Initialize attributes table
        setupAttributesTable();

        // Set up button actions for steps
        btnNextFromInfo.setOnAction(e -> stepImagesBtn.setSelected(true));
        btnNextFromImages.setOnAction(e -> stepPricesBtn.setSelected(true));
        btnNextFromPrices.setOnAction(e -> stepTaxesBtn.setSelected(true));
        btnNextFromTaxes.setOnAction(e -> stepBarcodesBtn.setSelected(true));
        btnNextFromBarcodes.setOnAction(e -> stepAttributesBtn.setSelected(true));

        btnSkipImages.setOnAction(e -> stepPricesBtn.setSelected(true));
        btnSkipPrices.setOnAction(e -> stepTaxesBtn.setSelected(true));
        btnSkipTaxes.setOnAction(e -> stepBarcodesBtn.setSelected(true));
        btnSkipBarcodes.setOnAction(e -> stepAttributesBtn.setSelected(true));
        btnSkipAttributes.setOnAction(e -> submitProduct());

        // Set up add buttons
        btnAddImage.setOnAction(e -> addImage());
        btnChooseImage.setOnAction(e -> chooseImageFromFile());
        btnAddPrice.setOnAction(e -> addPrice());
        btnAddTax.setOnAction(e -> addTax());
        btnAddBarcode.setOnAction(e -> addBarcode());
        btnAddAttribute.setOnAction(e -> addAttribute());

        // Submit button
        btnSubmitProduct.setOnAction(e -> submitProduct());
    }

    private void setupPricesTable() {
        pricesTable.setItems(productPrices);

        TableColumn<Product.UnitPrice, String> typeCol = new TableColumn<>("Price Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("priceType"));

        TableColumn<Product.UnitPrice, String> unitCol = new TableColumn<>("Unit Option");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unitOption"));

        TableColumn<Product.UnitPrice, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f", cell.getValue().getPrice())));

        TableColumn<Product.UnitPrice, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product.UnitPrice, Void> call(TableColumn<Product.UnitPrice, Void> param) {
                return new TableCell<>() {
                    private final Button deleteBtn = new Button("Delete");

                    {
                        deleteBtn.getStyleClass().add("delete-btn");
                        deleteBtn.setOnAction(e -> {
                            Product.UnitPrice price = getTableView().getItems().get(getIndex());
                            productPrices.remove(price);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteBtn);
                        }
                    }
                };
            }
        });

        pricesTable.getColumns().setAll(typeCol, unitCol, priceCol, actionCol);
    }

    private void setupTaxesTable() {
        taxesTable.setItems(productTaxes);

        TableColumn<Product.ProductTax, String> typeCol = new TableColumn<>("Tax Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("taxType"));

        TableColumn<Product.ProductTax, String> appliedCol = new TableColumn<>("Applied To");
        appliedCol.setCellValueFactory(cell -> {
            Product.ProductTax tax = cell.getValue();
            String appliedTo = "";
            if (tax.isAppliedToSelling()) appliedTo += "Selling ";
            if (tax.isAppliedToBuying()) appliedTo += "Buying";
            return new SimpleStringProperty(appliedTo.trim());
        });

        TableColumn<Product.ProductTax, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product.ProductTax, Void> call(TableColumn<Product.ProductTax, Void> param) {
                return new TableCell<>() {
                    private final Button deleteBtn = new Button("Delete");

                    {
                        deleteBtn.getStyleClass().add("delete-btn");
                        deleteBtn.setOnAction(e -> {
                            Product.ProductTax tax = getTableView().getItems().get(getIndex());
                            productTaxes.remove(tax);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteBtn);
                        }
                    }
                };
            }
        });

        taxesTable.getColumns().setAll(typeCol, appliedCol, actionCol);
    }

    private void setupBarcodesTable() {
        barcodesTable.setItems(productBarcodes);

        TableColumn<Product.Barcode, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product.Barcode, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(new PropertyValueFactory<>("barcode"));

        TableColumn<Product.Barcode, String> defaultCol = new TableColumn<>("Default");
        defaultCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isDefault() ? "Yes" : "No"));

        TableColumn<Product.Barcode, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product.Barcode, Void> call(TableColumn<Product.Barcode, Void> param) {
                return new TableCell<>() {
                    private final Button deleteBtn = new Button("Delete");

                    {
                        deleteBtn.getStyleClass().add("delete-btn");
                        deleteBtn.setOnAction(e -> {
                            Product.Barcode barcode = getTableView().getItems().get(getIndex());
                            productBarcodes.remove(barcode);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteBtn);
                        }
                    }
                };
            }
        });

        barcodesTable.getColumns().setAll(nameCol, barcodeCol, defaultCol, actionCol);
    }

    private void setupAttributesTable() {
        attributesTable.setItems(productAttributes);

        TableColumn<Product.ProductAttribute, String> nameCol = new TableColumn<>("Attribute");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("attributeName"));

        TableColumn<Product.ProductAttribute, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

        TableColumn<Product.ProductAttribute, String> arabicCol = new TableColumn<>("Arabic Value");
        arabicCol.setCellValueFactory(new PropertyValueFactory<>("arabicValue"));

        TableColumn<Product.ProductAttribute, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product.ProductAttribute, Void> call(TableColumn<Product.ProductAttribute, Void> param) {
                return new TableCell<>() {
                    private final Button deleteBtn = new Button("Delete");

                    {
                        deleteBtn.getStyleClass().add("delete-btn");
                        deleteBtn.setOnAction(e -> {
                            Product.ProductAttribute attr = getTableView().getItems().get(getIndex());
                            productAttributes.remove(attr);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteBtn);
                        }
                    }
                };
            }
        });

        attributesTable.getColumns().setAll(nameCol, valueCol, arabicCol, actionCol);
    }

    private void showAddProductForm() {
        // Clear previous data
        clearAddProductForm();

        // Hide all steps first
        hideAllSteps();

        // Show the add product view
        productListingView.setVisible(false);
        addProductView.setVisible(true);

        // Select the first step
        stepInfoBtn.setSelected(true);

        // The toggle listener will call showStep("info")
    }

    private void cancelAddProduct() {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Add Product");
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.setContentText("All unsaved changes will be lost.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Return to product listing
            productListingView.setVisible(true);
            addProductView.setVisible(false);
        }
    }

    private void clearAddProductForm() {
        // Clear all fields
        productNameField.clear();
        skuField.clear();
        categoryCombo.getSelectionModel().clearSelection();
        brandField.clear();
        purchaseUnitField.clear();
        sellingUnitField.clear();
        supplierField.clear();
        productGroupField.clear();
        reorderLevelSpinner.getValueFactory().setValue(0);
        descriptionArea.clear();

        // Clear lists
        productImages.clear();
        productPrices.clear();
        productTaxes.clear();
        productBarcodes.clear();
        productAttributes.clear();
    }

    private void hideAllSteps() {
        // Hide all steps and set managed to false
        infoStep.setVisible(false);
        infoStep.setManaged(false);

        imagesStep.setVisible(false);
        imagesStep.setManaged(false);

        pricesStep.setVisible(false);
        pricesStep.setManaged(false);

        taxesStep.setVisible(false);
        taxesStep.setManaged(false);

        barcodesStep.setVisible(false);
        barcodesStep.setManaged(false);

        attributesStep.setVisible(false);
        attributesStep.setManaged(false);
    }

    private void showStep(String step) {
        // Hide all steps first to ensure only one is visible
        hideAllSteps();

        System.out.println("Showing step: " + step);

        // Show the selected step
        switch (step) {
            case "info":
                infoStep.setVisible(true);
                infoStep.setManaged(true);
                break;
            case "images":
                imagesStep.setVisible(true);
                imagesStep.setManaged(true);
                break;
            case "prices":
                pricesStep.setVisible(true);
                pricesStep.setManaged(true);
                break;
            case "taxes":
                taxesStep.setVisible(true);
                taxesStep.setManaged(true);
                break;
            case "barcodes":
                barcodesStep.setVisible(true);
                barcodesStep.setManaged(true);
                break;
            case "attributes":
                attributesStep.setVisible(true);
                attributesStep.setManaged(true);
                break;
            default:
                System.out.println("Unknown step: " + step);
                infoStep.setVisible(true);
                infoStep.setManaged(true);
                break;
        }
    }

    private void addImage() {
        String imageUrl = imageUrlField.getText().trim();
        if (!imageUrl.isEmpty()) {
            productImages.add(imageUrl);
            imageUrlField.clear();
        }
    }

    /**
     * Opens a file chooser dialog to select an image file from the local filesystem
     */
    private void chooseImageFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");

        // Set extension filters
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
            "Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);

        // Show open file dialog
        Stage stage = (Stage) btnChooseImage.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Convert file path to URL format
                String fileUrl = selectedFile.toURI().toString();

                // Add the file URL to the product images list
                productImages.add(fileUrl);

                // Optionally preview the image or set the URL in the text field
                imageUrlField.setText(fileUrl);
            } catch (Exception e) {
                showAlert("Error", "Failed to load the selected image: " + e.getMessage());
            }
        }
    }

    private void submitProduct() {
        // Validate required fields
        if (productNameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Product name is required.");
            stepInfoBtn.setSelected(true);
            return;
        }

        if (skuField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "SKU is required.");
            stepInfoBtn.setSelected(true);
            return;
        }

        if (categoryCombo.getValue() == null) {
            showAlert("Validation Error", "Category is required.");
            stepInfoBtn.setSelected(true);
            return;
        }

        // Create product object
        Product product = new Product();
        product.setProductName(productNameField.getText().trim());
        product.setSku(skuField.getText().trim());
        product.setCategoryId(categoryCombo.getValue().getId());
        product.setBrand(brandField.getText().trim());
        product.setPurchaseUnit(purchaseUnitField.getText().trim());
        product.setSellingUnit(sellingUnitField.getText().trim());
        product.setSupplier(supplierField.getText().trim());
        product.setProductGroup(productGroupField.getText().trim());
        product.setReorderLevel(reorderLevelSpinner.getValue());
        product.setDescription(descriptionArea.getText().trim());

        // Set related data
        if (!productImages.isEmpty()) {
            List<Product.ProductImage> images = productImages.stream()
                    .map(url -> {
                        Product.ProductImage image = new Product.ProductImage();
                        image.setImageUrl(url);
                        return image;
                    })
                    .collect(Collectors.toList());
            product.setImages(images);
        }

        if (!productPrices.isEmpty()) {
            product.setPrices(new ArrayList<>(productPrices));
        }

        if (!productTaxes.isEmpty()) {
            product.setTaxes(new ArrayList<>(productTaxes));
        }

        if (!productBarcodes.isEmpty()) {
            product.setBarcodes(new ArrayList<>(productBarcodes));
        }

        if (!productAttributes.isEmpty()) {
            product.setAttributes(new ArrayList<>(productAttributes));
        }

        // Save product
        productDAO.createProduct(product);

        // Show success message
        showAlert("Success", "Product created successfully!");

        // Return to product listing
        productListingView.setVisible(true);
        addProductView.setVisible(false);

        // Refresh product list
        loadProductData();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showEditProductDialog(ProductView productView) {
        Optional<Product> productOpt = productDAO.getProductById(productView.getId());
        if (productOpt.isPresent()) {
            Product product = showProductDialog(productOpt.get());
            if (product != null) {
                productDAO.updateProduct(product);
                loadProductData();
            }
        }
    }

    private void deleteProduct(ProductView productView) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Product");
        confirm.setHeaderText("Confirm Deletion");
        confirm.setContentText("Are you sure you want to delete '" + productView.getProductName() + "'?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            productDAO.deleteProduct(productView.getId());
            loadProductData();
        }
    }

    private Product showProductDialog(Product product) {
        // This method is kept for backward compatibility but should be deprecated
        // in favor of the new step-by-step form
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle(product == null ? "Add Product" : "Edit Product");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField nameField = new TextField();
        TextField skuField = new TextField();
        ComboBox<Category> categoryCombo = new ComboBox<>();
        TextField brandField = new TextField();
        TextField purchaseUnitField = new TextField();
        TextField sellingUnitField = new TextField();
        TextField supplierField = new TextField();
        TextField productGroupField = new TextField();
        Spinner<Integer> reorderLevelSpinner = new Spinner<>(0, 1000, 0);
        TextArea descriptionArea = new TextArea();
        TextField imageUrlField = new TextField();

        categoryCombo.getItems().addAll(productDAO.getAllCategories());
        categoryCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category category) {
                return category == null ? "" : category.getName();
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });

        if (product != null) {
            nameField.setText(product.getProductName());
            skuField.setText(product.getSku());
            productDAO.getCategoryById(product.getCategoryId()).ifPresent(categoryCombo::setValue);
            brandField.setText(product.getBrand());
            purchaseUnitField.setText(product.getPurchaseUnit());
            sellingUnitField.setText(product.getSellingUnit());
            supplierField.setText(product.getSupplier());
            productGroupField.setText(product.getProductGroup());
            reorderLevelSpinner.getValueFactory().setValue(product.getReorderLevel());
            descriptionArea.setText(product.getDescription());

            if (product.getImages() != null && !product.getImages().isEmpty()) {
                imageUrlField.setText(product.getImages().get(0).getImageUrl());
            }
        }

        grid.add(new Label("Product Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("SKU:"), 0, 1);
        grid.add(skuField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryCombo, 1, 2);
        grid.add(new Label("Brand:"), 0, 3);
        grid.add(brandField, 1, 3);
        grid.add(new Label("Purchase Unit:"), 0, 4);
        grid.add(purchaseUnitField, 1, 4);
        grid.add(new Label("Selling Unit:"), 0, 5);
        grid.add(sellingUnitField, 1, 5);
        grid.add(new Label("Supplier:"), 0, 6);
        grid.add(supplierField, 1, 6);
        grid.add(new Label("Product Group:"), 0, 7);
        grid.add(productGroupField, 1, 7);
        grid.add(new Label("Reorder Level:"), 0, 8);
        grid.add(reorderLevelSpinner, 1, 8);
        grid.add(new Label("Description:"), 0, 9);
        grid.add(descriptionArea, 1, 9);
        grid.add(new Label("Image URL:"), 0, 10);
        grid.add(imageUrlField, 1, 10);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Product newProduct = new Product();
                if (product != null) {
                    newProduct.setId(product.getId());
                }
                newProduct.setProductName(nameField.getText());
                newProduct.setSku(skuField.getText());
                newProduct.setCategoryId(categoryCombo.getValue() != null ? categoryCombo.getValue().getId() : 0);
                newProduct.setBrand(brandField.getText());
                newProduct.setPurchaseUnit(purchaseUnitField.getText());
                newProduct.setSellingUnit(sellingUnitField.getText());
                newProduct.setSupplier(supplierField.getText());
                newProduct.setProductGroup(productGroupField.getText());
                newProduct.setReorderLevel(reorderLevelSpinner.getValue());
                newProduct.setDescription(descriptionArea.getText());

                if (!imageUrlField.getText().isEmpty()) {
                    Product.ProductImage image = new Product.ProductImage();
                    image.setImageUrl(imageUrlField.getText());
                    newProduct.setImages(List.of(image));
                }

                return newProduct;
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void showAddCategoryDialog() {
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle("Add New Category");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField nameField = new TextField();
        TextArea descriptionArea = new TextArea();
        ComboBox<Category> parentCategoryCombo = new ComboBox<>();
        TextField imageUrlField = new TextField();

        parentCategoryCombo.getItems().addAll(productDAO.getAllCategories());
        parentCategoryCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category category) {
                return category == null ? "None" : category.getName();
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });

        grid.add(new Label("Category Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Parent Category:"), 0, 2);
        grid.add(parentCategoryCombo, 1, 2);
        grid.add(new Label("Image URL:"), 0, 3);
        grid.add(imageUrlField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Category category = new Category();
                category.setName(nameField.getText());
                category.setDescription(descriptionArea.getText());
                category.setParentId(parentCategoryCombo.getValue() != null ? parentCategoryCombo.getValue().getId() : null);
                category.setImageUrl(imageUrlField.getText());
                category.setActive(true);
                return category;
            }
            return null;
        });

        Optional<Category> result = dialog.showAndWait();
        result.ifPresent(category -> {
            productDAO.createCategory(category);
            setupCategoryButtons();
        });
    }

    private void addPrice() {
        // Validate required fields
        if (priceTypeCombo.getValue() == null) {
            showAlert("Validation Error", "Price type is required.");
            return;
        }

        if (priceField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Price is required.");
            return;
        }

        double price;
        double cost = 0.0;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            if (!costField.getText().trim().isEmpty()) {
                cost = Double.parseDouble(costField.getText().trim());
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Price and Cost must be valid numbers.");
            return;
        }

        // Create UnitPrice object
        Product.UnitPrice unitPrice = new Product.UnitPrice();
        unitPrice.setPriceType(priceTypeCombo.getValue());
        unitPrice.setUnitOption(unitOptionField.getText().trim());
        unitPrice.setCost(cost);
        unitPrice.setPrice(price);
        unitPrice.setColor(colorField.getText().trim());

        // Add to list
        productPrices.add(unitPrice);

        // Clear fields
        priceTypeCombo.getSelectionModel().clearSelection();
        unitOptionField.clear();
        costField.clear();
        priceField.clear();
        colorField.clear();
    }

    private void addTax() {
        // Validate required fields
        if (taxTypeCombo.getValue() == null) {
            showAlert("Validation Error", "Tax type is required.");
            return;
        }

        // Create ProductTax object
        Product.ProductTax tax = new Product.ProductTax();
        tax.setTaxType(taxTypeCombo.getValue());
        tax.setAppliedToSelling(appliedToSellingCheck.isSelected());
        tax.setAppliedToBuying(appliedToBuyingCheck.isSelected());
        tax.setIncludeInPrice(includeInPriceCheck.isSelected());

        // Add to list
        productTaxes.add(tax);

        // Clear fields
        taxTypeCombo.getSelectionModel().clearSelection();
        appliedToSellingCheck.setSelected(false);
        appliedToBuyingCheck.setSelected(false);
        includeInPriceCheck.setSelected(false);
    }

    private void addBarcode() {
        // Validate required fields
        if (barcodeNameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Barcode name is required.");
            return;
        }

        if (barcodeField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Barcode value is required.");
            return;
        }

        // Create Barcode object
        Product.Barcode barcode = new Product.Barcode();
        barcode.setName(barcodeNameField.getText().trim());
        barcode.setBarcode(barcodeField.getText().trim());
        barcode.setDefault(isDefaultBarcodeCheck.isSelected());
        barcode.setStandard(isStandardBarcodeCheck.isSelected());

        // Add to list
        productBarcodes.add(barcode);

        // Clear fields
        barcodeNameField.clear();
        barcodeField.clear();
        isDefaultBarcodeCheck.setSelected(false);
        isStandardBarcodeCheck.setSelected(false);
    }

    private void addAttribute() {
        // Validate required fields
        if (attributeNameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Attribute name is required.");
            return;
        }

        if (attributeValueField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Attribute value is required.");
            return;
        }

        // Create ProductAttribute object
        Product.ProductAttribute attribute = new Product.ProductAttribute();
        attribute.setAttributeName(attributeNameField.getText().trim());
        attribute.setValue(attributeValueField.getText().trim());
        attribute.setArabicValue(arabicValueField.getText().trim());

        // Add to list
        productAttributes.add(attribute);

        // Clear fields
        attributeNameField.clear();
        attributeValueField.clear();
        arabicValueField.clear();
    }
}
