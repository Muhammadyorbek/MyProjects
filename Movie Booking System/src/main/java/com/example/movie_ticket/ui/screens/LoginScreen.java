package com.example.movie_ticket.ui.screens;

import com.example.movie_ticket.database.UserDAO;
import com.example.movie_ticket.model.Admin;
import com.example.movie_ticket.model.Customer;
import com.example.movie_ticket.model.FrontDeskOfficer;
import com.example.movie_ticket.ui.screens.admin.AdminScreen;
import com.example.movie_ticket.ui.screens.admin.MovieBookingApp;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class LoginScreen extends Application {
    Admin admin;
    Customer customer;
    FrontDeskOfficer FDOfficer;
    AdminScreen adminScreen;
    RegisterScreen registerScreen;
    FrontDeskOfficerScreen officerScreen;
    Stage loginStage;
    StackPane root = new StackPane();
    Scene loginScene = new Scene(root, UI.sceneWidth(), UI.sceneHeight());
    MovieBookingApp movieBookingApp;
    public void createScene(Stage stage) {
        TextField loginField = UI.createTextField();
        PasswordField passwordField = UI.createPasswordField();

        Label welcomeLabel = new Label("Welcome! ");
        Label loginLabel = UI.createLabel("Login: ", UI.getLightText());
        Label passwordLabel = UI.createLabel("Password: ", UI.getLightText());
        Label errorLabel = UI.createLabel("", UI.getDanger());
        welcomeLabel.setFont(new Font("Bahnschrift", 25));
        welcomeLabel.setStyle("-fx-text-fill: " + UI.getYellow() + ";");

        Button loginButton = UI.createCustomButton1("Sign in");
        Button registerButton = new Button("Sign up");
        registerButton.setFont(Font.font("Bahnschrift", FontWeight.BOLD, 15));
        String secondaryButtonStyle =
                "-fx-border-color: " + UI.getYellow() + ";" +
                "-fx-border-radius: 15;" +
                "-fx-background-radius: 15;" +
                "-fx-text-fill: " + UI.getYellow() + ";" +
                "-fx-background-color: transparent;";
        String secondaryButtonHoverStyle =
                "-fx-border-color: " + UI.getYellow() + ";" +
                "-fx-border-radius: 15;" +
                "-fx-background-radius: 15;" +
                "-fx-text-fill: " + UI.getBlack() + ";" +
                "-fx-background-color: " + UI.getYellow() + ";";
        registerButton.setStyle(secondaryButtonStyle);
        registerButton.setAlignment(Pos.CENTER_LEFT);

        GridPane buttonGrid = new GridPane(3, 3);
        buttonGrid.add(registerButton, 2, 1);
        buttonGrid.add(loginButton, 1, 2);
        buttonGrid.setHgap(50);

        VBox labelVbox = new VBox(10, loginLabel, passwordLabel);
        VBox fieldVbox = new VBox(10, loginField, passwordField);
        HBox hbox = new HBox(10, labelVbox, fieldVbox);
        hbox.setFillHeight(false);
        hbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        VBox leftSide = new VBox(20, welcomeLabel, hbox, buttonGrid, errorLabel);
        leftSide.setAlignment(Pos.TOP_CENTER);
        leftSide.setFillWidth(false);
        leftSide.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        leftSide.setStyle(
                "-fx-background-color: " + UI.getDarkGray() + ";" +
                "-fx-border-color: " + UI.getBorderColor() + ";" +
                "-fx-border-radius: 24;" +
                "-fx-background-radius: 24;"
        );
        leftSide.setPadding(new Insets(50, 50, 50, 50));

        ImageView backgroundImageView = UI.createImageView();
        BorderPane content = new BorderPane();
        content.setLeft(leftSide);
        BorderPane.setAlignment(leftSide, Pos.CENTER_LEFT);
        BorderPane.setMargin(leftSide, new Insets(20, 40, 40, 20));
        content.setRight(backgroundImageView);
        BorderPane.setAlignment(backgroundImageView, Pos.CENTER_RIGHT);
        BorderPane.setMargin(backgroundImageView, new Insets(20, 40, 40, 20));
        content.setPadding(new Insets(20));

        loginButton.setOnAction(event -> {
                    String login = loginField.getText();
                    String password = passwordField.getText();

                    if (login.isEmpty() || password.isEmpty()) {
                        errorLabel.setText("Please enter login or password");
                        errorLabel.setFont(new Font("Bahnschrift", 13));
                        return;
                    }

                    if (login.equals("Admin") && password.equals("Admin123")) {
                        errorLabel.setText("");
                        adminScreen = new AdminScreen();
                        try {
                            adminScreen.start(loginStage);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else if (login.equals("FrontDeskOfficer") && password.equals("FrontDeskOfficer123")) {
                        errorLabel.setText("");
                        officerScreen = new FrontDeskOfficerScreen();
                        try {
                            officerScreen.start(loginStage);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    else {
                        UserDAO userDAO = new UserDAO();
                        String status = userDAO.getStatusFromDB(login, password);
                        if ("Blocked".equals(status)) {
                            errorLabel.setText("Your account is blocked");
                            return;
                        }

                        if (status != null) {
                            errorLabel.setText("");

                            userDAO.loadUserDataToSession(login);

                            javafx.application.Platform.runLater(() -> {
                                try {
                                    movieBookingApp = new MovieBookingApp();
                                    movieBookingApp.start(loginStage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                        } else {
                            errorLabel.setText("Invalid login or password");
                        }
                    }
                });

        UI.setHoverButtonStyle(registerButton, secondaryButtonHoverStyle, secondaryButtonStyle);
        registerButton.setOnAction(event -> {
            registerScreen = new RegisterScreen();
            try {
                registerScreen.start(loginStage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        loginScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                movieBookingApp =  new MovieBookingApp();
                try {
                    movieBookingApp.start(loginStage);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        BackgroundFill backgroundFill = new BackgroundFill(javafx.scene.paint.Color.web(UI.getBlack()), CornerRadii.EMPTY, Insets.EMPTY);
        root.getChildren().add(content);
        root.setBackground(new Background(backgroundFill));
        root.setPadding(new Insets(20));
    }
    @Override
    public void start(Stage stage) {
        this.loginStage = stage;
        createScene(stage);
        stage.setTitle("Login");
        stage.setScene(loginScene);
        stage.show();
    }
}
