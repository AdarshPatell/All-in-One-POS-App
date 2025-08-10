package org.example.newchronopos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Reports Controller - Manages the reports view and navigation
 */
public class ReportsController implements Initializable {

    @FXML private VBox logoutSection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize reports view
        System.out.println("Reports view initialized");
    }

    // Navigation Methods
    @FXML
    private void navigateToDashboard() {
        showAlert("Navigation", "Dashboard feature coming soon!");
    }

    @FXML
    private void navigateToTransactions() {
        showAlert("Navigation", "Transactions feature coming soon!");
    }

    @FXML
    private void navigateToProducts() {
        try {
            Stage stage = (Stage) logoutSection.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Products.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Copy window properties
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
            showAlert("Navigation Error", "Failed to load Products: " + e.getMessage());
        }
    }

    @FXML
    private void navigateToReports() {
        // Already on reports page
        showAlert("Navigation", "You are already on the Reports page!");
    }

    @FXML
    private void navigateToSettings() {
        try {
            Stage stage = (Stage) logoutSection.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/settings.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Copy window properties
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
            showAlert("Navigation Error", "Failed to load Settings: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout");
            alert.setHeaderText("Are you sure you want to logout?");
            alert.setContentText("You will be redirected to the login screen.");
            
            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                Stage stage = (Stage) logoutSection.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
                Scene scene = new Scene(loader.load());
                
                var css = getClass().getResource("/css/style.css");
                if (css != null) scene.getStylesheets().add(css.toExternalForm());
                
                stage.setTitle("ChronoPos - Login");
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.setMaximized(false); // Reset to windowed mode for login
            }
        } catch (Exception e) {
            showAlert("Logout Error", "Failed to logout: " + e.getMessage());
        }
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
