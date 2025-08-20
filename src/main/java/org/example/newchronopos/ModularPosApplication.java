package org.example.newchronopos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.newchronopos.db.DatabaseInitializer;
import org.example.newchronopos.service.LicenseService;
import org.example.newchronopos.service.SessionService;
import org.example.newchronopos.util.ModularLayoutManager;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ModularPosApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database first
            DatabaseInitializer.initialize();

            // Check if license file exists in current directory (for development)
            boolean licenseFileExists = Files.exists(Paths.get("system.license"));

            // Check if system is licensed
            if (!licenseFileExists && !LicenseService.isSystemLicensed()) {
                // Show license activation screen
                showLicenseScreen(primaryStage);
            } else if (SessionService.isUserLoggedIn()) {
                // User is already logged in, go directly to modular dashboard
                initializeModularSystem(primaryStage);
            } else {
                // Show normal login screen
                showLoginScreen(primaryStage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLicenseScreen(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/license_activation.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("ChronoPos - License Activation");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void showLoginScreen(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("ChronoPos - Login");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void initializeModularSystem(Stage stage) throws Exception {
        // Initialize the modular layout system
        ModularLayoutManager.initializeMainLayout(stage);

        // Start with the dashboard by default
        ModularLayoutManager.navigateToDashboard();
    }

    // Method to switch to modular system after login (called from LoginController)
    public static void switchToModularSystem(Stage stage) {
        try {
            ModularLayoutManager.initializeMainLayout(stage);
            ModularLayoutManager.navigateToDashboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
