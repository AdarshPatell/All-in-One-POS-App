package org.example.newchronopos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.newchronopos.dao.ProductDAO;
import org.example.newchronopos.dao.StockAdjustmentDAO;
import org.example.newchronopos.dao.StockTransferDAO;
import org.example.newchronopos.dao.GoodsReceivedDAO;
import org.example.newchronopos.dao.GoodsReplacedDAO;
import org.example.newchronopos.dao.GoodsReturnDAO;
import org.example.newchronopos.model.Product;
import org.example.newchronopos.util.ModularLayoutManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StockManagementController implements Initializable {

    @FXML private VBox stockAdjustmentButton;
    @FXML private VBox stockTransferButton;
    @FXML private VBox goodsReceivedButton;
    @FXML private VBox goodsReplacedButton;
    @FXML private VBox goodsReturnButton;

    @FXML private Label stockAdjustmentCount;
    @FXML private Label stockTransferCount;
    @FXML private Label goodsReceivedCount;
    @FXML private Label goodsReplacedCount;
    @FXML private Label goodsReturnCount;

    @FXML private Button exportStockButton;
    @FXML private TableView<Product> stockTable;
    @FXML private TableColumn<Product, String> productNameColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, Integer> currentStockColumn;
    @FXML private TableColumn<Product, Integer> minimumStockColumn;
    @FXML private TableColumn<Product, String> statusColumn;
    @FXML private TextField searchField;

    private ObservableList<Product> stockItems = FXCollections.observableArrayList();
    private ProductDAO productDAO = new ProductDAO();
    private StockAdjustmentDAO stockAdjustmentDAO = new StockAdjustmentDAO();
    private StockTransferDAO stockTransferDAO = new StockTransferDAO();
    private GoodsReceivedDAO goodsReceivedDAO = new GoodsReceivedDAO();
    private GoodsReplacedDAO goodsReplacedDAO = new GoodsReplacedDAO();
    private GoodsReturnDAO goodsReturnDAO = new GoodsReturnDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupModuleNavigation();
        setupActions();
        setupStockTable();
        loadCounts();
        loadStockData();
    }

    private void setupModuleNavigation() {
        if (stockAdjustmentButton != null) {
            stockAdjustmentButton.setOnMouseClicked(event -> {
                try {
                    ModularLayoutManager.navigateToStockAdjustment();
                } catch (Exception e) {
                    showAlert("Error", "Failed to navigate to Stock Adjustment: " + e.getMessage());
                }
            });
        }

        if (stockTransferButton != null) {
            stockTransferButton.setOnMouseClicked(event -> {
                try {
                    ModularLayoutManager.navigateToStockTransfer();
                } catch (Exception e) {
                    showAlert("Error", "Failed to navigate to Stock Transfer: " + e.getMessage());
                }
            });
        }

        if (goodsReceivedButton != null) {
            goodsReceivedButton.setOnMouseClicked(event -> {
                try {
                    ModularLayoutManager.navigateToGoodsReceived();
                } catch (Exception e) {
                    showAlert("Error", "Failed to navigate to Goods Received: " + e.getMessage());
                }
            });
        }

        if (goodsReplacedButton != null) {
            goodsReplacedButton.setOnMouseClicked(event -> {
                try {
                    ModularLayoutManager.navigateToGoodsReplaced();
                } catch (Exception e) {
                    showAlert("Error", "Failed to navigate to Goods Replaced: " + e.getMessage());
                }
            });
        }

        if (goodsReturnButton != null) {
            goodsReturnButton.setOnMouseClicked(event -> {
                try {
                    ModularLayoutManager.navigateToGoodsReturn();
                } catch (Exception e) {
                    showAlert("Error", "Failed to navigate to Goods Return: " + e.getMessage());
                }
            });
        }
    }

    private void setupActions() {
        if (exportStockButton != null) {
            exportStockButton.setOnAction(e -> exportToExcel());
        }

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                searchStock(newValue);
            });
        }
    }

    private void setupStockTable() {
        if (stockTable != null) {
            stockTable.setItems(stockItems);

            if (productNameColumn != null) {
                productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            }

            if (categoryColumn != null) {
                categoryColumn.setCellValueFactory(cellData -> {
                    // This would need to be implemented based on your Product model
                    return new javafx.beans.property.SimpleStringProperty("General"); // Placeholder
                });
            }

            if (currentStockColumn != null) {
                currentStockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

                // Add styling for low stock items
                currentStockColumn.setCellFactory(col -> new TableCell<Product, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item.toString());
                            Product product = getTableView().getItems().get(getIndex());
                            if (item <= product.getMinimumStock()) {
                                setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828;");
                            } else {
                                setStyle("");
                            }
                        }
                    }
                });
            }

            if (minimumStockColumn != null) {
                minimumStockColumn.setCellValueFactory(new PropertyValueFactory<>("minimumStock"));
            }

            if (statusColumn != null) {
                statusColumn.setCellValueFactory(cellData -> {
                    Product product = cellData.getValue();
                    String status;
                    if (product.getStockQuantity() <= 0) {
                        status = "Out of Stock";
                    } else if (product.getStockQuantity() <= product.getMinimumStock()) {
                        status = "Low Stock";
                    } else {
                        status = "In Stock";
                    }
                    return new javafx.beans.property.SimpleStringProperty(status);
                });

                // Add styling for status
                statusColumn.setCellFactory(col -> new TableCell<Product, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            switch (item) {
                                case "Out of Stock":
                                    setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                                    break;
                                case "Low Stock":
                                    setStyle("-fx-text-fill: #f57c00; -fx-font-weight: bold;");
                                    break;
                                case "In Stock":
                                    setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                                    break;
                                default:
                                    setStyle("");
                            }
                        }
                    }
                });
            }
        }
    }

    private void loadCounts() {
        try {
            // Load counts for each module
            if (stockAdjustmentCount != null) {
                int adjustmentCount = stockAdjustmentDAO.getTotalAdjustments();
                stockAdjustmentCount.setText(String.valueOf(adjustmentCount));
            }

            if (stockTransferCount != null) {
                int transferCount = stockTransferDAO.getTotalTransfers();
                stockTransferCount.setText(String.valueOf(transferCount));
            }

            if (goodsReceivedCount != null) {
                int receivedCount = goodsReceivedDAO.getTotalReceived();
                goodsReceivedCount.setText(String.valueOf(receivedCount));
            }

            if (goodsReplacedCount != null) {
                int replacedCount = goodsReplacedDAO.getTotalReplaced();
                goodsReplacedCount.setText(String.valueOf(replacedCount));
            }

            if (goodsReturnCount != null) {
                int returnCount = goodsReturnDAO.getTotalReturns();
                goodsReturnCount.setText(String.valueOf(returnCount));
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load counts: " + e.getMessage());
        }
    }

    private void loadStockData() {
        try {
            List<Product> products = productDAO.getAllProducts();
            stockItems.clear();
            stockItems.addAll(products);
        } catch (Exception e) {
            showAlert("Error", "Failed to load stock data: " + e.getMessage());
        }
    }

    private void searchStock(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                loadStockData();
            } else {
                List<Product> searchResults = productDAO.searchProducts(searchTerm.trim());
                stockItems.clear();
                stockItems.addAll(searchResults);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to search stock: " + e.getMessage());
        }
    }

    private void exportToExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Stock Report");
            fileChooser.setInitialFileName("stock_report.xlsx");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );

            Stage stage = (Stage) exportStockButton.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                exportStockToExcel(file);
                showAlert("Success", "Stock report exported successfully to: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to export stock report: " + e.getMessage());
        }
    }

    private void exportStockToExcel(File file) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Stock Report");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Product Name", "SKU", "Current Stock", "Minimum Stock", "Status", "Price"};

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Add data rows
        int rowNum = 1;
        for (Product product : stockItems) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(product.getName());
            row.createCell(1).setCellValue(product.getSku());
            row.createCell(2).setCellValue(product.getStockQuantity());
            row.createCell(3).setCellValue(product.getMinimumStock());

            // Determine status
            String status;
            if (product.getStockQuantity() <= 0) {
                status = "Out of Stock";
            } else if (product.getStockQuantity() <= product.getMinimumStock()) {
                status = "Low Stock";
            } else {
                status = "In Stock";
            }
            row.createCell(4).setCellValue(status);
            row.createCell(5).setCellValue(product.getPrice());
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

    public void refreshData() {
        loadCounts();
        loadStockData();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
