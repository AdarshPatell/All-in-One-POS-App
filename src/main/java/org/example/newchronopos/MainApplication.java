package org.example.newchronopos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.newchronopos.db.DatabaseInitializer;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseInitializer.initialize(); // Ensures schema is created

        var fxmlUrl = getClass().getResource("/views/product.fxml");
        if (fxmlUrl == null) {
            throw new IOException("FXML file not found: /views/product.fxml");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(fxmlLoader.load());

        var cssUrl = getClass().getResource("/css/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("Warning: CSS file not found: /css/style.css");
        }

        stage.setTitle("Eaze POS - Product View");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}