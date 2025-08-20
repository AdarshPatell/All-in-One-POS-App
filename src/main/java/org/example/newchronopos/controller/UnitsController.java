package org.example.newchronopos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.newchronopos.dao.UnitDAO;
import org.example.newchronopos.model.Unit;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class UnitsController {

    @FXML private Button addUnitButton;
    @FXML private TextField searchField;
    @FXML private TableView<Unit> unitsTable;
    @FXML private TableColumn<Unit, String> unitIdColumn;
    @FXML private TableColumn<Unit, String> unitNameColumn;
    @FXML private TableColumn<Unit, String> abbreviationColumn;
    @FXML private TableColumn<Unit, String> descriptionColumn;
    @FXML private TableColumn<Unit, String> statusColumn;
    @FXML private TableColumn<Unit, String> createdDateColumn;
    @FXML private TableColumn<Unit, Void> actionsColumn;

    private ObservableList<Unit> units = FXCollections.observableArrayList();
    private UnitDAO unitDAO = new UnitDAO();

    @FXML
    public void initialize() {
        setupTable();
        setupButtons();
        loadUnits();
    }

    private void setupTable() {
        if (unitsTable != null) {
            unitsTable.setItems(units);
        }

        if (unitIdColumn != null) {
            unitIdColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        }

        if (unitNameColumn != null) {
            unitNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        }

        if (abbreviationColumn != null) {
            abbreviationColumn.setCellValueFactory(new PropertyValueFactory<>("abbreviation"));
        }

        if (descriptionColumn != null) {
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
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
            actionsColumn.setCellFactory(col -> new TableCell<Unit, Void>() {
                private final Button editButton = new Button("Edit");
                private final Button deleteButton = new Button("Delete");
                private final HBox buttons = new HBox(5, editButton, deleteButton);

                {
                    editButton.getStyleClass().add("btn-primary");
                    deleteButton.getStyleClass().add("btn-danger");

                    editButton.setOnAction(e -> {
                        Unit unit = getTableView().getItems().get(getIndex());
                        editUnit(unit);
                    });

                    deleteButton.setOnAction(e -> {
                        Unit unit = getTableView().getItems().get(getIndex());
                        deleteUnit(unit);
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
        if (addUnitButton != null) {
            addUnitButton.setOnAction(e -> openAddUnitDialog());
        }

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                searchUnits(newValue);
            });
        }
    }

    private void loadUnits() {
        try {
            List<Unit> unitList = unitDAO.getAllUnits();
            units.clear();
            units.addAll(unitList);
        } catch (Exception e) {
            showAlert("Error", "Failed to load units: " + e.getMessage());
        }
    }

    private void searchUnits(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                loadUnits();
            } else {
                List<Unit> searchResults = unitDAO.searchUnits(searchTerm.trim());
                units.clear();
                units.addAll(searchResults);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to search units: " + e.getMessage());
        }
    }

    private void openAddUnitDialog() {
        showUnitDialog(null, "Add New Unit of Measurement");
    }

    private void editUnit(Unit unit) {
        showUnitDialog(unit, "Edit Unit of Measurement");
    }

    private void showUnitDialog(Unit unit, String title) {
        Dialog<Unit> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        // Create form fields
        TextField nameField = new TextField();
        nameField.setPromptText("Unit Name (e.g., Kilogram, Meter)");

        TextField abbreviationField = new TextField();
        abbreviationField.setPromptText("Abbreviation (e.g., kg, m)");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Active", "Inactive");
        statusCombo.setValue("Active");

        // Populate fields if editing
        if (unit != null) {
            nameField.setText(unit.getName());
            abbreviationField.setText(unit.getAbbreviation());
            descriptionField.setText(unit.getDescription());
            statusCombo.setValue(unit.getStatus());
        }

        // Create layout
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.getChildren().addAll(
            new Label("Unit Name:"), nameField,
            new Label("Abbreviation:"), abbreviationField,
            new Label("Description:"), descriptionField,
            new Label("Status:"), statusCombo
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Enable/disable OK button based on input
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        // Validate both name and abbreviation
        Runnable validateInput = () -> {
            boolean valid = !nameField.getText().trim().isEmpty() &&
                           !abbreviationField.getText().trim().isEmpty();
            okButton.setDisable(!valid);
        };

        nameField.textProperty().addListener((observable, oldValue, newValue) -> validateInput.run());
        abbreviationField.textProperty().addListener((observable, oldValue, newValue) -> validateInput.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Unit result = unit != null ? unit : new Unit();
                result.setName(nameField.getText().trim());
                result.setAbbreviation(abbreviationField.getText().trim());
                result.setDescription(descriptionField.getText().trim());
                result.setStatus(statusCombo.getValue());
                return result;
            }
            return null;
        });

        Optional<Unit> result = dialog.showAndWait();
        result.ifPresent(this::saveUnit);
    }

    private void saveUnit(Unit unit) {
        try {
            boolean success;
            if (unit.getId() > 0) {
                success = unitDAO.updateUnit(unit);
            } else {
                success = unitDAO.addUnit(unit);
            }

            if (success) {
                showAlert("Success", unit.getId() > 0 ? "Unit updated successfully!" : "Unit added successfully!");
                loadUnits();
            } else {
                showAlert("Error", unit.getId() > 0 ? "Failed to update unit." : "Failed to add unit.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private void deleteUnit(Unit unit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Unit of Measurement");
        alert.setContentText("Are you sure you want to delete the unit '" + unit.getName() + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = unitDAO.deleteUnit(unit.getId());
                if (success) {
                    showAlert("Success", "Unit deleted successfully!");
                    loadUnits();
                } else {
                    showAlert("Error", "Failed to delete unit.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred while deleting unit: " + e.getMessage());
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
