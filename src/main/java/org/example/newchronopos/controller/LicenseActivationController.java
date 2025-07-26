package org.example.newchronopos.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.newchronopos.service.LicenseService;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class LicenseActivationController {

    // UI Elements
    @FXML private VBox step1, step2, step3, step4;
    @FXML private Label step1Indicator, step2Indicator, step3Indicator, step4Indicator;
    @FXML private TextField scratchCodeField, customerNameField, customerEmailField, customerPhoneField;
    @FXML private TextArea embeddedPasswordField, customerAddressField, salesPersonKeyArea, licenseKeyField;
    @FXML private Label step1Status, step2Status, step4Status;
    @FXML private Button verifyScratchBtn, generateKeyBtn, copySalesKeyBtn, saveKeyBtn, proceedToLicenseBtn, activateLicenseBtn;
    @FXML private Button backToStep1Btn, backToStep2Btn, backToStep3Btn;

    private Map<String, Object> salesPersonInfo;
    private String currentScratchCode;

    @FXML
    private void initialize() {
        // Initialize the first step
        showStep(1);
    }

    @FXML
    private void verifyScratchCard() {
        String scratchCode = scratchCodeField.getText().trim().toUpperCase();
        String embeddedPassword = embeddedPasswordField.getText().trim();

        if (scratchCode.isEmpty() || embeddedPassword.isEmpty()) {
            showError(step1Status, "Please enter both scratch code and embedded password");
            return;
        }

        if (scratchCode.length() != 12) {
            showError(step1Status, "Scratch code must be exactly 12 characters");
            return;
        }

        try {
            // Validate scratch card
            if (!LicenseService.validateScratchCard(scratchCode, embeddedPassword)) {
                showError(step1Status, "Invalid scratch card or embedded password");
                return;
            }

            // Get sales person information
            salesPersonInfo = LicenseService.getSalesPersonInfo(embeddedPassword);
            currentScratchCode = scratchCode;

            Object salesPersonNameObj = salesPersonInfo.get("salesPersonName");
            String salesPersonName = salesPersonNameObj != null ? salesPersonNameObj.toString() : "Unknown";

            showSuccess(step1Status, "✅ Scratch card verified! Sales Person: " + salesPersonName);

            // Proceed to step 2 after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    Platform.runLater(() -> showStep(2));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (Exception e) {
            showError(step1Status, "Error validating scratch card: " + e.getMessage());
        }
    }

    @FXML
    private void generateSalesPersonKey() {
        String customerName = customerNameField.getText().trim();
        String customerEmail = customerEmailField.getText().trim();
        String customerPhone = customerPhoneField.getText().trim();
        String customerAddress = customerAddressField.getText().trim();

        if (customerName.isEmpty() || customerEmail.isEmpty() || customerPhone.isEmpty() || customerAddress.isEmpty()) {
            showError(step2Status, "Please fill in all customer information fields");
            return;
        }

        // Basic email validation
        if (!customerEmail.contains("@") || !customerEmail.contains(".")) {
            showError(step2Status, "Please enter a valid email address");
            return;
        }

        try {
            String salesPersonKey = LicenseService.generateSalesPersonKey(
                currentScratchCode, customerName, customerEmail, customerPhone, customerAddress
            );

            salesPersonKeyArea.setText(salesPersonKey);
            showSuccess(step2Status, "✅ Sales person key generated successfully!");

            // Proceed to step 3 after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    Platform.runLater(() -> showStep(3));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (Exception e) {
            showError(step2Status, "Error generating sales person key: " + e.getMessage());
        }
    }

    @FXML
    private void copySalesPersonKey() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(salesPersonKeyArea.getText());
        clipboard.setContent(content);

        // Show temporary success message
        copySalesKeyBtn.setText("✅ Copied!");
        copySalesKeyBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");

        // Reset button after 2 seconds
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> {
                    copySalesKeyBtn.setText("📋 Copy to Clipboard");
                    copySalesKeyBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @FXML
    private void saveKeyToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Sales Person Key");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("sales_person_key.txt");

        File file = fileChooser.showSaveDialog(saveKeyBtn.getScene().getWindow());
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("ChronoPos Sales Person Key\n");
                writer.write("Generated on: " + java.time.LocalDateTime.now() + "\n");
                writer.write("Scratch Code: " + currentScratchCode + "\n");
                writer.write("Customer: " + customerNameField.getText() + "\n");
                writer.write("Email: " + customerEmailField.getText() + "\n");
                writer.write("Phone: " + customerPhoneField.getText() + "\n");
                writer.write("\n--- SALES PERSON KEY ---\n");
                writer.write(salesPersonKeyArea.getText());
                writer.write("\n\nSend this key to your admin for license generation.");

                saveKeyBtn.setText("✅ Saved!");
                saveKeyBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");

                // Reset button after 2 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        Platform.runLater(() -> {
                            saveKeyBtn.setText("💾 Save to File");
                            saveKeyBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to save file");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void proceedToLicenseActivation() {
        showStep(4);
    }

    @FXML
    private void activateLicense() {
        String licenseKey = licenseKeyField.getText().trim();

        if (licenseKey.isEmpty()) {
            showError(step4Status, "Please enter the license key");
            return;
        }

        try {
            if (LicenseService.activateLicense(licenseKey)) {
                showSuccess(step4Status, "🎉 License activated successfully! Starting application...");

                // Wait a moment then start the main application
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        Platform.runLater(this::startMainApplication);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();

            } else {
                showError(step4Status, "Failed to activate license. Please check the license key.");
            }

        } catch (Exception e) {
            showError(step4Status, "Error activating license: " + e.getMessage());
        }
    }

    // Navigation methods
    @FXML
    private void goToStep1() {
        showStep(1);
    }

    @FXML
    private void goToStep2() {
        showStep(2);
    }

    @FXML
    private void goToStep3() {
        showStep(3);
    }

    private void startMainApplication() {
        try {
            // Close current window
            Stage currentStage = (Stage) activateLicenseBtn.getScene().getWindow();

            // Load admin setup screen instead of login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin_setup.fxml"));
            Scene scene = new Scene(loader.load());

            var css = getClass().getResource("/css/style.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            currentStage.setTitle("ChronoPos - Admin Setup");
            currentStage.setScene(scene);
            currentStage.centerOnScreen();

        } catch (Exception e) {
            showError(step4Status, "Error opening admin setup: " + e.getMessage());
        }
    }

    private void showStep(int stepNumber) {
        // Hide all steps
        step1.setVisible(false);
        step2.setVisible(false);
        step3.setVisible(false);
        step4.setVisible(false);

        // Reset all indicators
        resetIndicator(step1Indicator);
        resetIndicator(step2Indicator);
        resetIndicator(step3Indicator);
        resetIndicator(step4Indicator);

        // Show the selected step and update indicators
        switch (stepNumber) {
            case 1:
                step1.setVisible(true);
                setActiveIndicator(step1Indicator);
                break;
            case 2:
                step2.setVisible(true);
                setActiveIndicator(step2Indicator);
                setCompletedIndicator(step1Indicator);
                break;
            case 3:
                step3.setVisible(true);
                setActiveIndicator(step3Indicator);
                setCompletedIndicator(step1Indicator);
                setCompletedIndicator(step2Indicator);
                break;
            case 4:
                step4.setVisible(true);
                setActiveIndicator(step4Indicator);
                setCompletedIndicator(step1Indicator);
                setCompletedIndicator(step2Indicator);
                setCompletedIndicator(step3Indicator);
                break;
        }
    }

    private void setActiveIndicator(Label indicator) {
        indicator.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 15; -fx-min-width: 30; -fx-min-height: 30; -fx-alignment: center; -fx-font-weight: bold;");
    }

    private void setCompletedIndicator(Label indicator) {
        indicator.setText("✓");
        indicator.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 15; -fx-min-width: 30; -fx-min-height: 30; -fx-alignment: center; -fx-font-weight: bold;");
    }

    private void resetIndicator(Label indicator) {
        String originalText = indicator.getText();
        if (originalText.equals("✓")) {
            // Reset to step number if it was showing checkmark
            if (indicator == step1Indicator) indicator.setText("1");
            else if (indicator == step2Indicator) indicator.setText("2");
            else if (indicator == step3Indicator) indicator.setText("3");
            else if (indicator == step4Indicator) indicator.setText("4");
        }
        indicator.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-background-radius: 15; -fx-min-width: 30; -fx-min-height: 30; -fx-alignment: center; -fx-font-weight: bold;");
    }

    private void showError(Label statusLabel, String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
    }

    private void showSuccess(Label statusLabel, String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px;");
    }
}
