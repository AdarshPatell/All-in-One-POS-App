package org.example.newchronopos.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.newchronopos.dao.ProductDAO;
import org.example.newchronopos.model.Product;

public class ProductController {
    @FXML private TableView<Product> table;
    @FXML private TableColumn<Product, String> nameCol;
    @FXML private TableColumn<Product, Double> priceCol;

    private final ProductDAO dao = new ProductDAO();

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getName()));
        priceCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPrice()));
        table.setItems(FXCollections.observableArrayList(dao.getAll()));
    }
}
