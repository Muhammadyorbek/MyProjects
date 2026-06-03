package com.example.movie_ticket.ui.screens;

import com.example.movie_ticket.database.MovieDAO;
import com.example.movie_ticket.database.SessionDAO;
import com.example.movie_ticket.database.UserDAO;
import com.example.movie_ticket.model.*;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

public class Table {
    private static void tableHeaderStyle(TableView<?> table) {
        table.lookupAll(".column-header-background").forEach(node ->
                node.setStyle("-fx-background-color: #d9d9d9; -fx-background-radius: 14 14 0 0;")
        );
        table.lookupAll(".column-header .label").forEach(node ->
                node.setStyle("-fx-text-fill: " + UI.getBlack() + "; -fx-font-weight: bold; -fx-opacity: 1;")
        );
    }
    private static <T> TableView<T> styledTable(ObservableList<T> data, double prefHeight, double maxWidth) {
        TableView<T> table = new TableView<>(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setFixedCellSize(54);
        table.setPrefHeight(prefHeight);
        table.setMaxWidth(maxWidth);
        table.setStyle(
                "-fx-background-color: " + UI.getPanelGray() + ";" +
                        "-fx-control-inner-background: " + UI.getPanelGray() + ";" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-radius: 18;" +
                        "-fx-border-color: " + UI.getBorderColor() + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-padding: 8;" +
                        "-fx-table-cell-border-color: transparent;" +
                        "-fx-selection-bar: " + UI.getYellow() + ";" +
                        "-fx-selection-bar-non-focused: " + UI.getYellow() + ";" +
                        "-fx-selection-bar-text: " + UI.getBlack() + ";"
        );
        table.setRowFactory(tv -> new DarkTableRow<>());

        table.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            applyTableHeaderStyle(table);
        });
        applyTableHeaderStyle(table);
        return table;
    }
    private static TableColumn<AdminMovieRow, Void> movieActionColumn(MovieDAO  movieDAO) {
        TableColumn<AdminMovieRow, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setSortable(false);
        actionColumn.setResizable(false);
        actionColumn.setReorderable(false);
        actionColumn.setPrefWidth(140);

        actionColumn.setCellFactory(column -> new TableCell<>() {
            final Button deleteButton = new Button("Delete");

            {
                deleteButton.setFont(Font.font("Bahnschrift", FontWeight.BOLD, 14));
                UI.applyActionButtonStyle(deleteButton);

                deleteButton.setOnAction(event -> {
                    AdminMovieRow rowData = getTableView().getItems().get(getIndex());

                    movieDAO.DeleteMovie(Integer.parseInt(rowData.getId()));

                    getTableView().getItems().remove(rowData);

                    System.out.println("Movie deleted from DB and UI");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
        return actionColumn;
    }
    private static TableColumn<SessionRow, Void> sessionActionColumn( SessionDAO sessionDAO, String action) {
        TableColumn<SessionRow, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setSortable(false);
        actionColumn.setResizable(false);
        actionColumn.setReorderable(false);
        actionColumn.setPrefWidth(140);

        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button actionButton = new Button(action);

            {
                actionButton.setFont(Font.font("Bahnschrift", FontWeight.BOLD, 14));
                UI.applyActionButtonStyle(actionButton);

                actionButton.setOnAction(event -> {
                    SessionRow rowData = getTableView().getItems().get(getIndex());

                    try {
                        int idToDelete = Integer.parseInt(rowData.getShow_id());

                        sessionDAO.deleteSession(idToDelete);

                        javafx.application.Platform.runLater(() -> {
                            getTableView().getItems().remove(rowData);
                        });

                        System.out.println("Session " + idToDelete + " " + action  + "successfully.");
                    } catch (NumberFormatException e) {
                        System.err.println("Ошибка: ID сессии не является числом!");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButton);
                }
            }
        });
        return actionColumn;
    }


    private static <T> TableColumn<T, String> textColumn(String title, double widthWeight, Callback<T, StringProperty> propertyAccessor) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> propertyAccessor.call(cellData.getValue()));
        column.setReorderable(false);
        column.setSortable(false);
        column.setResizable(false);
        column.setPrefWidth(120 * widthWeight);
        column.setStyle("-fx-alignment: CENTER;");
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                    return;
                }
                setText(item);
                setTextFill(Color.web(UI.getBlack()));
                setStyle("-fx-alignment: CENTER;");
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                if (!isEmpty() && getItem() != null) {
                    setTextFill(Color.web(UI.getBlack()));
                }
            }
        });
        return column;
    }
    private static TableColumn<AdminMovieRow, String> movieStatusColumn(String title, double widthWeight, Callback<AdminMovieRow, StringProperty> propertyAccessor) {
        TableColumn<AdminMovieRow, String> column = Table.createTextColumn(title, widthWeight, propertyAccessor);
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                    return;
                }
                setText(item);
                setTextFill(Movie.STATUS_DELETED.equals(item) ? Color.web(UI.getDanger()) : Color.web(UI.getSuccess()));
                setStyle("-fx-font-weight: bold; -fx-alignment: CENTER;");
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                if (!isEmpty() && getItem() != null) {
                    setTextFill(Movie.STATUS_DELETED.equals(getItem()) ? Color.web(UI.getDanger()) : Color.web(UI.getSuccess()));
                }
            }
        });
        return column;
    }
    private static  <T> TableColumn<T, String> statusColumn(String title, double widthWeight, Callback<T, StringProperty> propertyAccessor) {
        TableColumn<T, String> column = Table.createTextColumn(title, widthWeight, propertyAccessor);
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                    return;
                }
                setText(item);
                setTextFill("Expired".equals(item) ? Color.web(UI.getDanger()) : Color.web(UI.getSuccess()));
                setStyle("-fx-font-weight: bold; -fx-alignment: CENTER;");
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                if (!isEmpty() && getItem() != null) {
                    setTextFill("Expired".equals(getItem()) ? Color.web(UI.getDanger()) : Color.web(UI.getSuccess()));
                }
            }
        });
        return column;
    }

    private static TableColumn<CustomerRow, String> customerStatusColumn(String title, double widthWeight, Callback<CustomerRow, StringProperty> propertyAccessor) {
        TableColumn<CustomerRow, String> column = Table.createTextColumn(title, widthWeight, propertyAccessor);
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                    return;
                }
                setText(item);
                setTextFill("Blocked".equals(item) ? Color.web(UI.getDanger()) : Color.web(UI.getSuccess()));
                setStyle("-fx-font-weight: bold; -fx-alignment: CENTER;");
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                if (!isEmpty() && getItem() != null) {
                    setTextFill("Blocked".equals(getItem()) ? Color.web(UI.getDanger()) : Color.web(UI.getSuccess()));
                }
            }
        });
        return column;
    }

//    private static TableColumn<CustomerRow, Void> customerActionColumn(UserDAO userDAO) {
//        TableColumn<CustomerRow, Void> actionColumn = new TableColumn<>("Action");
//        actionColumn.setSortable(false);
//        actionColumn.setResizable(false);
//        actionColumn.setReorderable(false);
//        actionColumn.setPrefWidth(140);
//
//        actionColumn.setCellFactory(column -> new TableCell<>() {
//            final Button actionButton = new Button();
//
//            {
//                actionButton.setFont(Font.font("Bahnschrift", FontWeight.BOLD, 14));
//                UI.applyActionButtonStyle(actionButton);
//
//                actionButton.setOnAction(event -> {
//                    CustomerRow row = getTableView().getItems().get(getIndex());
//
//                    boolean isBlocked = "Blocked".equals(row.getBlocked());
//                    String newStatus = isBlocked ? "Active" : "Blocked";
//
//                    row.setBlocked(newStatus);
//                    actionButton.setText("Blocked".equals(newStatus) ? "Unblock" : "Block");
//
//                    if (userDAO != null) {
//                        userDAO.updateUserStatus(row.getId(), newStatus);                    }
//
//                    getTableView().refresh();
//                });
//            }
//
//            @Override
//            protected void updateItem(Void item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || getIndex() >= getTableView().getItems().size()) {
//                    setGraphic(null);
//                    return;
//                }
//                CustomerRow row = getTableView().getItems().get(getIndex());
//                actionButton.setText("Blocked".equals(row.getBlocked()) ? "Unblock" : "Block");
//                setGraphic(actionButton);
//            }
//        });
//        return actionColumn;
//    }
    private static TableColumn<CustomerRow, Void> customerActionColumn(UserDAO userDAO) {
        TableColumn<CustomerRow, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setSortable(false);
        actionColumn.setResizable(false);
        actionColumn.setReorderable(false);
        actionColumn.setPrefWidth(140);

        actionColumn.setCellFactory(column -> new TableCell<>() {
            final Button actionButton = new Button();

            {
                actionButton.setFont(Font.font("Bahnschrift", FontWeight.BOLD, 14));
                UI.applyActionButtonStyle(actionButton);

                actionButton.setOnAction(event -> {
                    CustomerRow row = getTableView().getItems().get(getIndex());
                    boolean isBlocked = "Blocked".equals(row.getBlocked());
                    String newStatus = isBlocked ? "Active" : "Blocked";

                    if (userDAO != null && userDAO.updateUserStatus(row.getId(), newStatus)) {
                        row.setBlocked(newStatus);
                        actionButton.setText("Blocked".equals(newStatus) ? "Unblock" : "Block");
                        getTableView().refresh();
                        System.out.println("User " + row.getId() + " status updated to " + newStatus);
                    } else {
                        System.out.println("Failed to update user " + row.getId());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }

                CustomerRow row = getTableView().getItems().get(getIndex());
                actionButton.setText("Blocked".equals(row.getBlocked()) ? "Unblock" : "Block");
                setGraphic(actionButton);
            }
        });

        return actionColumn;
    }


                private static void bindingButtonStates(Button button, String baseStyle, String hoverStyle, String pressedStyle) {
        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(button.isPressed() ? pressedStyle : hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        button.setOnMousePressed(e -> button.setStyle(pressedStyle));
        button.setOnMouseReleased(e -> button.setStyle(button.isHover() ? hoverStyle : baseStyle));
    }
    private <T> TableView<T> createTable(ObservableList<T> items, String placeholder) {
        TableView<T> table = new TableView<>(items);
        table.setPlaceholder(new Label(placeholder));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private <T> void addTextColumn(TableView<T> table, String title, java.util.function.Function<T, Object> mapper) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> {
            Object value = mapper.apply(cellData.getValue());
            return new javafx.beans.property.SimpleStringProperty(value == null ? "" : value.toString());
        });
        table.getColumns().add(column);
    }

    private <T> void addIndexColumn(TableView<T> table) {
        TableColumn<T, String> indexCol = new TableColumn<>("#");
        indexCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(String.valueOf(getIndex() + 1));
            }
        });
        indexCol.setPrefWidth(40);
        table.getColumns().add(indexCol);
    }
    private static void tableHeaders(TableView<?> table) {
        Platform.runLater(() -> {
            for (javafx.scene.Node header : table.lookupAll(".column-header")) {
                header.setStyle("-fx-background-color: #111111; -fx-border-color: " + UI.getBorderColor() + ";");
            }
            for (javafx.scene.Node label : table.lookupAll(".column-header .label")) {
                label.setStyle("-fx-text-fill: " + UI.getPrimary() + "; -fx-font-weight: 800;");
            }
        });
    }
    private static <T> void comboTextFix(ComboBox<T> comboBox) {
        String baseStyle = UI.getComboStyle();
        String hoverStyle = "-fx-background-color: #2a2a2a; -fx-border-color: " + UI.getPrimary() + "; -fx-border-radius: 12; " +
                "-fx-background-radius: 12; -fx-padding: 4; -fx-font-size: 14px; -fx-text-fill: " + UI.getLightText() + ";";
        String pressedStyle = "-fx-background-color: #1f1f1f; -fx-border-color: " + UI.getPrimary() + "; -fx-border-radius: 12; " +
                "-fx-background-radius: 12; -fx-padding: 4; -fx-font-size: 14px; -fx-text-fill: " + UI.getLightText() + ";";
        bindComboStates(comboBox, baseStyle, hoverStyle, pressedStyle);
        comboBox.setButtonCell(new javafx.scene.control.ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(comboBox.getPromptText());
                    setStyle("-fx-text-fill: " + UI.getLightText() + "; -fx-font-size: 14px;");
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: " + UI.getLightText() + ";");
                }
            }
        });
        comboBox.setCellFactory(listView -> new javafx.scene.control.ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
                if (empty || item == null) {
                    setStyle("-fx-background-color: #222222; -fx-text-fill: " + UI.getLightText() + "; -fx-font-size: 14px;");
                } else if (isHover()) {
                    setStyle("-fx-background-color: #2a2a2a; -fx-text-fill: " + UI.getPrimary() + "; -fx-font-size: 14px;");
                } else {
                    setStyle("-fx-background-color: #222222; -fx-text-fill: " + UI.getLightText() + "; -fx-font-size: 14px;");
                }
            }
            {
                hoverProperty().addListener((obs, oldValue, hovered) -> {
                    if (!isEmpty()) {
                        setStyle(hovered
                                ? "-fx-background-color: #2a2a2a; -fx-text-fill: " + UI.getPrimary() + "; -fx-font-size: 14px;"
                                : "-fx-background-color: #222222; -fx-text-fill: " + UI.getLightText() + "; -fx-font-size: 14px;");
                    }
                });
            }
        });
    }
    private static  <T> void bindComboStates(ComboBox<T> comboBox, String baseStyle, String hoverStyle, String pressedStyle) {
        comboBox.setStyle(baseStyle);
        comboBox.setOnMouseEntered(e -> comboBox.setStyle(comboBox.isShowing() ? pressedStyle : hoverStyle));
        comboBox.setOnMouseExited(e -> comboBox.setStyle(comboBox.isShowing() ? pressedStyle : baseStyle));
        comboBox.showingProperty().addListener((obs, oldValue, showing) ->
                comboBox.setStyle(showing ? pressedStyle : (comboBox.isHover() ? hoverStyle : baseStyle)));
        comboBox.focusedProperty().addListener((obs, oldValue, focused) -> {
            if (!comboBox.isShowing()) {
                comboBox.setStyle(focused || comboBox.isHover() ? hoverStyle : baseStyle);
            }
        });
    }

    public static void applyTableHeaderStyle(TableView<?> table) {tableHeaderStyle(table);}
    public static <T> TableView<T> createStyledTable(ObservableList<T> data, double prefHeight, double maxWidth) {return styledTable(data, prefHeight, maxWidth);}
    public static TableColumn<AdminMovieRow, Void> createMovieActionColumn(MovieDAO  movieDAO) {return movieActionColumn(movieDAO);}
    public static TableColumn<SessionRow, Void> createSessionActionColumn( SessionDAO sessionDAO, String text) {return sessionActionColumn(sessionDAO, text);}
    public static <T> TableColumn<T, String> createTextColumn(String title, double widthWeight, Callback<T, StringProperty> propertyAccessor) {return textColumn(title, widthWeight, propertyAccessor);}
    public static TableColumn<AdminMovieRow, String> createMovieStatusColumn(String title, double widthWeight, Callback<AdminMovieRow, StringProperty> propertyAccessor) {return movieStatusColumn(title, widthWeight, propertyAccessor);}
    public static  <T> TableColumn<T, String> createStatusColumn(String title, double widthWeight, Callback<T, StringProperty> propertyAccessor) {return statusColumn(title, widthWeight, propertyAccessor);}
    public static TableColumn<CustomerRow, String> createCustomerStatusColumn(String title, double widthWeight, Callback<CustomerRow, StringProperty> propertyAccessor) {return customerStatusColumn(title, widthWeight, propertyAccessor);}
    public static TableColumn<CustomerRow, Void> createCustomerActionColumn(UserDAO userDAO) {return customerActionColumn(userDAO);}
    public static void bindButtonStates(Button button, String baseStyle, String hoverStyle, String pressedStyle) {bindingButtonStates(button, baseStyle, hoverStyle, pressedStyle);}
    public static void styleTableHeaders(TableView<?> table) {tableHeaders(table);}
    public static  <T> void applyComboTextFix(ComboBox<T> comboBox) {comboTextFix(comboBox);}
}

