package com.example.movie_ticket.ui;

import com.example.movie_ticket.database.ShopDAO;
import com.example.movie_ticket.model.Product;
import com.example.movie_ticket.ui.screens.UI;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ItemManagementWindow {

    private final ShopDAO shopDAO = new ShopDAO();

    public Stage show() {

        Stage stage = new Stage();
        stage.setTitle("Item Management");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle(UI.getLinearGradient());

        Label title = UI.createSectionTitle("Item Management");

        TableView<Product> table = new TableView<>();

        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));
        idCol.setPrefWidth(70);

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(160);

        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPrice()));
        priceCol.setPrefWidth(100);

        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getQuantity()));
        quantityCol.setPrefWidth(100);

        table.getColumns().setAll(idCol, nameCol, priceCol, quantityCol);

        loadProducts(table);

        Button addButton = UI.createCustomButton2("Add");
        Button deleteButton = UI.createCustomButton2("Delete");
        Button refreshButton = UI.createCustomButton2("Refresh");
        Button updateButton = UI.createCustomButton2("Update");

        addButton.setOnAction(e -> showAddItemWindow(table));

        updateButton.setOnAction(e -> {

            Product selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Update Error",
                        "Please select item to update."
                );
                return;
            }

            showUpdateItemWindow(table, selected);
        });

        deleteButton.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert(Alert.AlertType.ERROR, "Delete Error", "Please select item to delete.");
                return;
            }

            shopDAO.deleteProductById(selected.getId());
            loadProducts(table);

            showAlert(Alert.AlertType.INFORMATION, "Delete", "Item deleted successfully.");
        });

        refreshButton.setOnAction(e -> loadProducts(table));

        HBox buttonBox = new HBox(10, addButton, deleteButton, refreshButton, updateButton);

        root.getChildren().addAll(title, table, buttonBox);

        Scene scene = new Scene(root, 520, 420);
        stage.setScene(scene);
        stage.show();

        return stage;
    }

    private void loadProducts(TableView<Product> table) {
        table.getItems().clear();

        table.getItems().addAll(shopDAO.getAllProducts());

        table.refresh();
    }

    private void showUpdateItemWindow(TableView<Product> table, Product selected) {

        Stage stage = new Stage();
        stage.setTitle("Update Item");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #FFF3E0, #FFD54F);"
        );

        TextField nameField = new TextField(selected.getName());

        TextField priceField = new TextField(
                String.valueOf(selected.getPrice())
        );

        TextField imageField = new TextField(
                selected.getImagePath()
        );

        TextField quantityField = new TextField(
                String.valueOf(selected.getQuantity())
        );

        Button chooseImageButton = new Button("Choose Image");
        Button saveButton = new Button("Save Changes");

        String buttonStyle =
                "-fx-background-color: #F57C00;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 16 8 16;";

        chooseImageButton.setStyle(buttonStyle);
        saveButton.setStyle(buttonStyle);

        chooseImageButton.setOnAction(e -> {
            chooseAndCopyImage(stage, imageField);
        });

        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                String imagePath = imageField.getText();
                int quantity = Integer.parseInt(quantityField.getText());

                if (name.isBlank() || imagePath.isBlank()) {
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Input Error",
                            "Name and image path cannot be empty."
                    );
                    return;
                }

                shopDAO.updateProduct(
                        selected.getId(),
                        name,
                        price,
                        imagePath,
                        quantity
                );

                loadProducts(table);

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Update",
                        "Item updated successfully."
                );

                stage.close();

            } catch (NumberFormatException ex) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Input Error",
                        "Price and quantity must be valid numbers."
                );
            }
        });

        root.getChildren().addAll(
                new Label("Name"),
                nameField,
                new Label("Price"),
                priceField,
                new Label("Image Path"),
                imageField,
                chooseImageButton,
                new Label("Quantity"),
                quantityField,
                saveButton
        );

        Scene scene = new Scene(root, 360, 430);
        stage.setScene(scene);
        stage.show();
    }

    private void showAddItemWindow(TableView<Product> table) {

        Stage stage = new Stage();
        stage.setTitle("Add Item");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #FFF3E0, #FFD54F);"
        );

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        TextField imageField = new TextField();
        imageField.setPromptText("Image Path");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        Button chooseImageButton = new Button("Choose Image");
        Button saveButton = new Button("Save");

        String buttonStyle =
                "-fx-background-color: #F57C00;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 16 8 16;";

        chooseImageButton.setStyle(buttonStyle);
        saveButton.setStyle(buttonStyle);

        chooseImageButton.setOnAction(e -> {
            chooseAndCopyImage(stage, imageField);
        });

        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                String imagePath = imageField.getText();
                int quantity = Integer.parseInt(quantityField.getText());

                if (name.isBlank() || imagePath.isBlank()) {
                    showAlert(Alert.AlertType.ERROR, "Input Error", "Name and image path cannot be empty.");
                    return;
                }

                shopDAO.addProduct(name, price, imagePath, quantity);

                loadProducts(table);

                showAlert(Alert.AlertType.INFORMATION, "Add Item", "Item added successfully.");

                stage.close();

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Price and quantity must be valid numbers.");
            }
        });

        root.getChildren().addAll(
                nameField,
                priceField,
                imageField,
                chooseImageButton,
                quantityField,
                saveButton
        );

        Scene scene = new Scene(root, 350, 320);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void chooseAndCopyImage(Stage owner, TextField imageField) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Product Image");

        File selectedFile = fileChooser.showOpenDialog(owner);
        if (selectedFile == null) {
            return;
        }

        try {
            Path projectRoot = findProjectRoot();
            Path destinationFolder = projectRoot.resolve(Path.of("src", "main", "resources", "images"));
            Files.createDirectories(destinationFolder);

            Path destinationFile = destinationFolder.resolve(selectedFile.getName());
            Files.copy(
                    selectedFile.toPath(),
                    destinationFile,
                    StandardCopyOption.REPLACE_EXISTING
            );

            imageField.setText("/images/" + selectedFile.getName());

            System.out.println("Image copied to:");
            System.out.println(destinationFile.toAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Image Error",
                    "Could not copy image:\n" + ex.getMessage()
            );
        }
    }

    private static Path findProjectRoot() {
        Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        Path p = current;
        while (p != null) {
            if (Files.exists(p.resolve("pom.xml"))) {
                return p;
            }
            p = p.getParent();
        }
        return current;
    }
}
