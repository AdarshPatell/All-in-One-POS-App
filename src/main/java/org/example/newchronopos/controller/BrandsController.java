package org.example.newchronopos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.newchronopos.dao.BrandDAO;
import org.example.newchronopos.model.Brand;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class BrandsController {

    @FXML private Button addBrandButton;
    @FXML private TextField searchField;
    @FXML private TableView<Brand> brandsTable;
    @FXML private TableColumn<Brand, String> brandIdColumn;
    @FXML private TableColumn<Brand, String> brandNameColumn;
    @FXML private TableColumn<Brand, String> descriptionColumn;
    @FXML private TableColumn<Brand, String> websiteColumn;
    @FXML private TableColumn<Brand, String> statusColumn;
    @FXML private TableColumn<Brand, String> createdDateColumn;
    @FXML private TableColumn<Brand, Void> actionsColumn;

    private ObservableList<Brand> brands = FXCollections.observableArrayList();
    private BrandDAO brandDAO = new BrandDAO();

    @FXML
    public void initialize() {
        setupTable();
        setupButtons();
        loadBrands();
    }

    private void setupTable() {
        if (brandsTable != null) {
            brandsTable.setItems(brands);
        }

        if (brandIdColumn != null) {
            brandIdColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        }

        if (brandNameColumn != null) {
            brandNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        }

        if (descriptionColumn != null) {
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        }

        if (websiteColumn != null) {
            websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));
        }

        if (statusColumn != null) {
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        }

        if (createdDateColumn != null) {
            createdDateColumn.setCellValueFactory(cellData -> {
                if (cellData.getValue().getCreatedAt() != null) {
                    return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    );
                }
                return new javafx.beans.property.SimpleStringProperty("");
            });
        }

        if (actionsColumn != null) {
            actionsColumn.setCellFactory(col -> new TableCell<Brand, Void>() {
                private final Button editButton = new Button("Edit");
                private final Button deleteButton = new Button("Delete");
                private final HBox buttons = new HBox(5, editButton, deleteButton);

                {
                    editButton.getStyleClass().add("btn-primary");
                    deleteButton.getStyleClass().add("btn-danger");

                    editButton.setOnAction(e -> {
                        Brand brand = getTableView().getItems().get(getIndex());
                        editBrand(brand);
                    });

                    deleteButton.setOnAction(e -> {
                        Brand brand = getTableView().getItems().get(getIndex());
                        deleteBrand(brand);
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
        if (addBrandButton != null) {
            addBrandButton.setOnAction(e -> openAddBrandDialog());
        }

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                searchBrands(newValue);
            });
        }
    }

    private void loadBrands() {
        try {
            List<Brand> brandList = brandDAO.getAllBrands();
            brands.clear();
            brands.addAll(brandList);
        } catch (Exception e) {
            showAlert("Error", "Failed to load brands: " + e.getMessage());
        }
    }

    private void searchBrands(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                loadBrands();
            } else {
                List<Brand> searchResults = brandDAO.searchBrands(searchTerm.trim());
                brands.clear();
                brands.addAll(searchResults);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to search brands: " + e.getMessage());
        }
    }

    private void openAddBrandDialog() {
        showBrandDialog(null, "Add New Brand");
    }

    private void editBrand(Brand brand) {
        showBrandDialog(brand, "Edit Brand");
    }

    private void showBrandDialog(Brand brand, String title) {
        Dialog<Brand> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        // Create form fields
        TextField nameField = new TextField();
        nameField.setPromptText("Brand Name");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        TextField websiteField = new TextField();
        websiteField.setPromptText("Website URL");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Active", "Inactive");
        statusCombo.setValue("Active");

        // Populate fields if editing
        if (brand != null) {
            nameField.setText(brand.getName());
            descriptionField.setText(brand.getDescription());
            websiteField.setText(brand.getWebsite());
            statusCombo.setValue(brand.getStatus());
        }

        // Create layout
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.getChildren().addAll(
            new Label("Brand Name:"), nameField,
            new Label("Description:"), descriptionField,
            new Label("Website:"), websiteField,
            new Label("Status:"), statusCombo
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Enable/disable OK button based on input
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Brand result = brand != null ? brand : new Brand();
                result.setName(nameField.getText().trim());
                result.setDescription(descriptionField.getText().trim());
                result.setWebsite(websiteField.getText().trim());
                result.setStatus(statusCombo.getValue());
                return result;
            }
            return null;
        });

        Optional<Brand> result = dialog.showAndWait();
        result.ifPresent(this::saveBrand);
    }

    private void saveBrand(Brand brand) {
        try {
            boolean success;
            if (brand.getId() > 0) {
                success = brandDAO.updateBrand(brand);
            } else {
                success = brandDAO.addBrand(brand);
            }

            if (success) {
                showAlert("Success", brand.getId() > 0 ? "Brand updated successfully!" : "Brand added successfully!");
                loadBrands();
            } else {
                showAlert("Error", brand.getId() > 0 ? "Failed to update brand." : "Failed to add brand.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private void deleteBrand(Brand brand) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Brand");
        alert.setContentText("Are you sure you want to delete the brand '" + brand.getName() + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = brandDAO.deleteBrand(brand.getId());
                if (success) {
                    showAlert("Success", "Brand deleted successfully!");
                    loadBrands();
                } else {
                    showAlert("Error", "Failed to delete brand.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred while deleting brand: " + e.getMessage());
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
