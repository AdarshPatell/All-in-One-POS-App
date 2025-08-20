package org.example.newchronopos.controller.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.example.newchronopos.util.ModularLayoutManager;

import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {

    @FXML private Button dashboardBtn;
    @FXML private Button transactionBtn;
    @FXML private Button backOfficeBtn;
    @FXML private Button productManagementBtn;
    @FXML private Button stockManagementBtn;
    @FXML private Button reportsBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;

    private String currentModule;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupNavigationActions();
    }

    private void setupNavigationActions() {
        dashboardBtn.setOnAction(event -> {
            ModularLayoutManager.navigateToDashboard();
            setActiveModule("Dashboard");
        });

        transactionBtn.setOnAction(event -> {
            // TODO: Navigate to Transaction module
            System.out.println("Transaction clicked");
            setActiveModule("Transaction");
        });

        backOfficeBtn.setOnAction(event -> {
            ModularLayoutManager.navigateToBackOffice();
            setActiveModule("BackOffice");
        });

        productManagementBtn.setOnAction(event -> {
            ModularLayoutManager.navigateToProductManagement();
            setActiveModule("ProductManagement");
        });

        stockManagementBtn.setOnAction(event -> {
            ModularLayoutManager.navigateToStockManagement();
            setActiveModule("StockManagement");
        });

        reportsBtn.setOnAction(event -> {
            ModularLayoutManager.navigateToReports();
            setActiveModule("Reports");
        });

        settingsBtn.setOnAction(event -> handleSettings());
        logoutBtn.setOnAction(event -> handleLogout());
    }

    public void setActiveModule(String moduleName) {
        // Reset all button styles to default
        resetButtonStyles();

        // Set active style for current module (matching BackOffice design)
        Button activeButton = getButtonByModule(moduleName);
        if (activeButton != null) {
            activeButton.setStyle("-fx-background-color: #F4B942; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10; -fx-alignment: center-left; -fx-pref-width: 180;");
        }

        this.currentModule = moduleName;
    }

    private void resetButtonStyles() {
        String defaultStyle = "-fx-background-color: #34495E; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10; -fx-alignment: center-left; -fx-pref-width: 180;";
        dashboardBtn.setStyle(defaultStyle);
        transactionBtn.setStyle(defaultStyle);
        backOfficeBtn.setStyle(defaultStyle);
        productManagementBtn.setStyle(defaultStyle);
        stockManagementBtn.setStyle(defaultStyle);
        reportsBtn.setStyle(defaultStyle);
        settingsBtn.setStyle(defaultStyle);
    }

    private Button getButtonByModule(String moduleName) {
        switch (moduleName) {
            case "Dashboard": return dashboardBtn;
            case "Transaction": return transactionBtn;
            case "BackOffice": return backOfficeBtn;
            case "ProductManagement": return productManagementBtn;
            case "StockManagement": return stockManagementBtn;
            case "Reports": return reportsBtn;
            case "Settings": return settingsBtn;
            default: return null;
        }
    }

    private void handleSettings() {
        // TODO: Implement settings functionality
        System.out.println("Settings clicked");
    }

    private void handleLogout() {
        try {
            // For logout, we need to load the login scene as a completely new scene
            // This replaces the entire window content, not just the modular content
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/views/login.fxml")
            );
            javafx.scene.Scene loginScene = new javafx.scene.Scene(loader.load());

            javafx.stage.Stage currentStage = (javafx.stage.Stage) logoutBtn.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.setTitle("ChronoPos - Login");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error during logout: " + e.getMessage());
        }
    }
}
