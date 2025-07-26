package org.example.newchronopos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.newchronopos.db.DatabaseInitializer;
import org.example.newchronopos.service.LicenseService;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseInitializer.initialize();   // creates schema + default users

        // Check if system is licensed
        if (!LicenseService.isSystemLicensed()) {
            // Show license activation screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/license_activation.fxml"));
            Scene scene = new Scene(loader.load());

            var css = getClass().getResource("/css/style.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            stage.setTitle("ChronoPos - License Activation");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } else {
            // Show normal login screen
            showLoginScreen(stage);
        }
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

    public static void main(String[] args) {
        launch();
    }
}