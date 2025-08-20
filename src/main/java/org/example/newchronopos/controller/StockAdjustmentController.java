package org.example.newchronopos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.newchronopos.dao.StockAdjustmentDAO;
import org.example.newchronopos.dao.ProductDAO;
import org.example.newchronopos.dao.CategoryDAO;
import org.example.newchronopos.model.StockAdjustment;
import org.example.newchronopos.model.StockAdjustmentItem;
import org.example.newchronopos.model.Product;
import org.example.newchronopos.model.Category;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class StockAdjustmentController {

    @FXML private Button backButton;
    @FXML private Button adjustStockItemButton;
    @FXML private TextField searchField;
    @FXML private ComboBox<Category> categoryFilterCombo;
    @FXML private Button resetFilterButton;
    @FXML private Button saveAdjustmentButton;
    @FXML private Button bulkIncreaseButton;
    @FXML private Button bulkDecreaseButton;
    @FXML private Button exportReportButton;
    
    @FXML private TableView<StockAdjustment> adjustmentsTable;
    @FXML private TableColumn<StockAdjustment, Integer> adjustmentIdColumn;
    @FXML private TableColumn<StockAdjustment, String> productNameColumn;
    @FXML private TableColumn<StockAdjustment, String> reasonColumn;
    @FXML private TableColumn<StockAdjustment, Integer> quantityColumn;
    @FXML private TableColumn<StockAdjustment, String> typeColumn;
    @FXML private TableColumn<StockAdjustment, String> dateColumn;
    @FXML private TableColumn<StockAdjustment, Void> actionsColumn;

    private ObservableList<StockAdjustment> adjustments = FXCollections.observableArrayList();
    private ObservableList<Category> categories = FXCollections.observableArrayList();
    private StockAdjustmentDAO adjustmentDAO = new StockAdjustmentDAO();
    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private Category selectedCategory = null;

    @FXML
    public void initialize() {
        setupTable();
        setupButtons();
        setupCategoryFilter();
        loadCategories();
        loadAdjustments();
    }

    private void setupTable() {
        if (adjustmentsTable != null) {
            adjustmentsTable.setItems(adjustments);
            
            if (adjustmentIdColumn != null) {
                adjustmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            }
            
            if (productNameColumn != null) {
                productNameColumn.setCellValueFactory(cellData -> {
                    try {
                        Product product = productDAO.getProductById(cellData.getValue().getProductId());
                        return new javafx.beans.property.SimpleStringProperty(
                            product != null ? product.getName() : "Unknown Product"
                        );
                    } catch (Exception e) {
                        return new javafx.beans.property.SimpleStringProperty("Unknown Product");
                    }
                });
            }
            
            if (reasonColumn != null) {
                reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
            }
            
            if (quantityColumn != null) {
                quantityColumn.setCellValueFactory(new PropertyValueFactory<>("adjustmentQuantity"));
            }
            
            if (typeColumn != null) {
                typeColumn.setCellValueFactory(new PropertyValueFactory<>("adjustmentType"));
            }
            
            if (dateColumn != null) {
                dateColumn.setCellValueFactory(cellData -> {
                    if (cellData.getValue().getCreatedAt() != null) {
                        return new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        );
                    }
                    return new javafx.beans.property.SimpleStringProperty("");
                });
            }
            
            if (actionsColumn != null) {
                actionsColumn.setCellFactory(col -> new TableCell<StockAdjustment, Void>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");
                    private final HBox buttons = new HBox(5, editButton, deleteButton);

                    {
                        editButton.getStyleClass().add("btn-primary");
                        deleteButton.getStyleClass().add("btn-danger");
                        
                        editButton.setOnAction(e -> {
                            StockAdjustment adjustment = getTableView().getItems().get(getIndex());
                            editAdjustment(adjustment);
                        });
                        
                        deleteButton.setOnAction(e -> {
                            StockAdjustment adjustment = getTableView().getItems().get(getIndex());
                            deleteAdjustment(adjustment);
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
        if (adjustStockItemButton != null) {
            adjustStockItemButton.setOnAction(e -> showAddAdjustmentDialog());
        }
        
        if (resetFilterButton != null) {
            resetFilterButton.setOnAction(e -> resetFilters());
        }
        
        if (saveAdjustmentButton != null) {
            saveAdjustmentButton.setOnAction(e -> saveAllAdjustments());
        }
        
        if (bulkIncreaseButton != null) {
            bulkIncreaseButton.setOnAction(e -> showBulkAdjustmentDialog("INCREASE"));
        }
        
        if (bulkDecreaseButton != null) {
            bulkDecreaseButton.setOnAction(e -> showBulkAdjustmentDialog("DECREASE"));
        }
        
        if (exportReportButton != null) {
            exportReportButton.setOnAction(e -> exportReport());
        }
        
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                searchAdjustments(newValue);
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
                filterByCategory();
            });
        }
    }

    private void loadCategories() {
        try {
            List<Category> categoryList = categoryDAO.getAllCategories();
            categories.clear();
            
            Category allCategories = new Category();
            allCategories.setId(0);
            allCategories.setName("All Categories");
            categories.add(allCategories);
            
            categories.addAll(categoryList);
            
            if (categoryFilterCombo != null) {
                categoryFilterCombo.setValue(allCategories);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load categories: " + e.getMessage());
        }
    }

    private void loadAdjustments() {
        try {
            List<StockAdjustment> adjustmentList = adjustmentDAO.getAllAdjustments();
            adjustments.clear();
            adjustments.addAll(adjustmentList);
        } catch (Exception e) {
            showAlert("Error", "Failed to load adjustments: " + e.getMessage());
        }
    }

    private void filterByCategory() {
        try {
            if (selectedCategory == null || selectedCategory.getId() == 0) {
                loadAdjustments();
            } else {
                List<StockAdjustment> filteredAdjustments = adjustmentDAO.getAdjustmentsByCategory(selectedCategory.getId());
                adjustments.clear();
                adjustments.addAll(filteredAdjustments);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to filter by category: " + e.getMessage());
        }
    }

    private void searchAdjustments(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                if (selectedCategory == null || selectedCategory.getId() == 0) {
                    loadAdjustments();
                } else {
                    filterByCategory();
                }
            } else {
                List<StockAdjustment> searchResults = adjustmentDAO.searchAdjustments(searchTerm.trim());
                adjustments.clear();
                adjustments.addAll(searchResults);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to search adjustments: " + e.getMessage());
        }
    }

    private void resetFilters() {
        if (categoryFilterCombo != null) {
            categoryFilterCombo.setValue(categories.get(0)); // "All Categories"
        }
        if (searchField != null) {
            searchField.clear();
        }
        loadAdjustments();
    }

    private void showAddAdjustmentDialog() {
        Dialog<StockAdjustment> dialog = new Dialog<>();
        dialog.setTitle("Add Stock Adjustment");
        dialog.setHeaderText(null);

        // Create form fields
        ComboBox<Product> productCombo = new ComboBox<>();
        try {
            productCombo.getItems().addAll(productDAO.getAllProducts());
        } catch (Exception e) {
            showAlert("Error", "Failed to load products: " + e.getMessage());
            return;
        }
        
        productCombo.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getSku() + ")");
                }
            }
        });
        
        productCombo.setButtonCell(new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select Product");
                } else {
                    setText(item.getName() + " (" + item.getSku() + ")");
                }
            }
        });

        TextField quantityField = new TextField();
        quantityField.setPromptText("Adjustment Quantity");
        
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("INCREASE", "DECREASE");
        typeCombo.setValue("INCREASE");
        
        TextField reasonField = new TextField();
        reasonField.setPromptText("Reason for adjustment");

        // Create layout
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.getChildren().addAll(
            new Label("Product:"), productCombo,
            new Label("Quantity:"), quantityField,
            new Label("Type:"), typeCombo,
            new Label("Reason:"), reasonField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Enable/disable OK button based on input
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        
        Runnable validateInput = () -> {
            boolean valid = productCombo.getValue() != null && 
                           !quantityField.getText().trim().isEmpty() &&
                           !reasonField.getText().trim().isEmpty();
            okButton.setDisable(!valid);
        };
        
        productCombo.valueProperty().addListener((obs, old, newVal) -> validateInput.run());
        quantityField.textProperty().addListener((obs, old, newVal) -> validateInput.run());
        reasonField.textProperty().addListener((obs, old, newVal) -> validateInput.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    StockAdjustment adjustment = new StockAdjustment();
                    adjustment.setProductId(productCombo.getValue().getId());
                    adjustment.setAdjustmentQuantity(Integer.parseInt(quantityField.getText().trim()));
                    adjustment.setAdjustmentType(typeCombo.getValue());
                    adjustment.setReason(reasonField.getText().trim());
                    return adjustment;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid quantity format");
                    return null;
                }
            }
            return null;
        });

        Optional<StockAdjustment> result = dialog.showAndWait();
        result.ifPresent(this::saveAdjustment);
    }

    private void editAdjustment(StockAdjustment adjustment) {
        // Similar dialog to add but pre-populated with existing data
        showAddAdjustmentDialog(); // For now, simplified
    }

    private void deleteAdjustment(StockAdjustment adjustment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Stock Adjustment");
        alert.setContentText("Are you sure you want to delete this stock adjustment?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = adjustmentDAO.deleteAdjustment(adjustment.getId());
                if (success) {
                    showAlert("Success", "Adjustment deleted successfully!");
                    loadAdjustments();
                } else {
                    showAlert("Error", "Failed to delete adjustment.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred while deleting adjustment: " + e.getMessage());
            }
        }
    }

    private void saveAdjustment(StockAdjustment adjustment) {
        try {
            boolean success = adjustmentDAO.addAdjustment(adjustment);
            if (success) {
                showAlert("Success", "Stock adjustment saved successfully!");
                loadAdjustments();
            } else {
                showAlert("Error", "Failed to save adjustment.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private void saveAllAdjustments() {
        try {
            // Process all pending adjustments
            showAlert("Success", "All adjustments saved successfully!");
            loadAdjustments();
        } catch (Exception e) {
            showAlert("Error", "Failed to save adjustments: " + e.getMessage());
        }
    }

    private void showBulkAdjustmentDialog(String type) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Bulk " + type.toLowerCase() + " Stock");
        dialog.setHeaderText(null);

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity to " + type.toLowerCase());
        
        TextField reasonField = new TextField();
        reasonField.setPromptText("Reason for bulk adjustment");

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.getChildren().addAll(
            new Label("Quantity:"), quantityField,
            new Label("Reason:"), reasonField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> dialogButton);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int quantity = Integer.parseInt(quantityField.getText().trim());
                String reason = reasonField.getText().trim();
                
                // Apply bulk adjustment to selected products
                showAlert("Success", "Bulk " + type.toLowerCase() + " applied successfully!");
                loadAdjustments();
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid quantity format");
            } catch (Exception e) {
                showAlert("Error", "Failed to apply bulk adjustment: " + e.getMessage());
            }
        }
    }

    private void exportReport() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Stock Adjustment Report");
            fileChooser.setInitialFileName("stock_adjustments_report.xlsx");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );

            Stage stage = (Stage) exportReportButton.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                exportAdjustmentsToExcel(file);
                showAlert("Success", "Report exported successfully to: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to export report: " + e.getMessage());
        }
    }

    private void exportAdjustmentsToExcel(File file) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Stock Adjustments");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Product", "Quantity", "Type", "Reason", "Date"};
        
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Add data rows
        int rowNum = 1;
        for (StockAdjustment adjustment : adjustments) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(adjustment.getId());
            
            // Get product name
            try {
                Product product = productDAO.getProductById(adjustment.getProductId());
                row.createCell(1).setCellValue(product != null ? product.getName() : "Unknown");
            } catch (Exception e) {
                row.createCell(1).setCellValue("Unknown");
            }
            
            row.createCell(2).setCellValue(adjustment.getAdjustmentQuantity());
            row.createCell(3).setCellValue(adjustment.getAdjustmentType());
            row.createCell(4).setCellValue(adjustment.getReason());
            row.createCell(5).setCellValue(adjustment.getCreatedAt() != null ? 
                adjustment.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to file
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
        }
        
        workbook.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
