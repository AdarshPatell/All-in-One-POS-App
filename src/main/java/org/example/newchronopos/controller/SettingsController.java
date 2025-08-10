package org.example.newchronopos.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import org.example.newchronopos.model.settings.SystemSettings;
import org.example.newchronopos.model.settings.CompanySettings;
import org.example.newchronopos.model.settings.UserSettings;
import org.example.newchronopos.service.settings.SettingsService;
import org.example.newchronopos.service.settings.SettingsServiceImpl;
import org.example.newchronopos.service.settings.SettingsValidator;

import java.io.*;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Settings Controller - Manages the settings UI and interactions
 */
public class SettingsController implements Initializable {

    // FXML Components
    @FXML private ListView<String> categoriesListView;
    @FXML private TextField searchField;
    @FXML private TabPane settingsTabPane;
    @FXML private Tab systemTab, companyTab, userTab;
    @FXML private VBox systemSettingsContainer, companySettingsContainer, userSettingsContainer;
    @FXML private ComboBox<String> companyComboBox, userComboBox;
    @FXML private Button saveAllBtn, resetBtn, importBtn, exportBtn;
    @FXML private Button addSettingBtn, refreshBtn, addCompanyBtn, addUserBtn;
    @FXML private Label statusLabel, lastModifiedLabel;

    // Services
    private SettingsService settingsService;
    
    // Data
    private ObservableList<String> allCategories;
    private List<SettingUIComponent> currentSettingComponents;
    private String selectedCategory = "GENERAL";
    private Long selectedCompanyId = 1L; // Default company
    private Long selectedUserId = 1L; // Default user

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settingsService = new SettingsServiceImpl();
        allCategories = FXCollections.observableArrayList();
        currentSettingComponents = new ArrayList<>();
        
        setupUI();
        loadCategories();
        loadCompaniesAndUsers();
        loadSettings();
        
        updateStatus("Settings loaded successfully");
    }

    private void setupUI() {
        // Setup categories list
        categoriesListView.setItems(allCategories);
        categoriesListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    selectedCategory = newVal;
                    loadSettings();
                }
            }
        );

        // Setup search field
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterSettings(newVal));

        // Set default selections
        settingsTabPane.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldTab, newTab) -> loadSettings()
        );
    }

    private void loadCategories() {
        CompletableFuture.runAsync(() -> {
            try {
                Set<String> categories = new HashSet<>();
                categories.addAll(settingsService.getSystemCategories());
                if (selectedCompanyId != null) {
                    categories.addAll(settingsService.getCompanyCategories(selectedCompanyId));
                }
                if (selectedUserId != null) {
                    categories.addAll(settingsService.getUserCategories(selectedUserId));
                }
                
                // Add default categories if empty
                if (categories.isEmpty()) {
                    categories.addAll(Arrays.asList("GENERAL", "LOCALIZATION", "SECURITY", "BACKUP", "UI", "FINANCIAL"));
                }

                Platform.runLater(() -> {
                    allCategories.clear();
                    allCategories.addAll(categories.stream().sorted().collect(Collectors.toList()));
                    if (!allCategories.isEmpty()) {
                        categoriesListView.getSelectionModel().selectFirst();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Failed to load categories", e));
            }
        });
    }

    private void loadCompaniesAndUsers() {
        // TODO: Load from actual company and user services
        // For now, use dummy data
        companyComboBox.setItems(FXCollections.observableArrayList("Default Company", "Company 2"));
        companyComboBox.setValue("Default Company");
        
        userComboBox.setItems(FXCollections.observableArrayList("Admin User", "Employee User"));
        userComboBox.setValue("Admin User");
    }

    private void loadSettings() {
        Tab selectedTab = settingsTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) return;

        CompletableFuture.runAsync(() -> {
            try {
                if (selectedTab == systemTab) {
                    loadSystemSettings();
                } else if (selectedTab == companyTab) {
                    loadCompanySettings();
                } else if (selectedTab == userTab) {
                    loadUserSettings();
                }
            } catch (Exception e) {
                Platform.runLater(() -> showError("Failed to load settings", e));
            }
        });
    }

    private void loadSystemSettings() {
        List<SystemSettings> settings = settingsService.getSystemSettingsByCategory(selectedCategory);
        
        Platform.runLater(() -> {
            systemSettingsContainer.getChildren().clear();
            currentSettingComponents.clear();
            
            if (settings.isEmpty()) {
                systemSettingsContainer.getChildren().add(createEmptyStateComponent("No system settings found for category: " + selectedCategory));
                return;
            }
            
            for (SystemSettings setting : settings) {
                SettingUIComponent component = createSettingComponent(setting);
                systemSettingsContainer.getChildren().add(component.getNode());
                currentSettingComponents.add(component);
            }
        });
    }

    private void loadCompanySettings() {
        if (selectedCompanyId == null) return;
        
        List<CompanySettings> settings = settingsService.getCompanySettingsByCategory(selectedCompanyId, selectedCategory);
        
        Platform.runLater(() -> {
            companySettingsContainer.getChildren().clear();
            currentSettingComponents.clear();
            
            if (settings.isEmpty()) {
                companySettingsContainer.getChildren().add(createEmptyStateComponent("No company settings found for category: " + selectedCategory));
                return;
            }
            
            for (CompanySettings setting : settings) {
                SettingUIComponent component = createSettingComponent(setting);
                companySettingsContainer.getChildren().add(component.getNode());
                currentSettingComponents.add(component);
            }
        });
    }

    private void loadUserSettings() {
        if (selectedUserId == null) return;
        
        List<UserSettings> settings = settingsService.getUserSettingsByCategory(selectedUserId, selectedCategory);
        
        Platform.runLater(() -> {
            userSettingsContainer.getChildren().clear();
            currentSettingComponents.clear();
            
            if (settings.isEmpty()) {
                userSettingsContainer.getChildren().add(createEmptyStateComponent("No user settings found for category: " + selectedCategory));
                return;
            }
            
            for (UserSettings setting : settings) {
                SettingUIComponent component = createSettingComponent(setting);
                userSettingsContainer.getChildren().add(component.getNode());
                currentSettingComponents.add(component);
            }
        });
    }

    private VBox createEmptyStateComponent(String message) {
        VBox emptyState = new VBox(15);
        emptyState.setAlignment(javafx.geometry.Pos.CENTER);
        emptyState.setPadding(new Insets(40));
        emptyState.setStyle("-fx-background-color: #2d2d2d; -fx-background-radius: 8px;");
        
        Label iconLabel = new Label("⚙️");
        iconLabel.setStyle("-fx-font-size: 48px;");
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 16px; -fx-wrap-text: true; -fx-text-alignment: center;");
        messageLabel.setWrapText(true);
        
        Label hintLabel = new Label("Add new settings using the 'Add Setting' button or check other categories.");
        hintLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px; -fx-wrap-text: true; -fx-text-alignment: center;");
        hintLabel.setWrapText(true);
        
        emptyState.getChildren().addAll(iconLabel, messageLabel, hintLabel);
        return emptyState;
    }

    private SettingUIComponent createSettingComponent(Object setting) {
        String key, value, description, dataType;
        
        if (setting instanceof SystemSettings) {
            SystemSettings s = (SystemSettings) setting;
            key = s.getSettingKey();
            value = s.getSettingValue();
            description = s.getDescription();
            dataType = s.getDataType();
        } else if (setting instanceof CompanySettings) {
            CompanySettings s = (CompanySettings) setting;
            key = s.getSettingKey();
            value = s.getSettingValue();
            description = s.getDescription();
            dataType = s.getDataType();
        } else if (setting instanceof UserSettings) {
            UserSettings s = (UserSettings) setting;
            key = s.getSettingKey();
            value = s.getSettingValue();
            description = s.getDescription();
            dataType = s.getDataType();
        } else {
            throw new IllegalArgumentException("Unknown setting type");
        }

        return new SettingUIComponent(key, value, description, dataType);
    }

    // Event Handlers
    @FXML
    private void onCategorySelected(MouseEvent event) {
        String selected = categoriesListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedCategory = selected;
            loadSettings();
        }
    }

    @FXML
    private void onSearchKeyReleased(KeyEvent event) {
        String searchText = searchField.getText();
        filterSettings(searchText);
    }

    @FXML
    private void onCompanySelected() {
        String selected = companyComboBox.getValue();
        if (selected != null) {
            // TODO: Map company name to ID
            selectedCompanyId = 1L; // Default for now
            loadSettings();
        }
    }

    @FXML
    private void onUserSelected() {
        String selected = userComboBox.getValue();
        if (selected != null) {
            // TODO: Map user name to ID
            selectedUserId = 1L; // Default for now
            loadSettings();
        }
    }

    @FXML
    private void saveAllSettings() {
        CompletableFuture.runAsync(() -> {
            try {
                Tab selectedTab = settingsTabPane.getSelectionModel().getSelectedItem();
                int savedCount = 0;
                
                for (SettingUIComponent component : currentSettingComponents) {
                    if (component.hasChanges()) {
                        String newValue = component.getValue();
                        
                        // Validate the new value
                        SettingsValidator.ValidationResult validation = 
                            SettingsValidator.validateSetting(component.getKey(), newValue, component.getDataType());
                        
                        if (!validation.isValid()) {
                            Platform.runLater(() -> showValidationErrors(component.getKey(), validation.getErrors()));
                            continue;
                        }
                        
                        // Save the setting
                        if (selectedTab == systemTab) {
                            settingsService.setSystemSetting(component.getKey(), newValue, selectedCategory);
                        } else if (selectedTab == companyTab && selectedCompanyId != null) {
                            settingsService.setCompanySetting(selectedCompanyId, component.getKey(), newValue, selectedCategory);
                        } else if (selectedTab == userTab && selectedUserId != null) {
                            settingsService.setUserSetting(selectedUserId, component.getKey(), newValue, selectedCategory);
                        }
                        
                        component.markAsSaved();
                        savedCount++;
                    }
                }
                
                final int finalSavedCount = savedCount;
                Platform.runLater(() -> {
                    updateStatus(finalSavedCount + " settings saved successfully");
                    updateLastModified();
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> showError("Failed to save settings", e));
            }
        });
    }

    @FXML
    private void resetToDefaults() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Settings");
        alert.setHeaderText("Reset to Default Values");
        alert.setContentText("Are you sure you want to reset all settings to their default values? This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                CompletableFuture.runAsync(() -> {
                    try {
                        Tab selectedTab = settingsTabPane.getSelectionModel().getSelectedItem();
                        
                        if (selectedTab == systemTab) {
                            settingsService.resetSystemSettingsToDefault();
                        } else if (selectedTab == companyTab && selectedCompanyId != null) {
                            settingsService.resetCompanySettingsToDefault(selectedCompanyId);
                        } else if (selectedTab == userTab && selectedUserId != null) {
                            settingsService.resetUserSettingsToDefault(selectedUserId);
                        }
                        
                        Platform.runLater(() -> {
                            loadSettings();
                            updateStatus("Settings reset to defaults");
                        });
                        
                    } catch (Exception e) {
                        Platform.runLater(() -> showError("Failed to reset settings", e));
                    }
                });
            }
        });
    }

    @FXML
    private void importSettings() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Settings");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Properties Files", "*.properties"));
        
        Stage stage = (Stage) importBtn.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    Properties props = new Properties();
                    try (FileInputStream fis = new FileInputStream(file)) {
                        props.load(fis);
                    }
                    
                    Map<String, String> settings = new HashMap<>();
                    for (String key : props.stringPropertyNames()) {
                        settings.put(key, props.getProperty(key));
                    }
                    
                    Tab selectedTab = settingsTabPane.getSelectionModel().getSelectedItem();
                    if (selectedTab == systemTab) {
                        settingsService.importSystemSettings(settings);
                    } else if (selectedTab == companyTab && selectedCompanyId != null) {
                        settingsService.importCompanySettings(selectedCompanyId, settings);
                    } else if (selectedTab == userTab && selectedUserId != null) {
                        settingsService.importUserSettings(selectedUserId, settings);
                    }
                    
                    Platform.runLater(() -> {
                        loadSettings();
                        updateStatus("Settings imported from " + file.getName());
                    });
                    
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Failed to import settings", e));
                }
            });
        }
    }

    @FXML
    private void exportSettings() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Settings");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Properties Files", "*.properties"));
        fileChooser.setInitialFileName("chronopos_settings.properties");
        
        Stage stage = (Stage) exportBtn.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    Map<String, String> settings;
                    Tab selectedTab = settingsTabPane.getSelectionModel().getSelectedItem();
                    
                    if (selectedTab == systemTab) {
                        settings = settingsService.exportSystemSettings();
                    } else if (selectedTab == companyTab && selectedCompanyId != null) {
                        settings = settingsService.exportCompanySettings(selectedCompanyId);
                    } else if (selectedTab == userTab && selectedUserId != null) {
                        settings = settingsService.exportUserSettings(selectedUserId);
                    } else {
                        settings = new HashMap<>();
                    }
                    
                    Properties props = new Properties();
                    settings.forEach(props::setProperty);
                    
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        props.store(fos, "ChronoPOS Settings Export - " + new Date());
                    }
                    
                    Platform.runLater(() -> updateStatus("Settings exported to " + file.getName()));
                    
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Failed to export settings", e));
                }
            });
        }
    }

    @FXML
    private void addNewSetting() {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add New Setting");
        dialog.setHeaderText("Create a new setting");

        // Create the dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField keyField = new TextField();
        keyField.setPromptText("Setting key (e.g., app.feature_name)");
        TextField valueField = new TextField();
        valueField.setPromptText("Setting value");
        TextField descField = new TextField();
        descField.setPromptText("Description (optional)");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("STRING", "INTEGER", "DECIMAL", "BOOLEAN", "EMAIL", "URL");
        typeCombo.setValue("STRING");
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("GENERAL", "SECURITY", "BACKUP", "LOCALIZATION", "UI", "FINANCIAL", "PRINTING", "INVENTORY", "WORKFLOW", "NOTIFICATIONS");
        categoryCombo.setValue("GENERAL");

        grid.add(new Label("Key:"), 0, 0);
        grid.add(keyField, 1, 0);
        grid.add(new Label("Value:"), 0, 1);
        grid.add(valueField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descField, 1, 2);
        grid.add(new Label("Type:"), 0, 3);
        grid.add(typeCombo, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Disable create button unless key and value are provided
        javafx.scene.Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);
        keyField.textProperty().addListener((obs, oldVal, newVal) -> 
            createButton.setDisable(newVal.trim().isEmpty() || valueField.getText().trim().isEmpty()));
        valueField.textProperty().addListener((obs, oldVal, newVal) -> 
            createButton.setDisable(newVal.trim().isEmpty() || keyField.getText().trim().isEmpty()));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                Map<String, String> result = new HashMap<>();
                result.put("key", keyField.getText());
                result.put("value", valueField.getText());
                result.put("description", descField.getText());
                result.put("type", typeCombo.getValue());
                result.put("category", categoryCombo.getValue());
                return result;
            }
            return null;
        });

        Optional<Map<String, String>> result = dialog.showAndWait();
        result.ifPresent(data -> {
            try {
                Tab selectedTab = settingsTabPane.getSelectionModel().getSelectedItem();
                String key = data.get("key");
                String value = data.get("value");
                String description = data.get("description");
                String type = data.get("type");
                String category = data.get("category");

                if (selectedTab == systemTab) {
                    settingsService.setSystemSetting(key, value, category, description, type);
                } else if (selectedTab == companyTab && selectedCompanyId != null) {
                    settingsService.setCompanySetting(selectedCompanyId, key, value, category, description, type);
                } else if (selectedTab == userTab && selectedUserId != null) {
                    settingsService.setUserSetting(selectedUserId, key, value, category, description, type);
                }

                loadSettings();
                updateStatus("New setting '" + key + "' created successfully");
            } catch (Exception e) {
                showError("Failed to create setting", e);
            }
        });
    }

    @FXML
    private void addNewCompany() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Company");
        dialog.setHeaderText("Enter company name:");
        dialog.setContentText("Company Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                // TODO: Integrate with actual company service when available
                // For now, just add to combo box
                companyComboBox.getItems().add(name);
                companyComboBox.setValue(name);
                updateStatus("Company '" + name + "' added (Note: This is temporary until company service is integrated)");
            }
        });
    }

    @FXML
    private void addNewUser() {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Create a new user");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Full name");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("admin", "manager", "employee", "cashier");
        roleCombo.setValue("employee");

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Role:"), 0, 2);
        grid.add(roleCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        javafx.scene.Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);
        nameField.textProperty().addListener((obs, oldVal, newVal) -> 
            createButton.setDisable(newVal.trim().isEmpty() || usernameField.getText().trim().isEmpty()));
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> 
            createButton.setDisable(newVal.trim().isEmpty() || nameField.getText().trim().isEmpty()));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                Map<String, String> result = new HashMap<>();
                result.put("name", nameField.getText());
                result.put("username", usernameField.getText());
                result.put("role", roleCombo.getValue());
                return result;
            }
            return null;
        });

        Optional<Map<String, String>> result = dialog.showAndWait();
        result.ifPresent(data -> {
            // TODO: Integrate with actual user service when available
            String name = data.get("name");
            userComboBox.getItems().add(name);
            userComboBox.setValue(name);
            updateStatus("User '" + name + "' added (Note: This is temporary until user service is integrated)");
        });
    }

    @FXML
    private void refreshSettings() {
        CompletableFuture.runAsync(() -> {
            Platform.runLater(() -> updateStatus("Refreshing settings..."));
            
            settingsService.clearCache();
            
            Platform.runLater(() -> {
                loadCategories();
                loadCompaniesAndUsers();
                loadSettings();
                updateStatus("Settings refreshed successfully");
            });
        });
    }

    // Helper Methods
    private void filterSettings(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            // Show all settings
            for (SettingUIComponent component : currentSettingComponents) {
                component.getNode().setVisible(true);
                component.getNode().setManaged(true);
            }
            return;
        }

        String lowerSearchText = searchText.toLowerCase();
        int visibleCount = 0;
        
        for (SettingUIComponent component : currentSettingComponents) {
            boolean matches = component.getKey().toLowerCase().contains(lowerSearchText) ||
                            (component.getDescription() != null && 
                             component.getDescription().toLowerCase().contains(lowerSearchText)) ||
                            component.getValue().toLowerCase().contains(lowerSearchText);
            
            component.getNode().setVisible(matches);
            component.getNode().setManaged(matches);
            
            if (matches) visibleCount++;
        }
        
        updateStatus("Showing " + visibleCount + " of " + currentSettingComponents.size() + " settings");
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
        
        // Auto-clear status after 5 seconds
        Timeline timeline = new Timeline(new KeyFrame(
            javafx.util.Duration.seconds(5),
            e -> statusLabel.setText("Ready")
        ));
        timeline.play();
    }

    private void updateLastModified() {
        lastModifiedLabel.setText("Last modified: " + 
            java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }

    private void showError(String title, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    private void showValidationErrors(String settingKey, List<String> errors) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Invalid value for setting: " + settingKey);
        alert.setContentText(String.join("\n", errors));
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for setting UI components
    private static class SettingUIComponent {
        private VBox container;
        private String key;
        private String dataType;
        private String description;
        private Control valueControl;
        private String originalValue;
        private boolean hasChanges = false;

        public SettingUIComponent(String key, String value, String description, String dataType) {
            this.key = key;
            this.dataType = dataType != null ? dataType : "STRING";
            this.description = description;
            this.originalValue = value != null ? value : "";
            
            createUI(value);
        }

        private void createUI(String value) {
            container = new VBox(8);
            container.setPadding(new Insets(15));
            container.setStyle("-fx-border-color: #444; -fx-border-radius: 8px; -fx-background-color: #3d3d3d; -fx-background-radius: 8px;");

            // Setting header with key name
            HBox headerBox = new HBox(10);
            headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            Label titleLabel = new Label(formatSettingKey(key));
            titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
            headerBox.getChildren().add(titleLabel);
            
            // Data type badge
            Label typeLabel = new Label(dataType);
            typeLabel.setStyle("-fx-background-color: #555; -fx-text-fill: #ccc; -fx-padding: 2 8; -fx-background-radius: 10; -fx-font-size: 10px;");
            headerBox.getChildren().add(typeLabel);
            
            // Add spacer and status indicator
            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            headerBox.getChildren().add(spacer);
            
            container.getChildren().add(headerBox);

            // Description
            if (description != null && !description.trim().isEmpty()) {
                Label descLabel = new Label(description);
                descLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 12px; -fx-wrap-text: true;");
                descLabel.setWrapText(true);
                container.getChildren().add(descLabel);
            }

            // Value input control
            HBox inputContainer = new HBox(10);
            inputContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            createValueControl(value);
            inputContainer.getChildren().add(valueControl);
            
            // Reset button for each setting
            Button resetBtn = new Button("↻");
            resetBtn.setStyle("-fx-background-color: #666; -fx-text-fill: white; -fx-padding: 5; -fx-min-width: 30;");
            resetBtn.setOnAction(e -> resetToOriginal());
            inputContainer.getChildren().add(resetBtn);

            container.getChildren().add(inputContainer);
        }

        private void createValueControl(String value) {
            switch (dataType.toUpperCase()) {
                case "BOOLEAN":
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(value != null && ("true".equalsIgnoreCase(value) || "1".equals(value)));
                    checkBox.setStyle("-fx-text-fill: white;");
                    checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> markAsChanged());
                    valueControl = checkBox;
                    break;
                
                case "INTEGER":
                    Spinner<Integer> intSpinner = new Spinner<>();
                    intSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        Integer.MIN_VALUE, Integer.MAX_VALUE, 
                        value != null && !value.isEmpty() ? Integer.parseInt(value) : 0, 1));
                    intSpinner.setEditable(true);
                    intSpinner.setPrefWidth(150);
                    intSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
                    intSpinner.valueProperty().addListener((obs, oldVal, newVal) -> markAsChanged());
                    valueControl = intSpinner;
                    break;
                
                case "DECIMAL":
                    Spinner<Double> doubleSpinner = new Spinner<>();
                    doubleSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                        Double.MIN_VALUE, Double.MAX_VALUE, 
                        value != null && !value.isEmpty() ? Double.parseDouble(value) : 0.0, 0.1));
                    doubleSpinner.setEditable(true);
                    doubleSpinner.setPrefWidth(150);
                    doubleSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
                    doubleSpinner.valueProperty().addListener((obs, oldVal, newVal) -> markAsChanged());
                    valueControl = doubleSpinner;
                    break;
                
                case "EMAIL":
                case "URL":
                case "STRING":
                default:
                    TextField textField = new TextField(value != null ? value : "");
                    textField.setPrefWidth(300);
                    textField.setStyle("-fx-background-color: #555; -fx-text-fill: white; -fx-prompt-text-fill: #999;");
                    
                    // Add validation styling for email and URL types
                    if ("EMAIL".equals(dataType)) {
                        textField.setPromptText("example@domain.com");
                        textField.textProperty().addListener((obs, oldVal, newVal) -> {
                            markAsChanged();
                            validateEmail(textField, newVal);
                        });
                    } else if ("URL".equals(dataType)) {
                        textField.setPromptText("https://example.com");
                        textField.textProperty().addListener((obs, oldVal, newVal) -> {
                            markAsChanged();
                            validateUrl(textField, newVal);
                        });
                    } else {
                        textField.textProperty().addListener((obs, oldVal, newVal) -> markAsChanged());
                    }
                    
                    valueControl = textField;
                    break;
            }
        }

        private void validateEmail(TextField field, String email) {
            boolean isValid = email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
            field.setStyle(isValid || email.isEmpty() ? 
                "-fx-background-color: #555; -fx-text-fill: white;" : 
                "-fx-background-color: #664444; -fx-text-fill: white; -fx-border-color: #ff6666;");
        }

        private void validateUrl(TextField field, String url) {
            boolean isValid = url.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");
            field.setStyle(isValid || url.isEmpty() ? 
                "-fx-background-color: #555; -fx-text-fill: white;" : 
                "-fx-background-color: #664444; -fx-text-fill: white; -fx-border-color: #ff6666;");
        }

        private void markAsChanged() {
            hasChanges = true;
            // Add visual indicator for changes
            container.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 8px; -fx-background-color: #3d3d3d; -fx-background-radius: 8px;");
        }

        private void resetToOriginal() {
            setValue(originalValue);
            hasChanges = false;
            container.setStyle("-fx-border-color: #444; -fx-border-radius: 8px; -fx-background-color: #3d3d3d; -fx-background-radius: 8px;");
        }

        private void setValue(String value) {
            if (valueControl instanceof TextField) {
                ((TextField) valueControl).setText(value != null ? value : "");
            } else if (valueControl instanceof CheckBox) {
                ((CheckBox) valueControl).setSelected("true".equalsIgnoreCase(value) || "1".equals(value));
            } else if (valueControl instanceof Spinner) {
                try {
                    if (dataType.equals("INTEGER")) {
                        ((Spinner<Integer>) valueControl).getValueFactory().setValue(
                            value != null && !value.isEmpty() ? Integer.parseInt(value) : 0);
                    } else {
                        ((Spinner<Double>) valueControl).getValueFactory().setValue(
                            value != null && !value.isEmpty() ? Double.parseDouble(value) : 0.0);
                    }
                } catch (NumberFormatException e) {
                    // Handle invalid number format
                }
            }
        }

        private String formatSettingKey(String key) {
            // Convert "app.name" to "App Name", "security.session_timeout" to "Session Timeout", etc.
            String[] parts = key.split("\\.");
            String lastPart = parts[parts.length - 1];
            StringBuilder formatted = new StringBuilder();
            String[] words = lastPart.replace("_", " ").replace("-", " ").split(" ");
            
            for (int i = 0; i < words.length; i++) {
                if (i > 0) formatted.append(" ");
                if (words[i].length() > 0) {
                    formatted.append(Character.toUpperCase(words[i].charAt(0)));
                    if (words[i].length() > 1) {
                        formatted.append(words[i].substring(1).toLowerCase());
                    }
                }
            }
            
            return formatted.toString();
        }

        // Getters
        public VBox getNode() { return container; }
        public String getKey() { return key; }
        public String getDataType() { return dataType; }
        public String getDescription() { return description; }
        public boolean hasChanges() { return hasChanges; }

        public String getValue() {
            if (valueControl instanceof TextField) {
                return ((TextField) valueControl).getText();
            } else if (valueControl instanceof CheckBox) {
                return String.valueOf(((CheckBox) valueControl).isSelected());
            } else if (valueControl instanceof Spinner) {
                return String.valueOf(((Spinner<?>) valueControl).getValue());
            }
            return originalValue;
        }

        public void markAsSaved() {
            hasChanges = false;
            originalValue = getValue();
            // Remove change indicator
            container.setStyle("-fx-border-color: #444; -fx-border-radius: 8px; -fx-background-color: #3d3d3d; -fx-background-radius: 8px;");
        }
    }

    // Navigation Methods
    @FXML
    private void navigateToDashboard() {
        showInfo("Navigation", "Dashboard feature coming soon!");
    }

    @FXML
    private void navigateToTransactions() {
        showInfo("Navigation", "Transactions feature coming soon!");
    }

    @FXML
    private void navigateToProducts() {
        try {
            Stage stage = (Stage) categoriesListView.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Products.fxml"));
            Scene scene = new Scene(loader.load());
            
            boolean wasMaximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            stage.setScene(scene);
            
            if (wasMaximized) {
                stage.setMaximized(true);
            } else {
                stage.setWidth(width);
                stage.setHeight(height);
            }
            
        } catch (Exception e) {
            showError("Navigation Error", e);
        }
    }

    @FXML
    private void navigateToReports() {
        try {
            Stage stage = (Stage) categoriesListView.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/reports.fxml"));
            Scene scene = new Scene(loader.load());
            
            boolean wasMaximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            stage.setScene(scene);
            
            if (wasMaximized) {
                stage.setMaximized(true);
            } else {
                stage.setWidth(width);
                stage.setHeight(height);
            }
            
        } catch (Exception e) {
            showError("Navigation Error", e);
        }
    }

    @FXML
    private void navigateToSettings() {
        showInfo("Navigation", "You are already on the Settings page!");
    }

    @FXML
    private void handleLogout() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout");
            alert.setHeaderText("Are you sure you want to logout?");
            alert.setContentText("You will be redirected to the login screen.");
            
            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                Stage stage = (Stage) categoriesListView.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
                Scene scene = new Scene(loader.load());
                
                var css = getClass().getResource("/css/style.css");
                if (css != null) scene.getStylesheets().add(css.toExternalForm());
                
                stage.setTitle("ChronoPos - Login");
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.setMaximized(false);
            }
        } catch (Exception e) {
            showError("Logout Error", e);
        }
    }
}
