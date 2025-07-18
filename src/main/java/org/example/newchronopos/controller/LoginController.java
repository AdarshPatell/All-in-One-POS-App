package org.example.newchronopos.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.newchronopos.dao.OwnerDAO;
import org.example.newchronopos.dao.UserDAO;
import org.example.newchronopos.model.Owner;
import org.example.newchronopos.model.User;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label lblError;

    private final OwnerDAO ownerDAO = new OwnerDAO();
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Check for empty input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        // Try owner login
        Owner owner = ownerDAO.findByUsername(username);
        if (owner != null && ownerDAO.checkPassword(owner, password)) {
            loadScene("OwnerDashboard.fxml");  // Owner view
            return;
        }

        // Try user login
        User user = userDAO.findByUsername(username);
        if (user != null && userDAO.checkPassword(user, password)) {
            switch (user.getRole().toLowerCase()) {
                case "admin", "cashier", "manager" -> loadScene("product.fxml");
                default -> showError("Unknown user role: " + user.getRole());
            }
            return;
        }

        // Login failed
        showError("Invalid credentials. Please try again.");
    }

    private void loadScene(String fxmlFile) {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + fxmlFile));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load screen: " + fxmlFile);
        }
    }

    private void showError(String message) {
        if (lblError != null) {
            lblError.setText(message);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Login Error");
            alert.setContentText(message);
            alert.showAndWait();
        }
    }
}
