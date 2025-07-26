package org.example.newchronopos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.newchronopos.dao.UserDAO;
import org.example.newchronopos.model.User;
import org.example.newchronopos.service.LicenseService;
import org.example.newchronopos.model.License;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class AdminSetupController {

    @FXML private TextField fullNameField, usernameField, emailField;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private Button createAdminBtn, skipSetupBtn;
    @FXML private Label setupStatus, licenseInfoLabel, licenseExpiryLabel;

    @FXML
    private void initialize() {
        loadLicenseInformation();
    }

    private void loadLicenseInformation() {
        try {
            License currentLicense = LicenseService.getCurrentLicense();
            if (currentLicense != null) {
                // Extract business name from customer details
                String customerDetails = currentLicense.getCustomerDetails();
                String businessName = extractBusinessNameFromDetails(customerDetails);

                licenseInfoLabel.setText("License activated for: " + businessName);

                // Format expiry date
                String expiryDate = currentLicense.getExpiresAt()
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
                licenseExpiryLabel.setText("License expires: " + expiryDate);
            }
        } catch (Exception e) {
            licenseInfoLabel.setText("License information unavailable");
            licenseExpiryLabel.setText("");
        }
    }

    private String extractBusinessNameFromDetails(String customerDetails) {
        if (customerDetails != null && customerDetails.contains("Name: ")) {
            String[] parts = customerDetails.split(",");
            for (String part : parts) {
                if (part.trim().startsWith("Name: ")) {
                    return part.trim().substring(6); // Remove "Name: "
                }
            }
        }
        return "Unknown Business";
    }

    @FXML
    private void createAdminAccount() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() ||
            password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all required fields");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Please enter a valid email address");
            return;
        }

        if (!isValidPassword(password)) {
            showError("Password must be at least 8 characters long and contain uppercase, lowercase, and numbers");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters long");
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();

            // Check if username already exists
            if (userDAO.usernameExists(username)) {
                showError("Username already exists. Please choose a different username.");
                return;
            }

            // Hash the password
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Create admin user
            User adminUser = new User();
            adminUser.setFullName(fullName);
            adminUser.setUsername(username);
            adminUser.setEmail(email);
            adminUser.setPassword(hashedPassword);
            adminUser.setRole("ADMIN");
            adminUser.setPhoneNo(""); // Can be updated later
            adminUser.setSalary(0.0);
            adminUser.setChangeAccess(true);
            adminUser.setAddress("");
            adminUser.setAdditionalDetails("System Administrator");
            adminUser.setUaeId("");
            adminUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // Save the admin user
            boolean success = userDAO.addUser(adminUser);

            if (success) {
                showSuccess("Admin account created successfully! Redirecting to dashboard...");

                // Disable form to prevent duplicate submission
                disableForm();

                // Wait a moment then redirect to login/dashboard
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::openDashboard);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();

            } else {
                showError("Failed to create admin account. Please try again.");
            }

        } catch (Exception e) {
            showError("Error creating admin account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void skipAdminSetup() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Skip Admin Setup");
        alert.setHeaderText("Skip Administrator Setup?");
        alert.setContentText("You can use the default login credentials:\nUsername: admin\nPassword: admin123\n\nYou can create additional users later from the dashboard.\n\nProceed to dashboard?");

        if (alert.showAndWait().orElse(null) == ButtonType.OK) {
            openDashboard();
        }
    }

    private void openDashboard() {
        try {
            // Close current window
            Stage currentStage = (Stage) createAdminBtn.getScene().getWindow();

            // Load login screen (which will redirect to dashboard if already logged in)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Scene scene = new Scene(loader.load());

            var css = getClass().getResource("/css/style.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            currentStage.setTitle("ChronoPos - Login");
            currentStage.setScene(scene);
            currentStage.centerOnScreen();

        } catch (Exception e) {
            showError("Error opening dashboard: " + e.getMessage());
        }
    }

    private void disableForm() {
        fullNameField.setDisable(true);
        usernameField.setDisable(true);
        emailField.setDisable(true);
        passwordField.setDisable(true);
        confirmPasswordField.setDisable(true);
        createAdminBtn.setDisable(true);
        skipSetupBtn.setDisable(true);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        return hasUpper && hasLower && hasDigit;
    }

    private void showError(String message) {
        setupStatus.setText("❌ " + message);
        setupStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
    }

    private void showSuccess(String message) {
        setupStatus.setText("✅ " + message);
        setupStatus.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px;");
    }
}
