package org.example.newchronopos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.newchronopos.dao.ProductDAO;
import org.example.newchronopos.dao.CategoryDAO;
import org.example.newchronopos.model.Category;
import org.example.newchronopos.model.Product;
import org.example.newchronopos.model.ProductView;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ProductsController {

    // Category Management Section
    @FXML private HBox categoriesContainer;
    @FXML private Button addNewCategoryButton;
    @FXML private ComboBox<Category> categoryFilterCombo;

    // Product Management Section
    @FXML private HBox productTabsContainer;
    @FXML private TextField searchField;
    @FXML private Button addProductButton;
    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, String> productNameColumn;
    @FXML private TableColumn<Product, String> productSkuColumn;
    @FXML private TableColumn<Product, String> productCategoryColumn;
    @FXML private TableColumn<Product, Double> productPriceColumn;
    @FXML private TableColumn<Product, Integer> productStockColumn;
    @FXML private TableColumn<Product, String> productStatusColumn;
    @FXML private TableColumn<Product, Void> productActionsColumn;

    private ObservableList<Product> products = FXCollections.observableArrayList();
    private ObservableList<Category> categories = FXCollections.observableArrayList();
    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private Category selectedCategory = null;

    @FXML
    public void initialize() {
        setupProductTable();
        setupButtons();
        setupCategoryFilter();
        loadCategories();
        loadProducts();
    }

    private void setupProductTable() {
        if (productsTable != null) {
            productsTable.setItems(products);

            if (productNameColumn != null) {
                productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            }

            if (productSkuColumn != null) {
                productSkuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
            }

            if (productCategoryColumn != null) {
                productCategoryColumn.setCellValueFactory(cellData -> {
                    try {
                        if (cellData.getValue().getCategoryId() > 0) {
                            Optional<Category> category = categoryDAO.getCategoryById(cellData.getValue().getCategoryId());
                            return new javafx.beans.property.SimpleStringProperty(
                                category.map(Category::getName).orElse("Unknown")
                            );
                        }
                    } catch (Exception e) {
                        // Handle error silently
                    }
                    return new javafx.beans.property.SimpleStringProperty("No Category");
                });
            }

            if (productPriceColumn != null) {
                productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
                productPriceColumn.setCellFactory(col -> new TableCell<Product, Double>() {
                    @Override
                    protected void updateItem(Double price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty || price == null) {
                            setText(null);
                        } else {
                            setText(String.format("$%.2f", price));
                        }
                    }
                });
            }

            if (productStockColumn != null) {
                productStockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
            }

            if (productStatusColumn != null) {
                productStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            }

            if (productActionsColumn != null) {
                productActionsColumn.setCellFactory(col -> new TableCell<Product, Void>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");
                    private final HBox buttons = new HBox(5, editButton, deleteButton);

                    {
                        editButton.getStyleClass().add("btn-primary");
                        deleteButton.getStyleClass().add("btn-danger");

                        editButton.setOnAction(e -> {
                            Product product = getTableView().getItems().get(getIndex());
                            editProduct(product);
                        });

                        deleteButton.setOnAction(e -> {
                            Product product = getTableView().getItems().get(getIndex());
                            deleteProduct(product);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : buttons);
                    }
                });
            }
        }
    }

    private void setupButtons() {
        if (addProductButton != null) {
            addProductButton.setOnAction(e -> openAddProductDialog());
        }

        if (addNewCategoryButton != null) {
            addNewCategoryButton.setOnAction(e -> openAddCategoryDialog());
        }

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                searchProducts(newValue);
            });
        }
    }

    private void setupCategoryFilter() {
        if (categoryFilterCombo != null) {
            categoryFilterCombo.setItems(categories);

            categoryFilterCombo.setCellFactory(param -> new ListCell<Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });

            categoryFilterCombo.setButtonCell(new ListCell<Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("All Categories");
                    } else {
                        setText(item.getName());
                    }
                }
            });

            categoryFilterCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                selectedCategory = newVal;
                filterProductsByCategory();
            });
        }
    }

    private void loadCategories() {
        try {
            List<Category> categoryList = categoryDAO.getAllCategories();
            categories.clear();

            // Add "All Categories" option
            Category allCategories = new Category();
            allCategories.setId(0);
            allCategories.setName("All Categories");
            categories.add(allCategories);

            categories.addAll(categoryList);

            if (categoryFilterCombo != null) {
                categoryFilterCombo.setValue(allCategories);
            }

            // Update the visual category display
            updateCategoriesDisplay();
        } catch (Exception e) {
            showAlert("Error", "Failed to load categories: " + e.getMessage());
        }
    }

    private void updateCategoriesDisplay() {
        if (categoriesContainer != null) {
            categoriesContainer.getChildren().clear();

            for (Category category : categories) {
                Button categoryButton = new Button(category.getName());
                categoryButton.setStyle(
                    "-fx-background-color: " + (selectedCategory != null && selectedCategory.getId() == category.getId() ? "#F4B942" : "#E9ECEF") + ";" +
                    "-fx-text-fill: " + (selectedCategory != null && selectedCategory.getId() == category.getId() ? "white" : "#495057") + ";" +
                    "-fx-background-radius: 20;" +
                    "-fx-padding: 8 15;" +
                    "-fx-border: none;" +
                    "-fx-cursor: hand;"
                );

                categoryButton.setOnAction(e -> {
                    selectedCategory = category;
                    updateCategoriesDisplay(); // Refresh to update button styles
                    filterProductsByCategory(category);
                });

                categoriesContainer.getChildren().add(categoryButton);
            }
        }
    }

    private void loadProducts() {
        try {
            List<Product> productList = productDAO.getAllProducts();
            products.clear();
            products.addAll(productList);
        } catch (Exception e) {
            showAlert("Error", "Failed to load products: " + e.getMessage());
        }
    }

    private void filterProductsByCategory() {
        filterProductsByCategory(selectedCategory);
    }

    private void filterProductsByCategory(Category category) {
        try {
            if (category == null || category.getId() == 0) {
                loadProducts();
            } else {
                List<Product> filteredProducts = productDAO.getProductsByCategory(category.getId());
                products.clear();
                products.addAll(filteredProducts);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to filter products by category: " + e.getMessage());
        }
    }

    private void searchProducts(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                if (selectedCategory == null || selectedCategory.getId() == 0) {
                    loadProducts();
                } else {
                    filterProductsByCategory();
                }
            } else {
                List<Product> searchResults = productDAO.searchProducts(searchTerm.trim());
                products.clear();
                products.addAll(searchResults);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to search products: " + e.getMessage());
        }
    }

    private void openAddProductDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddProduct.fxml"));
            Parent root = loader.load();

            AddProductController controller = loader.getController();
            controller.setParentController(this);
            System.out.println("controller set for AddProductController: " );
            Stage stage = new Stage();
            stage.setTitle("Add New Product");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            System.out.println("Stage initialized for AddProductController");
            stage.showAndWait();
        } catch (IOException e) {
            System.out.println("Failed to open Add Product dialog: " + e.getMessage());
            showAlert("Error", "Failed to open Add Product dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openAddCategoryDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddCategory.fxml"));
            Parent root = loader.load();

            AddCategoryController controller = loader.getController();
            controller.setParentController(null); // Set appropriate parent if needed

            Stage stage = new Stage();
            stage.setTitle("Add New Category");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.showAndWait();

            // Refresh categories after adding
            loadCategories();
        } catch (IOException e) {
            showAlert("Error", "Failed to open Add Category dialog: " + e.getMessage());
        }
    }

    private void editProduct(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddProduct.fxml"));
            Parent root = loader.load();

            AddProductController controller = loader.getController();
            controller.setParentController(this);
            controller.setEditMode(product);

            Stage stage = new Stage();
            stage.setTitle("Edit Product");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Error", "Failed to open Edit Product dialog: " + e.getMessage());
        }
    }

    private void deleteProduct(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Product");
        alert.setContentText("Are you sure you want to delete the product '" + product.getName() + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = productDAO.deleteProduct(product.getId());
                if (success) {
                    showAlert("Success", "Product deleted successfully!");
                    refreshProducts();
                } else {
                    showAlert("Error", "Failed to delete product.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred while deleting product: " + e.getMessage());
            }
        }
    }

    public void refreshProducts() {
        if (selectedCategory == null || selectedCategory.getId() == 0) {
            loadProducts();
        } else {
            filterProductsByCategory();
        }
    }

    public void refreshCategories() {
        loadCategories();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
