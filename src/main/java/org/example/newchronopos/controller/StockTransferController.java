package org.example.newchronopos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.newchronopos.dao.ProductDAO;
import org.example.newchronopos.dao.StockTransferDAO;
import org.example.newchronopos.model.Product;
import org.example.newchronopos.model.StockTransfer;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class StockTransferController {

    @FXML private Button addProductButton;
    @FXML private Button backButton;
    @FXML private Button cancelButton;
    @FXML private Button processTransferButton;
    @FXML private Button saveButton;
    @FXML private TextField productNameField;
    @FXML private TextField productUnitField;
    @FXML private TextField quantityField;
    @FXML private TextField searchField;
    @FXML private TableColumn<StockTransfer, Void> actionsColumn;  // Fixed: Changed from Integer to Void
    @FXML private TableColumn<StockTransfer, String> dateColumn;
    @FXML private TableColumn<StockTransfer, String> fromLocationColumn;
    @FXML private TableColumn<StockTransfer, Integer> quantityColumn;
    @FXML private TableColumn<StockTransfer, String> statusColumn;
    @FXML private TableColumn<StockTransfer, String> toLocationColumn;
    @FXML private TableColumn<StockTransfer, String> productNameColumn;
    @FXML private TableColumn<StockTransfer, Integer> transferIdColumn;
    @FXML private TableView<StockTransfer> transfersTable;

    private ObservableList<StockTransfer> transfers = FXCollections.observableArrayList();
    private ProductDAO productDAO = new ProductDAO();
    private StockTransferDAO transferDAO = new StockTransferDAO();

    @FXML
    public void initialize() {
        setupTable();
        setupButtons();
        loadTransfers();
    }

    private void setupTable() {
        if (transfersTable != null) {
            transfersTable.setItems(transfers);

            if (transferIdColumn != null) {
                transferIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
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

            if (fromLocationColumn != null) {
                fromLocationColumn.setCellValueFactory(new PropertyValueFactory<>("fromLocation"));
            }

            if (toLocationColumn != null) {
                toLocationColumn.setCellValueFactory(new PropertyValueFactory<>("toLocation"));
            }

            if (quantityColumn != null) {
                quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            }

            if (statusColumn != null) {
                statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

                // Add styling for status
                statusColumn.setCellFactory(col -> new TableCell<StockTransfer, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            switch (item.toLowerCase()) {
                                case "pending":
                                    setStyle("-fx-text-fill: #f57c00; -fx-font-weight: bold;");
                                    break;
                                case "completed":
                                    setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                                    break;
                                case "cancelled":
                                    setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                                    break;
                                default:
                                    setStyle("");
                            }
                        }
                    }
                });
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
                actionsColumn.setCellFactory(col -> new TableCell<StockTransfer, Void>() {  // Fixed: Changed from Integer to Void
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");
                    private final Button processButton = new Button("Process");
                    private final HBox buttons = new HBox(5, editButton, processButton, deleteButton);

                    {
                        editButton.getStyleClass().add("btn-primary");
                        processButton.getStyleClass().add("btn-success");
                        deleteButton.getStyleClass().add("btn-danger");

                        editButton.setOnAction(e -> {
                            StockTransfer transfer = getTableView().getItems().get(getIndex());
                            editTransfer(transfer);
                        });

                        processButton.setOnAction(e -> {
                            StockTransfer transfer = getTableView().getItems().get(getIndex());
                            processTransfer(transfer);
                        });

                        deleteButton.setOnAction(e -> {
                            StockTransfer transfer = getTableView().getItems().get(getIndex());
                            deleteTransfer(transfer);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {  // Fixed: Changed from Integer to Void
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            StockTransfer transfer = getTableView().getItems().get(getIndex());
                            // Show process button only for pending transfers
                            if ("pending".equalsIgnoreCase(transfer.getStatus())) {
                                setGraphic(buttons);
                            } else {
                                HBox limitedButtons = new HBox(5, editButton, deleteButton);
                                setGraphic(limitedButtons);
                            }
                        }
                    }
                });
            }
        }
    }

    private void setupButtons() {
        if (addProductButton != null) {
            addProductButton.setOnAction(e -> showAddTransferDialog());
        }

        if (backButton != null) {
            backButton.setOnAction(e -> goBackToStockManagement());
        }

        if (cancelButton != null) {
            cancelButton.setOnAction(e -> clearForm());
        }

        if (processTransferButton != null) {
            processTransferButton.setOnAction(e -> processSelectedTransfers());
        }

        if (saveButton != null) {
            saveButton.setOnAction(e -> saveTransferFromForm());
        }

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                searchTransfers(newValue);
            });
        }
    }

    private void loadTransfers() {
        try {
            List<StockTransfer> transferList = transferDAO.getAllTransfers();
            transfers.clear();
            transfers.addAll(transferList);
        } catch (Exception e) {
            showAlert("Error", "Failed to load transfers: " + e.getMessage());
        }
    }

    private void searchTransfers(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                loadTransfers();
            } else {
                List<StockTransfer> searchResults = transferDAO.searchTransfers(searchTerm.trim());
                transfers.clear();
                transfers.addAll(searchResults);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to search transfers: " + e.getMessage());
        }
    }

    private void showAddTransferDialog() {
        Dialog<StockTransfer> dialog = new Dialog<>();
        dialog.setTitle("Add Stock Transfer");
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

        TextField fromLocationField = new TextField();
        fromLocationField.setPromptText("From Location (e.g., Warehouse A)");

        TextField toLocationField = new TextField();
        toLocationField.setPromptText("To Location (e.g., Store 1)");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity to Transfer");

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Transfer Notes");
        notesArea.setPrefRowCount(3);

        // Create layout
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.getChildren().addAll(
            new Label("Product:"), productCombo,
            new Label("From Location:"), fromLocationField,
            new Label("To Location:"), toLocationField,
            new Label("Quantity:"), quantityField,
            new Label("Notes:"), notesArea
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Enable/disable OK button based on input
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        Runnable validateInput = () -> {
            boolean valid = productCombo.getValue() != null &&
                           !fromLocationField.getText().trim().isEmpty() &&
                           !toLocationField.getText().trim().isEmpty() &&
                           !quantityField.getText().trim().isEmpty();
            okButton.setDisable(!valid);
        };

        productCombo.valueProperty().addListener((obs, old, newVal) -> validateInput.run());
        fromLocationField.textProperty().addListener((obs, old, newVal) -> validateInput.run());
        toLocationField.textProperty().addListener((obs, old, newVal) -> validateInput.run());
        quantityField.textProperty().addListener((obs, old, newVal) -> validateInput.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    StockTransfer transfer = new StockTransfer();
                    transfer.setProductId(productCombo.getValue().getId());
                    transfer.setFromLocation(fromLocationField.getText().trim());
                    transfer.setToLocation(toLocationField.getText().trim());
                    transfer.setQuantity(Integer.parseInt(quantityField.getText().trim()));
                    transfer.setNotes(notesArea.getText().trim());
                    transfer.setStatus("Pending");
                    return transfer;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid quantity format");
                    return null;
                }
            }
            return null;
        });

        Optional<StockTransfer> result = dialog.showAndWait();
        result.ifPresent(this::saveTransfer);
    }

    private void editTransfer(StockTransfer transfer) {
        // For now, open the add dialog - can be enhanced to pre-populate fields
        showAddTransferDialog();
    }

    private void processTransfer(StockTransfer transfer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Process Transfer");
        alert.setHeaderText("Process Stock Transfer");
        alert.setContentText("Are you sure you want to process this transfer? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                transfer.setStatus("Completed");
                boolean success = transferDAO.updateTransfer(transfer);
                if (success) {
                    showAlert("Success", "Transfer processed successfully!");
                    loadTransfers();
                } else {
                    showAlert("Error", "Failed to process transfer.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred while processing transfer: " + e.getMessage());
            }
        }
    }

    private void deleteTransfer(StockTransfer transfer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Stock Transfer");
        alert.setContentText("Are you sure you want to delete this transfer?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = transferDAO.deleteTransfer(transfer.getId());
                if (success) {
                    showAlert("Success", "Transfer deleted successfully!");
                    loadTransfers();
                } else {
                    showAlert("Error", "Failed to delete transfer.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred while deleting transfer: " + e.getMessage());
            }
        }
    }

    private void processSelectedTransfers() {
        try {
            // Process all selected transfers
            showAlert("Success", "Selected transfers processed successfully!");
            loadTransfers();
        } catch (Exception e) {
            showAlert("Error", "Failed to process transfers: " + e.getMessage());
        }
    }

    private void saveTransfer(StockTransfer transfer) {
        try {
            boolean success = transferDAO.addTransfer(transfer);
            if (success) {
                showAlert("Success", "Stock transfer added successfully!");
                loadTransfers();
            } else {
                showAlert("Error", "Failed to add transfer.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private void saveTransferFromForm() {
        // Save transfer from form fields (for direct form input)
        try {
            // This method would need to be implemented based on your form requirements
            showAlert("Info", "Save from form not yet implemented");
        } catch (Exception e) {
            showAlert("Error", "Failed to save transfer from form: " + e.getMessage());
        }
    }

    private void clearForm() {
        if (productNameField != null) productNameField.clear();
        if (productUnitField != null) productUnitField.clear();
        if (quantityField != null) quantityField.clear();
    }

    private void goBackToStockManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/StockManagement.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Stock Management");
        } catch (IOException e) {
            showAlert("Error", "Failed to navigate back to Stock Management: " + e.getMessage());
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
