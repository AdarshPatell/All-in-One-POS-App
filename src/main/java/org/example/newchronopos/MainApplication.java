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
        DatabaseInitializer.initialize();   // creates schema + default users

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
        Scene scene = new Scene(loader.load());

        var css = getClass().getResource("/css/style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setTitle("Eaze POS - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}