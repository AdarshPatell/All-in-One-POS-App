package org.example.newchronopos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import org.example.newchronopos.controller.components.HeaderController;
import org.example.newchronopos.controller.components.SidebarController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainLayoutController implements Initializable {

    @FXML private StackPane dynamicContent;
    @FXML private HeaderController headerController;
    @FXML private SidebarController sidebarController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize with default content if needed
    }

    public void loadContent(String fxmlPath, String moduleTitle, String backPath) {
        try {
            // Load the content FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();

            // Clear current content and add new content
            dynamicContent.getChildren().clear();
            dynamicContent.getChildren().add(content);

            // Update header with navigation info
            if (headerController != null) {
                headerController.setNavigationInfo(moduleTitle, backPath);
            }

            // Update sidebar active state
            if (sidebarController != null) {
                String moduleName = extractModuleName(fxmlPath);
                sidebarController.setActiveModule(moduleName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractModuleName(String fxmlPath) {
        if (fxmlPath.contains("ProductManagement")) return "ProductManagement";
        if (fxmlPath.contains("StockManagement")) return "StockManagement";
        if (fxmlPath.contains("Dashboard")) return "Dashboard";
        if (fxmlPath.contains("reports")) return "Reports";
        if (fxmlPath.contains("BackOffice")) return "BackOffice";
        return "Dashboard";
    }

    public HeaderController getHeaderController() {
        return headerController;
    }

    public SidebarController getSidebarController() {
        return sidebarController;
    }
}
