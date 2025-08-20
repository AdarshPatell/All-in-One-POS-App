package org.example.newchronopos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.newchronopos.dao.GoodsReceivedDAO;
import org.example.newchronopos.dao.ProductDAO;
import org.example.newchronopos.dao.SupplierDAO;
import org.example.newchronopos.model.GoodsReceived;
import org.example.newchronopos.model.Product;
import org.example.newchronopos.model.Supplier;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class GoodsReceivedController {

    @FXML private Button addNewReceiptButton;
    @FXML private TextField searchField;
    @FXML private TableView<GoodsReceived> goodsReceivedTable;
    @FXML private TableColumn<GoodsReceived, Integer> receiptIdColumn;
    @FXML private TableColumn<GoodsReceived, String> supplierNameColumn;
    @FXML private TableColumn<GoodsReceived, String> productNameColumn;
    @FXML private TableColumn<GoodsReceived, Integer> quantityColumn;
    @FXML private TableColumn<GoodsReceived, Double> unitCostColumn;
    @FXML private TableColumn<GoodsReceived, Double> totalCostColumn;
    @FXML private TableColumn<GoodsReceived, String> statusColumn;
    @FXML private TableColumn<GoodsReceived, String> dateColumn;
    @FXML private TableColumn<GoodsReceived, Void> actionsColumn;

    private ObservableList<GoodsReceived> goodsReceivedList = FXCollections.observableArrayList();
    private GoodsReceivedDAO goodsReceivedDAO = new GoodsReceivedDAO();
    private ProductDAO productDAO = new ProductDAO();
    private SupplierDAO supplierDAO = new SupplierDAO();

    @FXML
    public void initialize() {
        setupTable();
        setupButtons();
        loadGoodsReceived();
    }

    private void setupTable() {
        if (goodsReceivedTable != null) {
            goodsReceivedTable.setItems(goodsReceivedList);
            
            if (receiptIdColumn != null) {
                receiptIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            }
            
            if (supplierNameColumn != null) {
                supplierNameColumn.setCellValueFactory(cellData -> {
                    try {
                        Optional<Supplier> supplier = supplierDAO.getSupplierById(cellData.getValue().getSupplierId());
                        return new javafx.beans.property.SimpleStringProperty(
                            supplier.map(Supplier::getName).orElse("Unknown Supplier")
                        );
                    } catch (Exception e) {
                        return new javafx.beans.property.SimpleStringProperty("Unknown Supplier");
                    }
                });
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
                quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityReceived"));
            }
            
            if (unitCostColumn != null) {
                unitCostColumn.setCellValueFactory(new PropertyValueFactory<>("unitCost"));
                unitCostColumn.setCellFactory(col -> new TableCell<GoodsReceived, Double>() {
                    @Override
                    protected void updateItem(Double cost, boolean empty) {
                        super.updateItem(cost, empty);
                        if (empty || cost == null) {
                            setText(null);
                        } else {
                            setText(String.format("$%.2f", cost));
                        }
                    }
                });
            }
            
            if (totalCostColumn != null) {
                totalCostColumn.setCellValueFactory(cellData -> {
                    GoodsReceived goods = cellData.getValue();
                    double total = goods.getQuantityReceived() * goods.getUnitCost();
                    return new javafx.beans.property.SimpleObjectProperty<>(total);
                });
                totalCostColumn.setCellFactory(col -> new TableCell<GoodsReceived, Double>() {
                    @Override
                    protected void updateItem(Double total, boolean empty) {
                        super.updateItem(total, empty);
                        if (empty || total == null) {
                            setText(null);
                        } else {
                            setText(String.format("$%.2f", total));
                        }
                    }
                });
            }
            
            if (statusColumn != null) {
                statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
                
                statusColumn.setCellFactory(col -> new TableCell<GoodsReceived, String>() {
                    @Override
                    protected void updateItem(String status, boolean empty) {
                        super.updateItem(status, empty);
                        if (empty || status == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(status);
                            switch (status.toLowerCase()) {
                                case "received":
                                    setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                                    break;
                                case "pending":
                                    setStyle("-fx-text-fill: #f57c00; -fx-font-weight: bold;");
                                    break;
                                case "damaged":
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
                    if (cellData.getValue().getReceivedDate() != null) {
                        return new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getReceivedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        );
                    }
                    return new javafx.beans.property.SimpleStringProperty("");
                });
            }
            
            if (actionsColumn != null) {
                actionsColumn.setCellFactory(col -> new TableCell<GoodsReceived, Void>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");
                    private final HBox buttons = new HBox(5, editButton, deleteButton);

                    {
                        editButton.getStyleClass().add("btn-primary");
                        deleteButton.getStyleClass().add("btn-danger");
                        
                        editButton.setOnAction(e -> {
                            GoodsReceived goods = getTableView().getItems().get(getIndex());
                            editGoodsReceived(goods);
                        });
                        
                        deleteButton.setOnAction(e -> {
                            GoodsReceived goods = getTableView().getItems().get(getIndex());
                            deleteGoodsReceived(goods);
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
        if (addNewReceiptButton != null) {
            addNewReceiptButton.setOnAction(e -> showAddReceiptDialog());
        }
        
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                searchGoodsReceived(newValue);
            });
        }
    }

    private void loadGoodsReceived() {
        try {
            List<GoodsReceived> list = goodsReceivedDAO.getAllGoodsReceived();
            goodsReceivedList.clear();
            goodsReceivedList.addAll(list);
        } catch (Exception e) {
            showAlert("Error", "Failed to load goods received: " + e.getMessage());
        }
    }

    private void searchGoodsReceived(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                loadGoodsReceived();
            } else {
                List<GoodsReceived> searchResults = goodsReceivedDAO.searchGoodsReceived(searchTerm.trim());
                goodsReceivedList.clear();
                goodsReceivedList.addAll(searchResults);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to search goods received: " + e.getMessage());
        }
    }

    private void showAddReceiptDialog() {
        Dialog<GoodsReceived> dialog = new Dialog<>();
        dialog.setTitle("Add New Receipt");
        dialog.setHeaderText(null);

        // Create form fields
        ComboBox<Supplier> supplierCombo = new ComboBox<>();
        ComboBox<Product> productCombo = new ComboBox<>();
        
        try {
            supplierCombo.getItems().addAll(supplierDAO.getAllSuppliers());
            productCombo.getItems().addAll(productDAO.getAllProducts());
        } catch (Exception e) {
            showAlert("Error", "Failed to load suppliers/products: " + e.getMessage());
            return;
        }
        
        // Setup supplier combo
        supplierCombo.setCellFactory(param -> new ListCell<Supplier>() {
            @Override
            protected void updateItem(Supplier item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        
        supplierCombo.setButtonCell(new ListCell<Supplier>() {
            @Override
            protected void updateItem(Supplier item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select Supplier");
                } else {
                    setText(item.getName());
                }
            }
        });
        
        // Setup product combo
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
        quantityField.setPromptText("Quantity Received");
        
        TextField unitCostField = new TextField();
        unitCostField.setPromptText("Unit Cost");
        
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Received", "Pending", "Damaged");
        statusCombo.setValue("Received");
        
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(java.time.LocalDate.now());
        
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Notes");
        notesArea.setPrefRowCount(3);

        // Create layout
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        grid.add(new Label("Supplier:"), 0, 0);
        grid.add(supplierCombo, 1, 0);
        grid.add(new Label("Product:"), 0, 1);
        grid.add(productCombo, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(new Label("Unit Cost:"), 0, 3);
        grid.add(unitCostField, 1, 3);
        grid.add(new Label("Status:"), 0, 4);
        grid.add(statusCombo, 1, 4);
        grid.add(new Label("Date:"), 0, 5);
        grid.add(datePicker, 1, 5);
        grid.add(new Label("Notes:"), 0, 6);
        grid.add(notesArea, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Enable/disable OK button based on input
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        
        Runnable validateInput = () -> {
            boolean valid = supplierCombo.getValue() != null && 
                           productCombo.getValue() != null &&
                           !quantityField.getText().trim().isEmpty() &&
                           !unitCostField.getText().trim().isEmpty();
            okButton.setDisable(!valid);
        };
        
        supplierCombo.valueProperty().addListener((obs, old, newVal) -> validateInput.run());
        productCombo.valueProperty().addListener((obs, old, newVal) -> validateInput.run());
        quantityField.textProperty().addListener((obs, old, newVal) -> validateInput.run());
        unitCostField.textProperty().addListener((obs, old, newVal) -> validateInput.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    GoodsReceived goods = new GoodsReceived();
                    goods.setSupplierId(supplierCombo.getValue().getId());
                    goods.setProductId(productCombo.getValue().getId());
                    goods.setQuantityReceived(Integer.parseInt(quantityField.getText().trim()));
                    goods.setUnitCost(Double.parseDouble(unitCostField.getText().trim()));
                    goods.setStatus(statusCombo.getValue());
                    goods.setReceivedDate(datePicker.getValue());
                    goods.setNotes(notesArea.getText().trim());
                    return goods;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid number format");
                    return null;
                }
            }
            return null;
        });

        Optional<GoodsReceived> result = dialog.showAndWait();
        result.ifPresent(this::saveGoodsReceived);
    }

    private void editGoodsReceived(GoodsReceived goods) {
        // Similar to add dialog but pre-populated
        showAddReceiptDialog(); // Simplified for now
    }

    private void deleteGoodsReceived(GoodsReceived goods) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Goods Received");
        alert.setContentText("Are you sure you want to delete this receipt?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = goodsReceivedDAO.deleteGoodsReceived(goods.getId());
                if (success) {
                    showAlert("Success", "Receipt deleted successfully!");
                    loadGoodsReceived();
                } else {
                    showAlert("Error", "Failed to delete receipt.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred while deleting receipt: " + e.getMessage());
            }
        }
    }

    private void saveGoodsReceived(GoodsReceived goods) {
        try {
            boolean success = goodsReceivedDAO.addGoodsReceived(goods);
            if (success) {
                showAlert("Success", "Goods received added successfully!");
                loadGoodsReceived();
            } else {
                showAlert("Error", "Failed to add goods received.");
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
