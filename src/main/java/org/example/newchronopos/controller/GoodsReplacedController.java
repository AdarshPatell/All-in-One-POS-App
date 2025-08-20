package org.example.newchronopos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.newchronopos.dao.GoodsReplacedDAO;
import org.example.newchronopos.dao.ProductDAO;
import org.example.newchronopos.model.GoodsReplaced;
import org.example.newchronopos.model.Product;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class GoodsReplacedController {

    @FXML private Button processReplacementButton;
    @FXML private TextField searchField;
    @FXML private TableView<GoodsReplaced> goodsReplacedTable;
    @FXML private TableColumn<GoodsReplaced, Integer> replacementIdColumn;
    @FXML private TableColumn<GoodsReplaced, String> originalProductColumn;
    @FXML private TableColumn<GoodsReplaced, String> replacementProductColumn;
    @FXML private TableColumn<GoodsReplaced, Integer> quantityColumn;
    @FXML private TableColumn<GoodsReplaced, String> reasonColumn;
    @FXML private TableColumn<GoodsReplaced, String> statusColumn;
    @FXML private TableColumn<GoodsReplaced, String> dateColumn;
    @FXML private TableColumn<GoodsReplaced, Void> actionsColumn;

    private ObservableList<GoodsReplaced> goodsReplacedList = FXCollections.observableArrayList();
    private GoodsReplacedDAO goodsReplacedDAO = new GoodsReplacedDAO();
    private ProductDAO productDAO = new ProductDAO();

    @FXML
    public void initialize() {
        setupTable();
        setupButtons();
        loadGoodsReplaced();
    }

    private void setupTable() {
        if (goodsReplacedTable != null) {
            goodsReplacedTable.setItems(goodsReplacedList);

            if (replacementIdColumn != null) {
                replacementIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            }

            if (originalProductColumn != null) {
                originalProductColumn.setCellValueFactory(cellData -> {
                    try {
                        Product product = productDAO.getProductById(cellData.getValue().getOriginalProductId());
                        return new javafx.beans.property.SimpleStringProperty(
                            product != null ? product.getName() : "Unknown Product"
                        );
                    } catch (Exception e) {
                        return new javafx.beans.property.SimpleStringProperty("Unknown Product");
                    }
                });
            }

            if (replacementProductColumn != null) {
                replacementProductColumn.setCellValueFactory(cellData -> {
                    try {
                        Product product = productDAO.getProductById(cellData.getValue().getReplacementProductId());
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

                statusColumn.setCellFactory(col -> new TableCell<GoodsReplaced, String>() {
                    @Override
                    protected void updateItem(String status, boolean empty) {
                        super.updateItem(status, empty);
                        if (empty || status == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(status);
                            switch (status.toLowerCase()) {
                                case "completed":
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

            if (dateColumn != null) {
                dateColumn.setCellValueFactory(cellData -> {
                    if (cellData.getValue().getReplacementDate() != null) {
                        return new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getReplacementDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        );
                    }
                    return new javafx.beans.property.SimpleStringProperty("");
                });
            }

            if (actionsColumn != null) {
                actionsColumn.setCellFactory(col -> new TableCell<GoodsReplaced, Void>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");
                    private final Button processButton = new Button("Process");
                    private final HBox buttons = new HBox(5, editButton, processButton, deleteButton);

                    {
                        editButton.getStyleClass().add("btn-primary");
                        processButton.getStyleClass().add("btn-success");
                        deleteButton.getStyleClass().add("btn-danger");

                        editButton.setOnAction(e -> {
                            GoodsReplaced goods = getTableView().getItems().get(getIndex());
                            editGoodsReplaced(goods);
                        });

                        processButton.setOnAction(e -> {
                            GoodsReplaced goods = getTableView().getItems().get(getIndex());
                            processReplacement(goods);
                        });

                        deleteButton.setOnAction(e -> {
                            GoodsReplaced goods = getTableView().getItems().get(getIndex());
                            deleteGoodsReplaced(goods);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            GoodsReplaced goods = getTableView().getItems().get(getIndex());
                            if ("pending".equalsIgnoreCase(goods.getStatus())) {
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
        if (processReplacementButton != null) {
            processReplacementButton.setOnAction(e -> showAddReplacementDialog());
        }

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                searchGoodsReplaced(newValue);
            });
        }
    }

    private void loadGoodsReplaced() {
        try {
            List<GoodsReplaced> list = goodsReplacedDAO.getAllGoodsReplaced();
            goodsReplacedList.clear();
            goodsReplacedList.addAll(list);
        } catch (Exception e) {
            showAlert("Error", "Failed to load goods replaced: " + e.getMessage());
        }
    }

    private void searchGoodsReplaced(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                loadGoodsReplaced();
            } else {
                List<GoodsReplaced> searchResults = goodsReplacedDAO.searchGoodsReplaced(searchTerm.trim());
                goodsReplacedList.clear();
                goodsReplacedList.addAll(searchResults);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to search goods replaced: " + e.getMessage());
        }
    }

    private void showAddReplacementDialog() {
        Dialog<GoodsReplaced> dialog = new Dialog<>();
        dialog.setTitle("Process Replacement");
        dialog.setHeaderText(null);

        // Create form fields
        ComboBox<Product> originalProductCombo = new ComboBox<>();
        ComboBox<Product> replacementProductCombo = new ComboBox<>();

        try {
            List<Product> products = productDAO.getAllProducts();
            originalProductCombo.getItems().addAll(products);
            replacementProductCombo.getItems().addAll(products);
        } catch (Exception e) {
            showAlert("Error", "Failed to load products: " + e.getMessage());
            return;
        }

        // Setup original product combo
        originalProductCombo.setCellFactory(param -> new ListCell<Product>() {
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

        originalProductCombo.setButtonCell(new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select Original Product");
                } else {
                    setText(item.getName() + " (" + item.getSku() + ")");
                }
            }
        });

        // Setup replacement product combo
        replacementProductCombo.setCellFactory(param -> new ListCell<Product>() {
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

        replacementProductCombo.setButtonCell(new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select Replacement Product");
                } else {
                    setText(item.getName() + " (" + item.getSku() + ")");
                }
            }
        });

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity to Replace");

        ComboBox<String> reasonCombo = new ComboBox<>();
        reasonCombo.getItems().addAll("Defective", "Damaged", "Wrong Item", "Customer Request", "Other");
        reasonCombo.setValue("Defective");

        TextField customReasonField = new TextField();
        customReasonField.setPromptText("Custom Reason (if Other selected)");
        customReasonField.setDisable(true);

        // Enable custom reason field when "Other" is selected
        reasonCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            customReasonField.setDisable(!"Other".equals(newVal));
        });

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(java.time.LocalDate.now());

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Additional Notes");
        notesArea.setPrefRowCount(3);

        // Create layout
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Original Product:"), 0, 0);
        grid.add(originalProductCombo, 1, 0);
        grid.add(new Label("Replacement Product:"), 0, 1);
        grid.add(replacementProductCombo, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(new Label("Reason:"), 0, 3);
        grid.add(reasonCombo, 1, 3);
        grid.add(new Label("Custom Reason:"), 0, 4);
        grid.add(customReasonField, 1, 4);
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
            boolean valid = originalProductCombo.getValue() != null &&
                           replacementProductCombo.getValue() != null &&
                           !quantityField.getText().trim().isEmpty() &&
                           reasonCombo.getValue() != null;

            // If "Other" is selected, custom reason must be filled
            if ("Other".equals(reasonCombo.getValue())) {
                valid = valid && !customReasonField.getText().trim().isEmpty();
            }

            okButton.setDisable(!valid);
        };

        originalProductCombo.valueProperty().addListener((obs, old, newVal) -> validateInput.run());
        replacementProductCombo.valueProperty().addListener((obs, old, newVal) -> validateInput.run());
        quantityField.textProperty().addListener((obs, old, newVal) -> validateInput.run());
        reasonCombo.valueProperty().addListener((obs, old, newVal) -> validateInput.run());
        customReasonField.textProperty().addListener((obs, old, newVal) -> validateInput.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    GoodsReplaced goods = new GoodsReplaced();
                    goods.setOriginalProductId(originalProductCombo.getValue().getId());
                    goods.setReplacementProductId(replacementProductCombo.getValue().getId());
                    goods.setQuantity(Integer.parseInt(quantityField.getText().trim()));

                    String reason = reasonCombo.getValue();
                    if ("Other".equals(reason)) {
                        reason = customReasonField.getText().trim();
                    }
                    goods.setReason(reason);

                    goods.setReplacementDate(datePicker.getValue().atStartOfDay());
                    goods.setNotes(notesArea.getText().trim());
                    goods.setStatus("Pending");
                    return goods;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid quantity format");
                    return null;
                }
            }
            return null;
        });

        Optional<GoodsReplaced> result = dialog.showAndWait();
        result.ifPresent(this::saveGoodsReplaced);
    }

    private void editGoodsReplaced(GoodsReplaced goods) {
        // Similar to add dialog but pre-populated
        showAddReplacementDialog(); // Simplified for now
    }

    private void processReplacement(GoodsReplaced goods) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Process Replacement");
        alert.setHeaderText("Process Goods Replacement");
        alert.setContentText("Are you sure you want to process this replacement? This will update inventory levels.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                goods.setStatus("Completed");
                boolean success = goodsReplacedDAO.updateGoodsReplaced(goods);
                if (success) {
                    showAlert("Success", "Replacement processed successfully!");
                    loadGoodsReplaced();
                } else {
                    showAlert("Error", "Failed to process replacement.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred while processing replacement: " + e.getMessage());
            }
        }
    }

    private void deleteGoodsReplaced(GoodsReplaced goods) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Goods Replacement");
        alert.setContentText("Are you sure you want to delete this replacement record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = goodsReplacedDAO.deleteGoodsReplaced(goods.getId());
                if (success) {
                    showAlert("Success", "Replacement record deleted successfully!");
                    loadGoodsReplaced();
                } else {
                    showAlert("Error", "Failed to delete replacement record.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred while deleting replacement: " + e.getMessage());
            }
        }
    }

    private void saveGoodsReplaced(GoodsReplaced goods) {
        try {
            boolean success = goodsReplacedDAO.addGoodsReplaced(goods);
            if (success) {
                showAlert("Success", "Goods replacement added successfully!");
                loadGoodsReplaced();
            } else {
                showAlert("Error", "Failed to add goods replacement.");
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
