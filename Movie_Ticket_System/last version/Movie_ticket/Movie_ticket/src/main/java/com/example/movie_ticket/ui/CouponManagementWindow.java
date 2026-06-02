package com.example.movie_ticket.ui;

import com.example.movie_ticket.model.CouponRow;
import com.example.movie_ticket.service.CouponService;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CouponManagementWindow {

    public void display(ObservableList<CouponRow> couponData) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Coupon Management");
        window.setMinWidth(680);

        Label titleLabel = new Label("Coupon Management");
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 22));

        TextField codeInput = new TextField();
        codeInput.setPromptText("e.g. CINEMA10");
        codeInput.setPrefWidth(150);

        TextField discountInput = new TextField();
        discountInput.setPromptText("e.g. 10.0");
        discountInput.setPrefWidth(100);

        CheckBox activeCheckbox = new CheckBox("Active");
        activeCheckbox.setSelected(true);

        Button addButton = new Button("+ ADD COUPON");
        addButton.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-padding: 7 16; -fx-cursor: hand;"
        );

        HBox addForm = new HBox(12,
                new Label("Code:"), codeInput,
                new Label("Discount %:"), discountInput,
                activeCheckbox,
                addButton
        );
        addForm.setAlignment(Pos.CENTER_LEFT);
        addForm.setPadding(new Insets(10, 0, 10, 0));

        TableView<CouponRow> table = new TableView<>(couponData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);

        TableColumn<CouponRow, String> codeCol = new TableColumn<>("Coupon Code");
        codeCol.setCellValueFactory(data -> data.getValue().coupon_codeProperty());

        TableColumn<CouponRow, String> discountCol = new TableColumn<>("Discount %");
        discountCol.setCellValueFactory(data -> data.getValue().discount_percentageProperty());

        TableColumn<CouponRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    boolean active = "1".equals(item);
                    setText(active ? "✔ Active" : "✘ Inactive");
                    setStyle(active
                            ? "-fx-text-fill: #2e7d32; -fx-font-weight: bold;"
                            : "-fx-text-fill: #c62828; -fx-font-weight: bold;"
                    );
                }
            }
        });

        TableColumn<CouponRow, Void> toggleCol = new TableColumn<>("Toggle");
        toggleCol.setCellFactory(col -> new TableCell<>() {
            private final Button toggleBtn = new Button();
            {
                toggleBtn.setStyle(
                        "-fx-font-size: 11; -fx-padding: 4 10; -fx-cursor: hand;"
                );
                toggleBtn.setOnAction(e -> {
                    CouponRow row = getTableView().getItems().get(getIndex());
                    boolean currentlyActive = row.isActive();
                    boolean newStatus = !currentlyActive;

                    CouponService service = new CouponService();
                    boolean success = service.toggleActive(row.getCode(), newStatus);

                    if (success) {
                        refreshTable(couponData);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error",
                                "Could not update status for: " + row.getCode());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CouponRow row = getTableView().getItems().get(getIndex());
                    boolean active = row.isActive();
                    toggleBtn.setText(active ? "Deactivate" : "Activate");
                    toggleBtn.setStyle(active
                            ? "-fx-background-color: #FF9800; -fx-text-fill: white; " +
                            "-fx-font-size: 11; -fx-padding: 4 10; -fx-cursor: hand;"
                            : "-fx-background-color: #2196F3; -fx-text-fill: white; " +
                            "-fx-font-size: 11; -fx-padding: 4 10; -fx-cursor: hand;"
                    );
                    setGraphic(toggleBtn);
                }
            }
        });

        TableColumn<CouponRow, Void> deleteCol = new TableColumn<>("Delete");
        deleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑 Delete");
            {
                deleteBtn.setStyle(
                        "-fx-background-color: #f44336; -fx-text-fill: white; " +
                                "-fx-font-size: 11; -fx-padding: 4 10; -fx-cursor: hand;"
                );
                deleteBtn.setOnAction(e -> {
                    CouponRow row = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Delete Coupon");
                    confirm.setHeaderText(null);
                    confirm.setContentText(
                            "Delete coupon \"" + row.getCode() + "\"? This cannot be undone."
                    );
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            CouponService service = new CouponService();
                            boolean success = service.deleteCoupon(row.getCode());
                            if (success) {
                                refreshTable(couponData);
                                showAlert(Alert.AlertType.INFORMATION,
                                        "Deleted", "Coupon deleted successfully.");
                            } else {
                                showAlert(Alert.AlertType.ERROR,
                                        "Error", "Could not delete coupon: " + row.getCode());
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        table.getColumns().addAll(codeCol, discountCol, statusCol, toggleCol, deleteCol);

        addButton.setOnAction(e -> {
            String code = codeInput.getText().trim();
            String discountStr = discountInput.getText().trim();
            boolean isActive = activeCheckbox.isSelected();

            if (code.isEmpty() || discountStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error",
                        "Please fill in all fields.");
                return;
            }

            CouponService service = new CouponService();
            if (service.isCodeExists(code)) {
                showAlert(Alert.AlertType.WARNING, "Duplicate Code",
                        "Coupon code '" + code.toUpperCase() + "' already exists.");
                return;
            }

            try {
                double discount = Double.parseDouble(discountStr);
                if (discount <= 0 || discount > 100) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error",
                            "Discount must be between 1 and 100.");
                    return;
                }

                boolean success = service.addCoupon(code.toUpperCase(), discount, isActive);
                if (success) {
                    refreshTable(couponData);
                    codeInput.clear();
                    discountInput.clear();
                    activeCheckbox.setSelected(true);
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "Coupon added successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Database Error",
                            "Failed to save coupon. Check console for details.");
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Input Error",
                        "Please enter a valid number for discount.");
            }
        });

        Button closeButton = new Button("CLOSE");
        closeButton.setStyle(
                "-fx-background-color: #607D8B; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-padding: 8 24; -fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> window.close());

        HBox bottomBar = new HBox(closeButton);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));

        VBox layout = new VBox(16, titleLabel, addForm, table, bottomBar);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: #f4f4f4;");

        Scene scene = new Scene(layout, 700, 500);
        window.setScene(scene);
        window.showAndWait();
    }

    private void refreshTable(ObservableList<CouponRow> couponData) {
        couponData.clear();
        couponData.addAll(new CouponService().getAllCoupons());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}