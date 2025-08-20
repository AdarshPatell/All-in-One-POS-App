package org.example.newchronopos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.newchronopos.db.DatabaseInitializer;
import org.example.newchronopos.service.LicenseService;
import org.example.newchronopos.service.SessionService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseInitializer.initialize();   // creates schema + default users

        // Check if license file exists in current directory (for development)
        boolean licenseFileExists = Files.exists(Paths.get("system.license"));

        // Check if system is licensed
        if (!licenseFileExists && !LicenseService.isSystemLicensed()) {
            // Show license activation screen
            showLicenseScreen(stage);
        } else if (SessionService.isUserLoggedIn()) {
            // User is already logged in, go directly to dashboard
            showDashboard(stage);
        } else {
            // Show normal login screen
            showLoginScreen(stage);
        }
    }

    private void showLicenseScreen(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/license_activation.fxml"));
        Scene scene = new Scene(loader.load());

        var css = getClass().getResource("/css/style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setTitle("ChronoPos - License Activation");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void showLoginScreen(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Scene scene = new Scene(loader.load());

        var css = getClass().getResource("/css/style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setTitle("ChronoPos - Login");
        stage.setScene(scene);
        stage.show();
    }

    private void showDashboard(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BackOffice.fxml"));
        Scene scene = new Scene(loader.load());

        var css = getClass().getResource("/css/style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setTitle("ChronoPos - Dashboard");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}