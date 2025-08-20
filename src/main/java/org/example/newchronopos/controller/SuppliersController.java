package org.example.newchronopos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.newchronopos.dao.SupplierDAO;
import org.example.newchronopos.model.Supplier;

import java.util.List;
import java.util.Optional;

public class SuppliersController {

    @FXML private Button addSupplierButton;
    @FXML private TextField searchField;
    @FXML private TableView<Supplier> suppliersTable;
    @FXML private TableColumn<Supplier, String> supplierIdColumn;
    @FXML private TableColumn<Supplier, String> supplierNameColumn;
    @FXML private TableColumn<Supplier, String> contactPersonColumn;
    @FXML private TableColumn<Supplier, String> emailColumn;
    @FXML private TableColumn<Supplier, String> phoneColumn;
    @FXML private TableColumn<Supplier, String> cityColumn;
    @FXML private TableColumn<Supplier, String> statusColumn;
    @FXML private TableColumn<Supplier, Void> actionsColumn;

    private ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
    private SupplierDAO supplierDAO = new SupplierDAO();

    @FXML
    public void initialize() {
        setupTable();
        setupButtons();
        loadSuppliers();
    }

    private void setupTable() {
        if (suppliersTable != null) {
            suppliersTable.setItems(suppliers);
        }

        if (supplierIdColumn != null) {
            supplierIdColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        }

        if (supplierNameColumn != null) {
            supplierNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        }

        if (contactPersonColumn != null) {
            contactPersonColumn.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        }

        if (emailColumn != null) {
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        }

        if (phoneColumn != null) {
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        }

        if (cityColumn != null) {
            cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        }

        if (statusColumn != null) {
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        }

        if (actionsColumn != null) {
            actionsColumn.setCellFactory(col -> new TableCell<Supplier, Void>() {
                private final Button editButton = new Button("Edit");
                private final Button deleteButton = new Button("Delete");
                private final HBox buttons = new HBox(5, editButton, deleteButton);

                {
                    editButton.getStyleClass().add("btn-primary");
                    deleteButton.getStyleClass().add("btn-danger");

                    editButton.setOnAction(e -> {
                        Supplier supplier = getTableView().getItems().get(getIndex());
                        editSupplier(supplier);
                    });

                    deleteButton.setOnAction(e -> {
                        Supplier supplier = getTableView().getItems().get(getIndex());
                        deleteSupplier(supplier);
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

    private void setupButtons() {
        if (addSupplierButton != null) {
            addSupplierButton.setOnAction(e -> openAddSupplierDialog());
        }

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                searchSuppliers(newValue);
            });
        }
    }

    private void loadSuppliers() {
        try {
            List<Supplier> supplierList = supplierDAO.getAllSuppliers();
            suppliers.clear();
            suppliers.addAll(supplierList);
        } catch (Exception e) {
            showAlert("Error", "Failed to load suppliers: " + e.getMessage());
        }
    }

    private void searchSuppliers(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                loadSuppliers();
            } else {
                List<Supplier> searchResults = supplierDAO.searchSuppliers(searchTerm.trim());
                suppliers.clear();
                suppliers.addAll(searchResults);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to search suppliers: " + e.getMessage());
        }
    }

    private void openAddSupplierDialog() {
        showSupplierDialog(null, "Add New Supplier");
    }

    private void editSupplier(Supplier supplier) {
        showSupplierDialog(supplier, "Edit Supplier");
    }

    private void showSupplierDialog(Supplier supplier, String title) {
        Dialog<Supplier> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        // Create form fields
        TextField nameField = new TextField();
        nameField.setPromptText("Supplier Name");

        TextField contactPersonField = new TextField();
        contactPersonField.setPromptText("Contact Person");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        TextField addressField = new TextField();
        addressField.setPromptText("Address");

        TextField cityField = new TextField();
        cityField.setPromptText("City");

        TextField stateField = new TextField();
        stateField.setPromptText("State");

        TextField countryField = new TextField();
        countryField.setPromptText("Country");

        TextField postalCodeField = new TextField();
        postalCodeField.setPromptText("Postal Code");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Active", "Inactive");
        statusCombo.setValue("Active");

        // Populate fields if editing
        if (supplier != null) {
            nameField.setText(supplier.getName());
            contactPersonField.setText(supplier.getContactPerson());
            emailField.setText(supplier.getEmail());
            phoneField.setText(supplier.getPhone());
            addressField.setText(supplier.getAddress());
            cityField.setText(supplier.getCity());
            stateField.setText(supplier.getState());
            countryField.setText(supplier.getCountry());
            postalCodeField.setText(supplier.getPostalCode());
            statusCombo.setValue(supplier.getStatus());
        }

        // Create layout
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Supplier Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Contact Person:"), 0, 1);
        grid.add(contactPersonField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);
        grid.add(new Label("City:"), 0, 5);
        grid.add(cityField, 1, 5);
        grid.add(new Label("State:"), 0, 6);
        grid.add(stateField, 1, 6);
        grid.add(new Label("Country:"), 0, 7);
        grid.add(countryField, 1, 7);
        grid.add(new Label("Postal Code:"), 0, 8);
        grid.add(postalCodeField, 1, 8);
        grid.add(new Label("Status:"), 0, 9);
        grid.add(statusCombo, 1, 9);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Enable/disable OK button based on input
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Supplier result = supplier != null ? supplier : new Supplier();
                result.setName(nameField.getText().trim());
                result.setContactPerson(contactPersonField.getText().trim());
                result.setEmail(emailField.getText().trim());
                result.setPhone(phoneField.getText().trim());
                result.setAddress(addressField.getText().trim());
                result.setCity(cityField.getText().trim());
                result.setState(stateField.getText().trim());
                result.setCountry(countryField.getText().trim());
                result.setPostalCode(postalCodeField.getText().trim());
                result.setStatus(statusCombo.getValue());
                return result;
            }
            return null;
        });

        Optional<Supplier> result = dialog.showAndWait();
        result.ifPresent(this::saveSupplier);
    }

    private void saveSupplier(Supplier supplier) {
        try {
            boolean success;
            if (supplier.getId() > 0) {
                success = supplierDAO.updateSupplier(supplier);
            } else {
                success = supplierDAO.addSupplier(supplier);
            }

            if (success) {
                showAlert("Success", supplier.getId() > 0 ? "Supplier updated successfully!" : "Supplier added successfully!");
                loadSuppliers();
            } else {
                showAlert("Error", supplier.getId() > 0 ? "Failed to update supplier." : "Failed to add supplier.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private void deleteSupplier(Supplier supplier) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Supplier");
        alert.setContentText("Are you sure you want to delete the supplier '" + supplier.getName() + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = supplierDAO.deleteSupplier(supplier.getId());
                if (success) {
                    showAlert("Success", "Supplier deleted successfully!");
                    loadSuppliers();
                } else {
                    showAlert("Error", "Failed to delete supplier.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred while deleting supplier: " + e.getMessage());
            }
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
