package com.example.movie_ticket.ui.screens;

import com.example.movie_ticket.database.UserDAO;
import com.example.movie_ticket.model.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class RegisterScreen extends Application {
    Person person;
    LoginScreen loginScreen;
    Stage registerStage;
    StackPane root;

    @Override
    public void start(Stage stage) throws Exception {
        this.registerStage = stage;
        TextField nameField = UI.createTextField();
        TextField surnameField = UI.createTextField();
        TextField loginField = UI.createTextField();
        PasswordField passwordField = UI.createPasswordField();
        PasswordField verifyField = UI.createPasswordField();
        TextField phoneField = UI.createTextField();
        TextField emailField = UI.createTextField();

        Label welcomeLabel = new Label("Welcome!");
        Label nameLabel = UI.createLabel("Name: ", UI.getLightText());
        Label surnameLabel = UI.createLabel("Surname: ", UI.getLightText());
        Label loginLabel = UI.createLabel("Login: ", UI.getLightText());
        Label passwordLabel = UI.createLabel("Password: ", UI.getLightText());
        Label verifyLabel = UI.createLabel("Verify: ", UI.getLightText());
        Label emailLabel = UI.createLabel("Email: ", UI.getLightText());
        Label phoneLabel = UI.createLabel("Phone: ", UI.getLightText());
        Label errorLabel = new Label("");
        errorLabel.setPrefWidth(500);
        welcomeLabel.setFont(new Font("Bahnschrift", 25));
        errorLabel.setFont(new Font("Bahnschrift", 20));
        welcomeLabel.setStyle("-fx-text-fill: " + UI.getYellow() + ";");
        errorLabel.setStyle("-fx-text-fill: " + UI.getDanger() + ";");

        CheckBox box = new CheckBox("By checking this box you agree to our Terms of Service");
        box.setFont(new Font("Bahnschrift", 10));
        box.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + UI.getLightText() + ";" +
                "-fx-font-size: 13px;" +
                "-fx-border-color: " + UI.getBorderColor() + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8;"
        );

        Button registerButton = UI.createCustomButton1("Sign up");

        VBox labelVbox = new VBox(18, nameLabel, surnameLabel, loginLabel, passwordLabel, verifyLabel, phoneLabel, emailLabel);
        VBox fieldVbox = new VBox(20, nameField, surnameField, loginField, passwordField, verifyField, phoneField, emailField);
        fieldVbox.setPadding(new Insets(10));
        HBox hbox = new HBox(10, labelVbox, fieldVbox);
        hbox.setFillHeight(false);
        hbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        VBox leftVbox = new VBox(20, welcomeLabel, hbox, box, registerButton, errorLabel);
        VBox.setMargin(errorLabel, new Insets(0, 0, 0, 30));
        leftVbox.setFillWidth(false);
        leftVbox.setAlignment(Pos.CENTER);
        leftVbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        leftVbox.setStyle(
                "-fx-background-color: " + UI.getDarkGray() + ";" +
                "-fx-border-color: " + UI.getBorderColor() + ";" +
                "-fx-border-radius: 24;" +
                "-fx-background-radius: 24;"
        );
        leftVbox.setPadding(new Insets(30));

        ImageView rightImage = UI.createImageView();
        BorderPane content = new BorderPane();
        content.setLeft(leftVbox);
        BorderPane.setAlignment(leftVbox, Pos.CENTER_LEFT);
        BorderPane.setMargin(leftVbox, new Insets(30, 30, 30, 30));
        content.setRight(rightImage);
        BorderPane.setAlignment(rightImage, Pos.CENTER_RIGHT);
        BorderPane.setMargin(rightImage, new Insets(20, 40, 40, 20));
        content.setPadding(new Insets(20));

        registerButton.setOnAction(event -> {
            String name = nameField.getText();
            String surname = surnameField.getText();
            String email = emailField.getText();
            String login = loginField.getText();
            String password = passwordField.getText();
            String phone = phoneField.getText();
            if (name.isEmpty()) {errorLabel.setText("Please enter a name!");}
            else if (surname.isEmpty()) {errorLabel.setText("Please enter a surname!");}
            else if (login.isEmpty()) {errorLabel.setText("Please enter a login!");}
            else if (password.isEmpty()) {errorLabel.setText("Please enter a password!");}
            else if (email.isEmpty()) {errorLabel.setText("Please enter an email!");}
            else if (phone.isEmpty()) {errorLabel.setText("Please enter your phone");}
            else if (!verifyField.getText().equals(passwordField.getText())) errorLabel.setText("Wrong verification of the password!");
            else if (!box.isSelected()) errorLabel.setText("Check the box");
            else if (!checkName(name)) {errorLabel.setText("Name should not contain a number!");}
            else if (!checkEmail(email)) {errorLabel.setText("Email should contain @ and .");}
            else if (!checkPhone(phone)) {errorLabel.setText("Phone number with +");}
            else if (!checkPassword(password)) {errorLabel.setText("Password must be secure!");}
            else {

                    UserDAO userDAO = new UserDAO();

                    boolean success = userDAO.registerCustomer(name, surname, login, password, email, phone);

                    if (success) {
                        loginScreen = new LoginScreen();
                        try {
                            loginScreen.start(registerStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        errorLabel.setText("Registration failed! Login or Email already exists.");
                    }
                }
        });

        root = new StackPane(content);
        BackgroundFill backgroundFill = new BackgroundFill(javafx.scene.paint.Color.web(UI.getBlack()), CornerRadii.EMPTY, Insets.EMPTY);
        root.setBackground(new Background(backgroundFill));
        root.setPadding(new Insets(20));

        Scene registerScene = new Scene(root, UI.sceneWidth(), UI.sceneHeight());

        registerScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                loginScreen = new LoginScreen();
                loginScreen.start(registerStage);
            }
        });

        stage.setTitle("Register");
        stage.setScene(registerScene);
        stage.show();

        Region checkboxBox = (Region) box.lookup(".box");
        checkboxBox.setStyle(
                "-fx-background-color: " + UI.getPanelGray() + ";" +
                        "-fx-border-color: " + UI.getYellow() + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;"
        );

    }
    public boolean checkPassword(String password) {
        boolean validLength = password.length() >= 8;
        boolean containUpperLowerCase = password.matches(".*[A-Z].*") && password.matches(".*[a-z].*");
        boolean containDigits = password.matches(".*\\d.*");
        boolean containSpecial = password.matches(".*[^a-zA-Z0-9].*");
        if (!validLength) return false;
        else if (!containUpperLowerCase) return false;
        else if (!containDigits) return false;
        else return containSpecial;
    }
    public boolean checkName(String name) {return !name.matches(".*\\d.*");}
    public boolean checkEmail(String email) {return email.contains("@") && email.contains(".");}
    public boolean checkPhone(String phone) {return phone.matches("^\\+\\d+$") && phone.length() >= 11;}
 }
