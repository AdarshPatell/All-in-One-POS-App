package org.example.newchronopos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.newchronopos.dao.CategoryDAO;
import org.example.newchronopos.model.Category;

import java.io.File;
import java.util.Optional;

public class AddCategoryController {

    @FXML private ImageView categoryImageView;
    @FXML private Button selectImageButton;
    @FXML private TextField categoryNameField;
    @FXML private ComboBox<Category> parentCategoryCombo;
    @FXML private TextArea descriptionArea;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private CategoryDAO categoryDAO = new CategoryDAO();
    private String selectedImagePath;
    private ProductManagementController parentController;
    private Category editingCategory;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        setupButtons();
        loadParentCategories();
        setupDefaultImage();
        setupComboBoxRenderer();
    }

    private void setupButtons() {
        if (selectImageButton != null) {
            selectImageButton.setOnAction(e -> selectCategoryImage());
        }
        if (cancelButton != null) {
            cancelButton.setOnAction(e -> closeDialog());
        }
        if (saveButton != null) {
            saveButton.setOnAction(e -> saveCategory());
        }
    }

    private void loadParentCategories() {
        try {
            if (parentCategoryCombo != null) {
                // Add "No Parent" option first
                Category noParent = new Category();
                noParent.setId(0);
                noParent.setName("No Parent");
                parentCategoryCombo.getItems().clear();
                parentCategoryCombo.getItems().add(noParent);
                parentCategoryCombo.setValue(noParent);

                // Try to load existing categories
                var categories = categoryDAO.getAllCategories();
                parentCategoryCombo.getItems().addAll(categories);
            }
        } catch (Exception e) {
            // If database isn't ready, just show "No Parent" option
            System.out.println("Database not ready for categories: " + e.getMessage());
            if (parentCategoryCombo != null && parentCategoryCombo.getItems().isEmpty()) {
                Category noParent = new Category();
                noParent.setId(0);
                noParent.setName("No Parent");
                parentCategoryCombo.getItems().add(noParent);
                parentCategoryCombo.setValue(noParent);
            }
        }
    }

    private void setupComboBoxRenderer() {
        if (parentCategoryCombo != null) {
            parentCategoryCombo.setCellFactory(param -> new ListCell<Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });

            parentCategoryCombo.setButtonCell(new ListCell<Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
        }
    }

    private void setupDefaultImage() {
        if (categoryImageView != null) {
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-category.png"));
                categoryImageView.setImage(defaultImage);
            } catch (Exception e) {
                // If default image not found, create a placeholder
                categoryImageView.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc;");
            }
        }
    }

    private void selectCategoryImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Category Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) selectImageButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                categoryImageView.setImage(image);
                selectedImagePath = selectedFile.getAbsolutePath();
            } catch (Exception e) {
                showAlert("Error", "Failed to load selected image: " + e.getMessage());
            }
        }
    }

    private void saveCategory() {
        if (!validateInput()) {
            return;
        }

        try {
            Category category = isEditMode ? editingCategory : new Category();

            category.setName(categoryNameField.getText().trim());
            category.setDescription(descriptionArea.getText().trim());
            category.setImageUrl(selectedImagePath);
            category.setStatus("Active");

            Category selectedParent = parentCategoryCombo.getValue();
            if (selectedParent != null && selectedParent.getId() > 0) {
                category.setParentId(selectedParent.getId());
            } else {
                category.setParentId(null);
            }

            boolean success;
            if (isEditMode) {
                success = categoryDAO.updateCategory(category);
            } else {
                success = categoryDAO.addCategory(category);
            }

            if (success) {
                showAlert("Success", isEditMode ? "Category updated successfully!" : "Category added successfully!");
                if (parentController != null) {
                    parentController.refreshCategories();
                }
                closeDialog();
            } else {
                showAlert("Error", isEditMode ? "Failed to update category." : "Failed to add category.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        if (categoryNameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Category name is required.");
            categoryNameField.requestFocus();
            return false;
        }
        return true;
    }

    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to set edit mode
    public void setEditMode(Category category) {
        this.isEditMode = true;
        this.editingCategory = category;

        if (category != null) {
            categoryNameField.setText(category.getName());
            descriptionArea.setText(category.getDescription());

            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                try {
                    Image image = new Image("file:" + category.getImageUrl());
                    categoryImageView.setImage(image);
                    selectedImagePath = category.getImageUrl();
                } catch (Exception e) {
                    // Keep default image if loading fails
                }
            }

            // Set parent category
            if (category.getParentId() != null) {
                Optional<Category> parent = categoryDAO.getCategoryById(category.getParentId());
                parent.ifPresent(p -> parentCategoryCombo.setValue(p));
            }

            saveButton.setText("Update Category");
        }
    }

    public void setParentController(ProductManagementController controller) {
        this.parentController = controller;
    }
}
