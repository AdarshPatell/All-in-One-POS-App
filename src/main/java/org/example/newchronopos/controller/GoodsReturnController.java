package org.example.newchronopos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.newchronopos.dao.GoodsReturnDAO;
import org.example.newchronopos.dao.ProductDAO;
import org.example.newchronopos.model.GoodsReturn;
import org.example.newchronopos.model.Product;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class GoodsReturnController {

    @FXML private Button processReturnButton;
    @FXML private TextField searchField;
    @FXML private TableView<GoodsReturn> goodsReturnTable;
    @FXML private TableColumn<GoodsReturn, Integer> returnIdColumn;
    @FXML private TableColumn<GoodsReturn, String> productNameColumn;
    @FXML private TableColumn<GoodsReturn, Integer> quantityColumn;
    @FXML private TableColumn<GoodsReturn, String> reasonColumn;
    @FXML private TableColumn<GoodsReturn, String> statusColumn;
    @FXML private TableColumn<GoodsReturn, Double> refundAmountColumn;
    @FXML private TableColumn<GoodsReturn, String> dateColumn;
    @FXML private TableColumn<GoodsReturn, Void> actionsColumn;

    private ObservableList<GoodsReturn> goodsReturnList = FXCollections.observableArrayList();
    private GoodsReturnDAO goodsReturnDAO = new GoodsReturnDAO();
    private ProductDAO productDAO = new ProductDAO();

    @FXML
    public void initialize() {
        setupTable();
        setupButtons();
        loadGoodsReturns();
    }

    private void setupTable() {
        if (goodsReturnTable != null) {
            goodsReturnTable.setItems(goodsReturnList);

            if (returnIdColumn != null) {
                returnIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
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

            if (quantityColumn != null) {
                quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            }

            if (reasonColumn != null) {
                reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
            }

            if (statusColumn != null) {
                statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

                statusColumn.setCellFactory(col -> new TableCell<GoodsReturn, String>() {
                    @Override
                    protected void updateItem(String status, boolean empty) {
                        super.updateItem(status, empty);
                        if (empty || status == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(status);
                            switch (status.toLowerCase()) {
                                case "processed":
                                    setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                                    break;
                                case "pending":
                                    setStyle("-fx-text-fill: #f57c00; -fx-font-weight: bold;");
                                    break;
                                case "rejected":
                                    setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                                    break;
                                default:
                                    setStyle("");
                            }
                        }
                    }
                });
            }

            if (refundAmountColumn != null) {
                refundAmountColumn.setCellValueFactory(new PropertyValueFactory<>("refundAmount"));
                refundAmountColumn.setCellFactory(col -> new TableCell<GoodsReturn, Double>() {
                    @Override
                    protected void updateItem(Double amount, boolean empty) {
                        super.updateItem(amount, empty);
                        if (empty || amount == null) {
                            setText(null);
                        } else {
                            setText(String.format("$%.2f", amount));
                        }
                    }
                });
            }

            if (dateColumn != null) {
                dateColumn.setCellValueFactory(cellData -> {
                    if (cellData.getValue().getReturnDate() != null) {
                        return new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getReturnDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        );
                    }
                    return new javafx.beans.property.SimpleStringProperty("");
                });
            }

            if (actionsColumn != null) {
                actionsColumn.setCellFactory(col -> new TableCell<GoodsReturn, Void>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");
                    private final Button processButton = new Button("Process");
                    private final HBox buttons = new HBox(5, editButton, processButton, deleteButton);

                    {
                        editButton.getStyleClass().add("btn-primary");
                        processButton.getStyleClass().add("btn-success");
                        deleteButton.getStyleClass().add("btn-danger");

                        editButton.setOnAction(e -> {
                            GoodsReturn goodsReturn = getTableView().getItems().get(getIndex());
                            editGoodsReturn(goodsReturn);
                        });

                        processButton.setOnAction(e -> {
                            GoodsReturn goodsReturn = getTableView().getItems().get(getIndex());
                            processReturn(goodsReturn);
                        });

                        deleteButton.setOnAction(e -> {
                            GoodsReturn goodsReturn = getTableView().getItems().get(getIndex());
                            deleteGoodsReturn(goodsReturn);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            GoodsReturn goodsReturn = getTableView().getItems().get(getIndex());
                            if ("pending".equalsIgnoreCase(goodsReturn.getStatus())) {
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
        if (processReturnButton != null) {
            processReturnButton.setOnAction(e -> showAddReturnDialog());
        }

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                searchGoodsReturns(newValue);
            });
        }
    }

    private void loadGoodsReturns() {
        try {
            List<GoodsReturn> list = goodsReturnDAO.getAllGoodsReturns();
            goodsReturnList.clear();
            goodsReturnList.addAll(list);
        } catch (Exception e) {
            showAlert("Error", "Failed to load goods returns: " + e.getMessage());
        }
    }

    private void searchGoodsReturns(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                loadGoodsReturns();
            } else {
                List<GoodsReturn> searchResults = goodsReturnDAO.searchGoodsReturns(searchTerm.trim());
                goodsReturnList.clear();
                goodsReturnList.addAll(searchResults);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to search goods returns: " + e.getMessage());
        }
    }

    private void showAddReturnDialog() {
        Dialog<GoodsReturn> dialog = new Dialog<>();
        dialog.setTitle("Process Return");
        dialog.setHeaderText(null);

        // Create form fields
        ComboBox<Product> productCombo = new ComboBox<>();

        try {
            productCombo.getItems().addAll(productDAO.getAllProducts());
        } catch (Exception e) {
            showAlert("Error", "Failed to load products: " + e.getMessage());
            return;
        }

        // Setup product combo
        productCombo.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getSku() + ") - $" + String.format("%.2f", item.getPrice()));
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
        quantityField.setPromptText("Quantity to Return");

        ComboBox<String> reasonCombo = new ComboBox<>();
        reasonCombo.getItems().addAll("Defective", "Wrong Item", "Not as Described", "Customer Changed Mind", "Damaged in Transit", "Other");
        reasonCombo.setValue("Defective");

        TextField customReasonField = new TextField();
        customReasonField.setPromptText("Custom Reason (if Other selected)");
        customReasonField.setDisable(true);

        // Enable custom reason field when "Other" is selected
        reasonCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            customReasonField.setDisable(!"Other".equals(newVal));
        });

        TextField refundAmountField = new TextField();
        refundAmountField.setPromptText("Refund Amount");

        // Auto-calculate refund amount when product and quantity change
        Runnable calculateRefund = () -> {
            if (productCombo.getValue() != null && !quantityField.getText().trim().isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    double unitPrice = Double.parseDouble(productCombo.getValue().getPrice());
                    double totalRefund = quantity * unitPrice;
                    refundAmountField.setText(String.format("%.2f", totalRefund));
                } catch (NumberFormatException e) {
                    refundAmountField.clear();
                }
            }
        };

        productCombo.valueProperty().addListener((obs, old, newVal) -> calculateRefund.run());
        quantityField.textProperty().addListener((obs, old, newVal) -> calculateRefund.run());

        ComboBox<String> refundMethodCombo = new ComboBox<>();
        refundMethodCombo.getItems().addAll("Cash", "Credit Card", "Store Credit", "Bank Transfer");
        refundMethodCombo.setValue("Cash");

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(java.time.LocalDate.now());

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Return Notes");
        notesArea.setPrefRowCount(3);

        // Create layout
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Product:"), 0, 0);
        grid.add(productCombo, 1, 0);
        grid.add(new Label("Quantity:"), 0, 1);
        grid.add(quantityField, 1, 1);
        grid.add(new Label("Reason:"), 0, 2);
        grid.add(reasonCombo, 1, 2);
        grid.add(new Label("Custom Reason:"), 0, 3);
        grid.add(customReasonField, 1, 3);
        grid.add(new Label("Refund Amount:"), 0, 4);
        grid.add(refundAmountField, 1, 4);
        grid.add(new Label("Refund Method:"), 0, 5);
        grid.add(refundMethodCombo, 1, 5);
        grid.add(new Label("Return Date:"), 0, 6);
        grid.add(datePicker, 1, 6);
        grid.add(new Label("Notes:"), 0, 7);
        grid.add(notesArea, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Enable/disable OK button based on input
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        Runnable validateInput = () -> {
            boolean valid = productCombo.getValue() != null &&
                           !quantityField.getText().trim().isEmpty() &&
                           reasonCombo.getValue() != null &&
                           !refundAmountField.getText().trim().isEmpty();

            // If "Other" is selected, custom reason must be filled
            if ("Other".equals(reasonCombo.getValue())) {
                valid = valid && !customReasonField.getText().trim().isEmpty();
            }

            okButton.setDisable(!valid);
        };

        productCombo.valueProperty().addListener((obs, old, newVal) -> validateInput.run());
        quantityField.textProperty().addListener((obs, old, newVal) -> validateInput.run());
        reasonCombo.valueProperty().addListener((obs, old, newVal) -> validateInput.run());
        customReasonField.textProperty().addListener((obs, old, newVal) -> validateInput.run());
        refundAmountField.textProperty().addListener((obs, old, newVal) -> validateInput.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    GoodsReturn goodsReturn = new GoodsReturn();
                    goodsReturn.setProductId(productCombo.getValue().getId());
                    goodsReturn.setQuantity(Integer.parseInt(quantityField.getText().trim()));

                    String reason = reasonCombo.getValue();
                    if ("Other".equals(reason)) {
                        reason = customReasonField.getText().trim();
                    }
                    goodsReturn.setReason(reason);

                    goodsReturn.setRefundAmount(Double.parseDouble(refundAmountField.getText().trim()));
                    goodsReturn.setRefundMethod(refundMethodCombo.getValue());
                    goodsReturn.setReturnDate(datePicker.getValue());
                    goodsReturn.setNotes(notesArea.getText().trim());
                    goodsReturn.setStatus("Pending");
                    return goodsReturn;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid number format");
                    return null;
                }
            }
            return null;
        });

        Optional<GoodsReturn> result = dialog.showAndWait();
        result.ifPresent(this::saveGoodsReturn);
    }

    private void editGoodsReturn(GoodsReturn goodsReturn) {
        // Similar to add dialog but pre-populated
        showAddReturnDialog(); // Simplified for now
    }

    private void processReturn(GoodsReturn goodsReturn) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Process Return");
        alert.setHeaderText("Process Goods Return");
        alert.setContentText("Are you sure you want to process this return? This will update inventory and initiate the refund.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                goodsReturn.setStatus("Processed");
                boolean success = goodsReturnDAO.updateGoodsReturn(goodsReturn);
                if (success) {
                    showAlert("Success", "Return processed successfully! Refund of $" +
                        String.format("%.2f", goodsReturn.getRefundAmount()) + " has been initiated.");
                    loadGoodsReturns();
                } else {
                    showAlert("Error", "Failed to process return.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred while processing return: " + e.getMessage());
            }
        }
    }

    private void deleteGoodsReturn(GoodsReturn goodsReturn) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Goods Return");
        alert.setContentText("Are you sure you want to delete this return record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = goodsReturnDAO.deleteGoodsReturn(goodsReturn.getId());
                if (success) {
                    showAlert("Success", "Return record deleted successfully!");
                    loadGoodsReturns();
                } else {
                    showAlert("Error", "Failed to delete return record.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred while deleting return: " + e.getMessage());
            }
        }
    }

    private void saveGoodsReturn(GoodsReturn goodsReturn) {
        try {
            boolean success = goodsReturnDAO.addGoodsReturn(goodsReturn);
            if (success) {
                showAlert("Success", "Goods return added successfully!");
                loadGoodsReturns();
            } else {
                showAlert("Error", "Failed to add goods return.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
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
