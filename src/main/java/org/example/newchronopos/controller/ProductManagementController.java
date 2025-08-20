package org.example.newchronopos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.newchronopos.dao.ProductDAO;
import org.example.newchronopos.dao.CategoryDAO;
import org.example.newchronopos.model.Product;
import org.example.newchronopos.model.Category;
import org.example.newchronopos.model.ProductView;
import org.example.newchronopos.util.ModularLayoutManager;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductManagementController implements Initializable {

    // Module Navigation Cards
    @FXML private VBox productsButton;
    @FXML private VBox categoriesButton;
    @FXML private VBox suppliersButton;
    @FXML private VBox brandsButton;
    @FXML private VBox unitsButton;

    // Category Management Section
    @FXML private HBox categoriesContainer;
    @FXML private Button addNewCategoryButton;
    @FXML private TableView<Category> categoriesTable;
    @FXML private TableColumn<Category, String> categoryNameColumn;
    @FXML private TableColumn<Category, String> categoryDescriptionColumn;
    @FXML private TableColumn<Category, String> categoryStatusColumn;
    @FXML private TableColumn<Category, String> categoryCreatedColumn;
    @FXML private TableColumn<Category, Void> categoryActionsColumn;
    @FXML private TextField categorySearchField;

    private ObservableList<Category> categories = FXCollections.observableArrayList();
    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupModuleNavigation();
        setupCategoryManagement();
        setupCategoryTable();
        loadCategories();
    }

    private void setupModuleNavigation() {
        // Navigate to Products module
        if (productsButton != null) {
            productsButton.setOnMouseClicked(e -> navigateToModule("Products"));
        }

        // Navigate to Categories module
        if (categoriesButton != null) {
            categoriesButton.setOnMouseClicked(e -> navigateToModule("Categories"));
        }

        // Navigate to Suppliers module
        if (suppliersButton != null) {
            suppliersButton.setOnMouseClicked(e -> navigateToModule("Suppliers"));
        }

        // Navigate to Brands module
        if (brandsButton != null) {
            brandsButton.setOnMouseClicked(e -> navigateToModule("Brands"));
        }

        // Navigate to Units module
        if (unitsButton != null) {
            unitsButton.setOnMouseClicked(e -> navigateToModule("Units"));
        }
    }

    private void setupCategoryManagement() {
        if (addNewCategoryButton != null) {
            addNewCategoryButton.setOnAction(e -> openAddCategoryDialog());
        }

        if (categorySearchField != null) {
            categorySearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                searchCategories(newValue);
            });
        }
    }

    private void setupCategoryTable() {
        if (categoriesTable != null) {
            categoriesTable.setItems(categories);

            if (categoryNameColumn != null) {
                categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            }

            if (categoryDescriptionColumn != null) {
                categoryDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            }

            if (categoryStatusColumn != null) {
                categoryStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            }

            if (categoryCreatedColumn != null) {
                categoryCreatedColumn.setCellValueFactory(cellData -> {
                    if (cellData.getValue().getCreatedAt() != null) {
                        return new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        );
                    }
                    return new javafx.beans.property.SimpleStringProperty("");
                });
            }

            if (categoryActionsColumn != null) {
                categoryActionsColumn.setCellFactory(col -> new TableCell<Category, Void>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");
                    private final HBox buttons = new HBox(5, editButton, deleteButton);

                    {
                        editButton.getStyleClass().add("btn-primary");
                        deleteButton.getStyleClass().add("btn-danger");

                        editButton.setOnAction(e -> {
                            Category category = getTableView().getItems().get(getIndex());
                            editCategory(category);
                        });

                        deleteButton.setOnAction(e -> {
                            Category category = getTableView().getItems().get(getIndex());
                            deleteCategory(category);
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

    private void navigateToModule(String moduleName) {
        try {
            switch (moduleName) {
                case "Products":
                    ModularLayoutManager.navigateToProducts();
                    break;
                case "Suppliers":
                    ModularLayoutManager.navigateToSuppliers();
                    break;
                case "Brands":
                    ModularLayoutManager.navigateToBrands();
                    break;
                case "Units":
                    ModularLayoutManager.navigateToUnits();
                    break;
                default:
                    return;
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to navigate to " + moduleName + ": " + e.getMessage());
        }
    }

    private void openAddCategoryDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddCategory.fxml"));
            Parent root = loader.load();

            AddCategoryController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Add New Category");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // Position the dialog to slide from right
            Stage parentStage = (Stage) addNewCategoryButton.getScene().getWindow();
            stage.setX(parentStage.getX() + parentStage.getWidth());
            stage.setY(parentStage.getY());

            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Error", "Failed to open Add Category dialog: " + e.getMessage());
        }
    }

    private void editCategory(Category category) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddCategory.fxml"));
            Parent root = loader.load();

            AddCategoryController controller = loader.getController();
            controller.setParentController(this);
            controller.setEditMode(category);

            Stage stage = new Stage();
            stage.setTitle("Edit Category");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // Position the dialog to slide from right
            Stage parentStage = (Stage) categoriesTable.getScene().getWindow();
            stage.setX(parentStage.getX() + parentStage.getWidth());
            stage.setY(parentStage.getY());

            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Error", "Failed to open Edit Category dialog: " + e.getMessage());
        }
    }

    private void deleteCategory(Category category) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Category");
        alert.setContentText("Are you sure you want to delete the category '" + category.getName() + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = categoryDAO.deleteCategory(category.getId());
                if (success) {
                    showAlert("Success", "Category deleted successfully!");
                    refreshCategories();
                } else {
                    showAlert("Error", "Failed to delete category.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred while deleting category: " + e.getMessage());
            }
        }
    }

    private void searchCategories(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                loadCategories();
            } else {
                List<Category> searchResults = categoryDAO.searchCategories(searchTerm.trim());
                categories.clear();
                categories.addAll(searchResults);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to search categories: " + e.getMessage());
        }
    }

    public void refreshCategories() {
        loadCategories();
    }

    private void loadCategories() {
        try {
            List<Category> categoryList = categoryDAO.getAllCategories();
            categories.clear();
            categories.addAll(categoryList);
        } catch (Exception e) {
            showAlert("Error", "Failed to load categories: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
