package org.example.newchronopos.controller.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.newchronopos.util.ModularLayoutManager;

import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    @FXML private Button backButton;
    @FXML private Label titleLabel;
    @FXML private Button notificationButton;
    @FXML private Button userButton;

    private String backNavigationPath;
    private String currentTitle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupButtonActions();
    }

    private void setupButtonActions() {
        backButton.setOnAction(event -> handleBackNavigation());
        notificationButton.setOnAction(event -> handleNotifications());
        userButton.setOnAction(event -> handleUserMenu());
    }

    public void setNavigationInfo(String title, String backPath) {
        this.currentTitle = title;
        this.backNavigationPath = backPath;

        titleLabel.setText(title);
        if (backPath != null && !backPath.isEmpty()) {
            backButton.setText("← " + getBackButtonText(backPath));
            backButton.setVisible(true);
        } else {
            backButton.setVisible(false);
        }
    }

    private String getBackButtonText(String path) {
        if (path.contains("BackOffice")) return "Back Office";
        if (path.contains("ProductManagement")) return "Product Management";
        if (path.contains("StockManagement")) return "Stock Management";
        return "Back";
    }

    private void handleBackNavigation() {
        if (backNavigationPath != null && !backNavigationPath.isEmpty()) {
            // Use ModularLayoutManager for seamless navigation
            if (backNavigationPath.contains("BackOffice")) {
                ModularLayoutManager.navigateToBackOffice();
            } else if (backNavigationPath.contains("ProductManagement")) {
                ModularLayoutManager.navigateToProductManagement();
            } else if (backNavigationPath.contains("StockManagement")) {
                ModularLayoutManager.navigateToStockManagement();
            } else {
                ModularLayoutManager.navigateToDashboard();
            }
        }
    }

    private void handleNotifications() {
        // TODO: Implement notification functionality
        System.out.println("Notifications clicked");
    }

    private void handleUserMenu() {
        // TODO: Implement user menu functionality
        System.out.println("User menu clicked");
    }
}
