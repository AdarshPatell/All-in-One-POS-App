package org.example.newchronopos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.example.newchronopos.dao.ProductDAO;
import org.example.newchronopos.model.Product;
import org.example.newchronopos.model.ProductView;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class ProductsController implements Initializable {

    @FXML
    private TableView<ProductView> productTable;
    @FXML
    private TableColumn<ProductView, Void> colProduct;
    @FXML
    private TableColumn<ProductView, String> colItemId;
    @FXML
    private TableColumn<ProductView, Integer> colStock;
    @FXML
    private TableColumn<ProductView, String> colCategory;
    @FXML
    private TableColumn<ProductView, Double> colPrice;
    @FXML
    private TableColumn<ProductView, String> colAvailability;
    @FXML
    private TableColumn<ProductView, Void> colAction;
    @FXML
    private Button btnAddProduct;

    private final ObservableList<ProductView> productList = FXCollections.observableArrayList();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadProductData();
        if (btnAddProduct != null) {
            btnAddProduct.setOnAction(e -> showAddProductDialog());
        }
    }

    private void setupTableColumns() {
        // --- Standard Columns ---
        colItemId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colAvailability.setCellValueFactory(new PropertyValueFactory<>("availability"));

        // --- Custom "Product" Column ---
        colProduct.setCellFactory(param -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            private final Label lblName = new Label();
            private final Label lblDesc = new Label();
            private final HBox contentBox = new HBox(10);

            {
                lblName.setFont(new Font("System Bold", 13));
                lblName.setTextFill(Color.WHITE);
                lblDesc.setTextFill(Color.LIGHTGRAY);

                VBox textBox = new VBox(lblName, lblDesc);
                imageView.setFitHeight(40);
                imageView.setFitWidth(40);
                contentBox.getChildren().addAll(imageView, textBox);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ProductView product = getTableView().getItems().get(getIndex());
                    imageView.setImage(product.getPhoto().getImage());
                    lblName.setText(product.getProductName());
                    lblDesc.setText(product.getDescription());
                    setGraphic(contentBox);
                }
            }
        });

        // --- Custom "Action" Column ---
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDelete = new Button("🗑️");
            private final HBox pane = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setOnAction(event -> {
                    ProductView product = getTableView().getItems().get(getIndex());
                    showEditProductDialog(product);
                });
                btnDelete.setOnAction(event -> {
                    ProductView product = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(AlertType.CONFIRMATION, "Delete product '" + product.getProductName() + "'?");
                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        productDAO.delete(Integer.parseInt(product.getItemId()));
                        loadProductData();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    private void loadProductData() {
        productList.clear();
        for (Product product : productDAO.getAll()) {
            ImageView imageView;
            try {
                if (product.getPhotoPath() != null && !product.getPhotoPath().isEmpty()) {
                    imageView = new ImageView(new Image(product.getPhotoPath(), 40, 40, true, true));
                } else {
                    imageView = new ImageView(); // Empty/default image
                }
            } catch (Exception e) {
                imageView = new ImageView();
            }
            productList.add(new ProductView(
                imageView,
                product.getName(),
                product.getDescription(),
                product.getItemId(),
                product.getStock(),
                product.getCategory(),
                product.getPrice(),
                product.getAvailability()
            ));
        }
        productTable.setItems(productList);
    }

    private void showAddProductDialog() {
        Product product = showProductDialog(null);
        if (product != null) {
            productDAO.insert(product);
            loadProductData();
        }
    }

    private void showEditProductDialog(ProductView productView) {
        Product product = showProductDialog(productView);
        if (product != null) {
            product.setId(Integer.parseInt(productView.getItemId()));
            productDAO.update(product);
            loadProductData();
        }
    }

    private Product showProductDialog(ProductView productView) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle(productView == null ? "Add Product" : "Edit Product");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField descField = new TextField();
        descField.setPromptText("Description");
        TextField itemIdField = new TextField();
        itemIdField.setPromptText("Item ID");
        TextField stockField = new TextField();
        stockField.setPromptText("Stock");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField photoPathField = new TextField();
        photoPathField.setPromptText("Photo Path");
        TextField availabilityField = new TextField();
        availabilityField.setPromptText("Availability");

        if (productView != null) {
            nameField.setText(productView.getProductName());
            descField.setText(productView.getDescription());
            itemIdField.setText(productView.getItemId());
            stockField.setText(String.valueOf(productView.getStock()));
            categoryField.setText(productView.getCategory());
            priceField.setText(String.valueOf(productView.getPrice()));
            availabilityField.setText(productView.getAvailability());
        }

        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Description:"), descField);
        grid.addRow(2, new Label("Item ID:"), itemIdField);
        grid.addRow(3, new Label("Stock:"), stockField);
        grid.addRow(4, new Label("Category:"), categoryField);
        grid.addRow(5, new Label("Price:"), priceField);
        grid.addRow(6, new Label("Photo Path:"), photoPathField);
        grid.addRow(7, new Label("Availability:"), availabilityField);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    return new Product(
                        0,
                        nameField.getText(),
                        descField.getText(),
                        itemIdField.getText(),
                        Integer.parseInt(stockField.getText()),
                        categoryField.getText(),
                        Double.parseDouble(priceField.getText()),
                        photoPathField.getText(),
                        availabilityField.getText()
                    );
                } catch (Exception e) {
                    showAlert("Invalid input. Please check your entries.");
                }
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}