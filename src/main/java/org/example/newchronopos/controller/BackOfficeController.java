package org.example.newchronopos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import org.example.newchronopos.service.SessionService;
import org.example.newchronopos.util.ModularLayoutManager;

import java.util.Optional;

public class BackOfficeController {

    @FXML private VBox stockManagementButton;
    @FXML private VBox productManagementButton;
    @FXML private VBox suppliersButton;
    @FXML private VBox customerButton;
    @FXML private VBox paymentOptionsButton;
    @FXML private VBox serviceChargesButton;
    @FXML private Button logoutButton;

    @FXML
    public void initialize() {
        setupButtons();
    }

    private void setupButtons() {
        // Use the new modular navigation system
        stockManagementButton.setOnMouseClicked(e -> {
            ModularLayoutManager.navigateToStockManagement();
        });

        productManagementButton.setOnMouseClicked(e -> {
            ModularLayoutManager.navigateToProductManagement();
        });

        suppliersButton.setOnMouseClicked(e -> {
            // TODO: Implement suppliers module in modular system
            System.out.println("Suppliers management clicked");
        });

        customerButton.setOnMouseClicked(e -> {
            // TODO: Implement customer module in modular system
            System.out.println("Customer management clicked");
        });

        paymentOptionsButton.setOnMouseClicked(e -> {
            // TODO: Implement payment options module in modular system
            System.out.println("Payment options clicked");
        });

        serviceChargesButton.setOnMouseClicked(e -> {
            // TODO: Implement service charges module in modular system
            System.out.println("Service charges clicked");
        });

        // Add logout functionality if button exists
        if (logoutButton != null) {
            logoutButton.setOnAction(e -> handleLogout());
        }
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be redirected to the login screen.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            SessionService.clearSession();

            try {
                // Use direct JavaFX scene loading for login (complete scene change)
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/views/login.fxml")
                );
                javafx.scene.Scene loginScene = new javafx.scene.Scene(loader.load());

                javafx.stage.Stage currentStage = (javafx.stage.Stage) logoutButton.getScene().getWindow();
                currentStage.setScene(loginScene);
                currentStage.setTitle("ChronoPos - Login");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
