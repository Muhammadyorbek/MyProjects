package com.example.movie_ticket.ui.screens;

import com.example.movie_ticket.database.MovieDAO;
import com.example.movie_ticket.database.SessionDAO;
import com.example.movie_ticket.model.*;
import com.example.movie_ticket.ui.ItemManagementWindow;
import com.example.movie_ticket.ui.screens.admin.AdminMethod;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import com.example.movie_ticket.model.AdminMovieRow;

import java.util.List;

public class FrontDeskOfficerScreen extends Application {


    AdminMethod adminPanel;
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, UI.sceneWidth(), UI.sceneHeight());
    Stage frontDeskOfficerStage;
    LoginScreen loginScreen;
    Stage shopStage;

    final ObservableList<AdminMovieRow> movieData = FXCollections.observableArrayList();

    final MovieDAO movieDAO = new MovieDAO();

    final SessionDAO sessionDAO = new SessionDAO();

    final ObservableList<SessionRow> sessionData = FXCollections.observableArrayList();


    @Override
    public void start(Stage stage) {
        frontDeskOfficerStage = stage;
        createScene(stage);
        stage.setTitle("Front Desk Officer Panel");
        stage.setScene(scene);
        stage.show();

        List<AdminMovieRow> listFromDb = movieDAO.getAllAdminMovies();
        movieData.addAll(listFromDb);

        List<SessionRow> sessionfromdb = sessionDAO.GetAllSessions();
        sessionData.addAll(sessionfromdb);
    }

    public void sessionScene(BorderPane root) {
        Label moviesLabel = new Label("Sessions");
        moviesLabel.setFont(Font.font("Bahnschrift", FontWeight.BOLD, 35));
        moviesLabel.setTextFill(Color.web(UI.getLightText()));

        TableView<SessionRow> sessionTable = Table.createStyledTable(sessionData, 320, 920);
        sessionTable.getColumns().addAll(
                Table.createTextColumn("show_id", 1.0, SessionRow::show_idProperty),
                Table.createTextColumn("movie_id", 1.0, SessionRow::movieTitleProperty),
                Table.createTextColumn("Cinema", 1.5, SessionRow::cinemaProperty),
                Table.createTextColumn("Hall", 1.2, SessionRow::hallProperty),
                Table.createTextColumn("Showtime", 1.2, SessionRow::showtimeProperty),
                Table.createSessionActionColumn(this.sessionDAO, "Cancel")
        );
        sessionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        sessionTable.setFixedCellSize(54);
        sessionTable.setPrefHeight(280);
        sessionTable.setMaxWidth(980);
        sessionTable.setStyle(
                "-fx-background-color: " + UI.getPanelGray() + ";" +
                        "-fx-control-inner-background: " + UI.getPanelGray() + ";" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-radius: 18;" +
                        "-fx-border-color: " + UI.getBorderColor() + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-padding: 8;" +
                        "-fx-table-cell-border-color: transparent;"
        );
        sessionTable.skinProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                sessionTable.lookupAll(".column-header-background").forEach(node ->
                        node.setStyle("-fx-background-color:"+ UI.getBlack() +"; -fx-background-radius: 14 14 0 0;")
                );
                sessionTable.lookupAll(".column-header .label").forEach(node ->
                        node.setStyle("-fx-text-fill: "+ UI.getBlack() + "; -fx-font-weight: bold;")
                );
            }
        });

        Button addSessionButton = UI.createSectionAddButton();
        addSessionButton.setOnAction(e -> {
            adminPanel = new AdminMethod();
            adminPanel.showMovieManager(frontDeskOfficerStage, Role.FRONT_DESK_OFFICER);
        });

        VBox centerBar = new VBox(16, moviesLabel, sessionTable, addSessionButton);
        centerBar.setPadding(new Insets(30, 40, 30, 90));

        root.setCenter(centerBar);
    }

    public void createScene(Stage stage) {
        Label officerPanelLabel = UI.createTitle("Front Desk Officer Panel");

        Button sessionButton = UI.createLeftButton("Session");
        Button moviesButton = UI.createLeftButton("Movies");
        Button shopButton = UI.createLeftButton("Add Shop Item");
        Button exitButton = UI.createExitButton();

        moviesButton.setOnAction(actionEvent -> moviesScene(root));

        sessionButton.setOnAction(e -> sessionScene(root));

        shopButton.setOnAction(e -> {
            if (shopStage == null || !shopStage.isShowing()) {
                shopStage = shopScene();
                shopStage.setOnHidden(event -> shopStage = null);
            } else {
                shopStage.toFront();
                shopStage.requestFocus();
            }
        });

        exitButton.setOnAction(event -> {
            loginScreen = new LoginScreen();
            loginScreen.start(frontDeskOfficerStage);
        });


        Background backgroundLeft = new Background(new BackgroundFill(Color.web(UI.getPanelDark()), CornerRadii.EMPTY, Insets.EMPTY));

        BorderPane topBar = new BorderPane();
        topBar.setCenter(officerPanelLabel);
        topBar.setRight(exitButton);
        topBar.setStyle("-fx-border-color: " + UI.getBorderColor() + "; -fx-border-width: 0 0 1 0; -fx-background-color:" + UI.getDarkGray() + " ;");
        topBar.setPadding(new Insets(10));

        VBox leftBar = new VBox(sessionButton, moviesButton, shopButton);
        leftBar.setBackground(backgroundLeft);
        leftBar.setStyle("-fx-pref-width: 200");
        VBox.setMargin(moviesButton, new Insets(10));
        VBox.setMargin(sessionButton, new Insets(10));
        VBox.setMargin(shopButton, new Insets(10));

        root.setBackground(new Background(new BackgroundFill(Color.web(UI.getDarkGray()), CornerRadii.EMPTY, Insets.EMPTY)));
        root.setTop(topBar);
        root.setLeft(leftBar);
        sessionScene(root);
    }

    public void moviesScene(BorderPane root) {
        Label moviesLabel = UI.createSectionTitle("Movies");

        TableView<AdminMovieRow> moviesTable = Table.createStyledTable(movieData, 320, 920);
        moviesTable.getColumns().addAll(
                Table.createTextColumn("ID", 1.0, AdminMovieRow::idProperty),
                Table.createTextColumn("Name", 1.8, AdminMovieRow::nameProperty),
                Table.createTextColumn("Registered Date", 1.5, AdminMovieRow::registeredDateProperty),
                Table.createMovieStatusColumn("Status", 1.1, AdminMovieRow::statusProperty),
                Table.createMovieActionColumn(this.movieDAO)
        );

        Button addMovieButton = UI.createSectionAddButton();
        addMovieButton.setText("Add");

        addMovieButton.setOnAction(e -> {
            adminPanel = new AdminMethod();
            adminPanel.AddMovieManager(frontDeskOfficerStage);
        });

        VBox centerBar = new VBox(16, moviesLabel, moviesTable, addMovieButton);
        centerBar.setPadding(new Insets(30, 40, 30, 50));

        ScrollPane movieScrollPane = UI.createScrollPane(centerBar);
        root.setCenter(movieScrollPane);
    }
    public Stage shopScene() {
        ItemManagementWindow shop = new ItemManagementWindow();
        return shop.show();
    }



    public static class TableRowStyled extends javafx.scene.control.TableRow<MovieTableRow> {
        @Override
        protected void updateItem(MovieTableRow item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setStyle(null);
                return;
            }

            if (getIndex() % 2 == 0) {
                setStyle("-fx-background-color: " + UI.getDark() + "; -fx-text-background-color: " + UI.getLightText() + ";");
            } else {
                setStyle("-fx-background-color: " + UI.getDarkColumn() + "; -fx-text-background-color: " + UI.getLightText() + ";");
            }
        }
    }
}
