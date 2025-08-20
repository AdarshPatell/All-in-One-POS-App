package org.example.newchronopos.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.newchronopos.controller.MainLayoutController;

import java.io.IOException;

public class ModularLayoutManager {

    private static MainLayoutController mainLayoutController;
    private static Stage primaryStage;

    public static void initializeMainLayout(Stage stage) throws IOException {
        primaryStage = stage;

        // Load the main layout
        FXMLLoader loader = new FXMLLoader(ModularLayoutManager.class.getResource("/views/MainLayout.fxml"));
        Scene scene = new Scene(loader.load());

        mainLayoutController = loader.getController();

        stage.setScene(scene);
        stage.setTitle("ChronoPos - Point of Sale System");
        stage.setMaximized(true);
        stage.show();
    }

    public static void loadModuleContent(String contentPath, String moduleTitle, String backPath) {
        if (mainLayoutController != null) {
            mainLayoutController.loadContent(contentPath, moduleTitle, backPath);
        }
    }

    public static void loadContent(String contentPath) {
        if (mainLayoutController != null) {
            mainLayoutController.loadContent(contentPath, "", "");
        }
    }

    public static void navigateToProductManagement() {
        loadModuleContent("/views/content/ProductManagementContent.fxml",
                         "Product Management",
                         "/views/BackOffice.fxml");
    }

    public static void navigateToProducts() {
        loadModuleContent("/views/content/ProductsContent.fxml",
                         "Products",
                         "/views/ProductManagement.fxml");
    }

    public static void navigateToStockManagement() {
        loadModuleContent("/views/content/StockManagementContent.fxml",
                         "Stock Management",
                         "/views/BackOffice.fxml");
    }

    public static void navigateToStockAdjustment() {
        loadModuleContent("/views/content/StockAdjustmentContent.fxml",
                         "Stock Adjustment",
                         "/views/StockManagement.fxml");
    }

    public static void navigateToStockTransfer() {
        loadModuleContent("/views/content/StockTransferContent.fxml",
                         "Stock Transfer",
                         "/views/StockManagement.fxml");
    }

    public static void navigateToGoodsReceived() {
        loadModuleContent("/views/content/GoodsReceivedContent.fxml",
                         "Goods Received",
                         "/views/StockManagement.fxml");
    }

    public static void navigateToGoodsReturn() {
        loadModuleContent("/views/content/GoodsReturnContent.fxml",
                         "Goods Return",
                         "/views/StockManagement.fxml");
    }

    public static void navigateToGoodsReplaced() {
        loadModuleContent("/views/content/GoodsReplacedContent.fxml",
                         "Goods Replaced",
                         "/views/StockManagement.fxml");
    }

    public static void navigateToDashboard() {
        loadModuleContent("/views/content/Dashboard.fxml",
                         "Dashboard",
                         null);
    }

    public static void navigateToReports() {
        loadModuleContent("/views/reports.fxml",
                         "Reports",
                         "/views/BackOffice.fxml");
    }

    public static void navigateToBackOffice() {
        loadModuleContent("/views/content/BackOfficeContent.fxml",
                         "Back Office",
                         null);
    }

    public static void navigateToSuppliers() {
        loadModuleContent("/views/content/SuppliersContent.fxml",
                         "Suppliers Management",
                         "/views/content/ProductManagementContent.fxml");
    }

    public static void navigateToBrands() {
        loadModuleContent("/views/content/BrandsContent.fxml",
                         "Brands Management",
                         "/views/content/ProductManagementContent.fxml");
    }

    public static void navigateToUnits() {
        loadModuleContent("/views/content/UnitsContent.fxml",
                         "Units Management",
                         "/views/content/ProductManagementContent.fxml");
    }

    public static MainLayoutController getMainLayoutController() {
        return mainLayoutController;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static ModularLayoutManager getInstance() {
        // Return a singleton instance for non-static access
        return new ModularLayoutManager();
    }
}
