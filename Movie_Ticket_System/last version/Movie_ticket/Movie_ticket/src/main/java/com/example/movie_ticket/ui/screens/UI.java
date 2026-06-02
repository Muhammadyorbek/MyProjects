package com.example.movie_ticket.ui.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UI {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;

    public static final String MUTED = "#888888";
    private static final String PRIMARY_HOVER = "#e6b800";
    private static final String PRIMARY = "#FFCC00";

    private static final String BLACK = "#121212";
    private static final String DARK = "#1F1F1F";
    private static final String DARK_COLUMN = "#272727";
    private static final String YELLOW = "#FFD700";
    private static final String DARK_GRAY = "#1E1E1E";
    private static final String PANEL_GRAY = "#242424";
    private static final String PANEL_DARK = "#181818";
    private static final String LIGHT_TEXT = "#F4F4F4";
    private static final String MUTED_TEXT = "#A6A6A6";
    private static final String BORDER_COLOR = "#333333";
    private static final String SUCCESS = "#86EFAC";
    private static final String DANGER = "#FF7B72";
    private static final String YELLOW_HOVER = "#FFE55C";
    private static final String LINEAR_GRADIENT = "-fx-background-color: linear-gradient(to bottom, #111111, #151515 30%, #1C1C1C);";

    static Stop[] stops = new Stop[] {
            new Stop(0.0, Color.web("#4A4A4A")),
            new Stop(0.3, Color.web("#3A3A3A")),
            new Stop(0.6, Color.web("#2F2F2F")),
            new Stop(1.0, Color.web("#242424"))
    };
    public static LinearGradient gradient1 = new LinearGradient(
            0,
            Math.sin(Math.toRadians(45)),
            Math.cos(Math.toRadians(45)),
            0,
            true,
            CycleMethod.NO_CYCLE,
            stops
    );

    private static void hoverButton(Button button, String hoverStyle, String defaultStyle) {
        button.setOnMouseEntered(event -> {
            button.setStyle(hoverStyle);
        });
        button.setOnMouseExited(event -> {
            button.setStyle(defaultStyle);
        });
    }
    private static void hoverButton(Button button, String hoverStyle, String defaultStyle, ImageView hoverView, ImageView defaultView) {
        button.setOnMouseEntered(event -> {
            button.setStyle(hoverStyle);
            button.setGraphic(hoverView);
        });
        button.setOnMouseExited(event -> {
            button.setStyle(defaultStyle);
            button.setGraphic(defaultView);
        });
    }
    private static Button leftButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Bahnschrift", FontWeight.BOLD, 20));
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(46);
        String defaultStyle =
                "-fx-background-color: " + UI.PANEL_GRAY + ";" +
                        "-fx-text-fill: " + UI.LIGHT_TEXT + ";" +
                        "-fx-border-color: " + UI.BORDER_COLOR + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;";
        String hoverStyle =
                "-fx-background-color: " + UI.YELLOW + ";" +
                        "-fx-text-fill: " + UI.BLACK + ";" +
                        "-fx-border-color: " + UI.YELLOW + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;";
        button.setStyle(defaultStyle);
        UI.hoverButton(button, hoverStyle, defaultStyle);
        return button;
    }
    private static Button exitButton() {
        Button button = new Button("Exit");
        button.setFont(Font.font("Bahnschrift", FontWeight.BOLD, 18));
        String defaultStyle =
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + UI.YELLOW + ";" +
                        "-fx-border-color: " + UI.YELLOW + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 16 8 16;";
        String hoverStyle =
                "-fx-background-color: " + UI.YELLOW + ";" +
                        "-fx-text-fill: " + UI.BLACK + ";" +
                        "-fx-border-color: " + UI.YELLOW + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 16 8 16;";
        Image exitIcon = new Image(UI.class.getResource("/images/exitIcon.png").toExternalForm());
        ImageView exitIconView = new ImageView(exitIcon);
        Image exitIconHover = new Image(UI.class.getResource("/images/exitIconHover.png").toExternalForm());
        ImageView exitIconHoverView = new ImageView(exitIconHover);
        button.setGraphic(exitIconView);
        button.setStyle(defaultStyle);
        UI.hoverButton(button, hoverStyle, defaultStyle, exitIconHoverView, exitIconView);
        return button;
    }
    private static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Bahnschrift", FontWeight.BOLD, 35));
        label.setTextFill(Color.web(UI.YELLOW));
        return label;
    }
    private static void actionButtonStyle(Button button) {
        String defaultStyle =
                "-fx-background-color: " + UI.YELLOW + ";" +
                        "-fx-text-fill: " + UI.BLACK + ";" +
                        "-fx-font-family: 'Bahnschrift';" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: " + UI.YELLOW + ";" +
                        "-fx-padding: 8 16 8 16;";
        String hoverStyle =
                "-fx-background-color: #FFE55C;" +
                        "-fx-text-fill: " + UI.BLACK + ";" +
                        "-fx-font-family: 'Bahnschrift';" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: #FFE55C;" +
                        "-fx-padding: 8 16 8 16;";
        button.setStyle(defaultStyle);
        UI.hoverButton(button, hoverStyle, defaultStyle);
    }
    private static Label title(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Bahnschrift", FontWeight.BOLD, 40));
        label.setTextFill(Color.web(UI.YELLOW));
        return label;
    }
    private static ScrollPane scrollPane(VBox content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        return scrollPane;
    }
    private static Button sectionAddButton() {
        Button button = new Button("Add");
        button.setFont(Font.font("Bahnschrift", FontWeight.BOLD, 18));
        button.setPrefWidth(120);
        button.setPrefHeight(42);
        UI.actionButtonStyle(button);
        return button;
    }
    private static Button customButton1(String text) {
        Button button1 = new Button(text);
        button1.setFont(new Font("Bahnschrift", 20));
        String primaryButtonStyle =
                "-fx-border-color: " + UI.YELLOW + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-text-fill: " + UI.BLACK + ";" +
                        "-fx-background-color: " + UI.YELLOW + ";";
        String primaryButtonHoverStyle =
                "-fx-border-color: " + UI.YELLOW_HOVER + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-text-fill: " + UI.BLACK + ";" +
                        "-fx-background-color: " + UI.YELLOW_HOVER + ";";
        button1.setStyle(primaryButtonStyle);
        UI.hoverButton(button1, primaryButtonHoverStyle, primaryButtonStyle);
        return button1;
    }
    private static Button customButton2(String text) {
        Button button1 = new Button(text);
        button1.setFont(new Font("Bahnschrift", 14));
        String primaryButtonStyle =
                "-fx-border-color: " + UI.YELLOW + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-text-fill: " + UI.BLACK + ";" +
                        "-fx-background-color: " + UI.YELLOW + ";";
        String primaryButtonHoverStyle =
                "-fx-border-color: " + UI.YELLOW_HOVER + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-text-fill: " + UI.BLACK + ";" +
                        "-fx-background-color: " + UI.YELLOW_HOVER + ";";
        button1.setStyle(primaryButtonStyle);
        UI.hoverButton(button1, primaryButtonHoverStyle, primaryButtonStyle);
        return button1;
    }
    private static Button dangerButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        String baseStyle = "-fx-background-color: #222222; -fx-text-fill: " + UI.getLightText() + "; -fx-font-size: 14px; " +
                "-fx-font-weight: 700; -fx-padding: 12 20; -fx-background-radius: 18; -fx-cursor: hand; " +
                "-fx-border-color: #7a2f2f; -fx-border-radius: 18; -fx-border-width: 1;";
        String hoverStyle = "-fx-background-color: #2a2a2a; -fx-text-fill: " + UI.getLightText() + "; -fx-font-size: 14px; " +
                "-fx-font-weight: 700; -fx-padding: 12 20; -fx-background-radius: 18; -fx-cursor: hand; " +
                "-fx-border-color: " + UI.getPrimary() + "; -fx-border-radius: 18; -fx-border-width: 1.5; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 204, 0, 0.12), 10, 0.15, 0, 0);";
        String pressedStyle = "-fx-background-color: #1a1a1a; -fx-text-fill: " + UI.getLightText() + "; -fx-font-size: 14px; " +
                "-fx-font-weight: 700; -fx-padding: 13 20 11 20; -fx-background-radius: 18; -fx-cursor: hand; " +
                "-fx-border-color: " + UI.getPrimary() + "; -fx-border-radius: 18; -fx-border-width: 1.5;";
        Table.bindButtonStates(button, baseStyle, hoverStyle, pressedStyle);
        return button;
    }
    private static Button primaryButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        String baseStyle = "-fx-background-color: " + UI.getPrimary() + "; -fx-text-fill: black; -fx-font-size: 14px; " +
                "-fx-font-weight: 700; -fx-padding: 12 20; -fx-background-radius: 18; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: " + UI.getPrimaryHover() + "; -fx-text-fill: black; -fx-font-size: 14px; " +
                "-fx-font-weight: 700; -fx-padding: 12 20; -fx-background-radius: 18; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 204, 0, 0.24), 12, 0.2, 0, 0);";
        String pressedStyle = "-fx-background-color: #c79f00; -fx-text-fill: black; -fx-font-size: 14px; " +
                "-fx-font-weight: 700; -fx-padding: 13 20 11 20; -fx-background-radius: 18; -fx-cursor: hand;";
        Table.bindButtonStates(button, baseStyle, hoverStyle, pressedStyle);
        return button;
    }

    private static Button secondaryButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        String baseStyle = "-fx-background-color: #222222; -fx-text-fill: " + UI.getLightText() + "; -fx-font-size: 14px; " +
                "-fx-font-weight: 700; -fx-padding: 12 20; -fx-background-radius: 18; -fx-cursor: hand; " +
                "-fx-border-color: " + UI.getBlack() + "; -fx-border-radius: 18; -fx-border-width: 1;";
        String hoverStyle = "-fx-background-color: #2a2a2a; -fx-text-fill: " + UI.getLightText() + "; -fx-font-size: 14px; " +
                "-fx-font-weight: 700; -fx-padding: 12 20; -fx-background-radius: 18; -fx-cursor: hand; " +
                "-fx-border-color: " + UI.getPrimary() + "; -fx-border-radius: 18; -fx-border-width: 1.5; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 204, 0, 0.12), 10, 0.15, 0, 0);";
        String pressedStyle = "-fx-background-color: #1a1a1a; -fx-text-fill: " + UI.getLightText() + "; -fx-font-size: 14px; " +
                "-fx-font-weight: 700; -fx-padding: 13 20 11 20; -fx-background-radius: 18; -fx-cursor: hand; " +
                "-fx-border-color: " + UI.getPrimary() + "; -fx-border-radius: 18; -fx-border-width: 1.5;";
        Table.bindButtonStates(button, baseStyle, hoverStyle, pressedStyle);
        return button;
    }
    private static TextField textField() {
        TextField textField = new TextField();
        String inputStyle =
                "-fx-background-color: " + UI.PANEL_GRAY + ";" +
                        "-fx-text-fill: " + UI.LIGHT_TEXT + ";" +
                        "-fx-prompt-text-fill: " + UI.MUTED_TEXT + ";" +
                        "-fx-border-color: " + UI.BORDER_COLOR + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-pref-height: 34;";
        textField.setStyle(inputStyle);
        textField.setFont(new Font("Bahnschrift", 15));
        return textField;
    }
    private static PasswordField passwordField() {
        PasswordField passwordField = new PasswordField();
        String inputStyle =
                "-fx-background-color: " + UI.PANEL_GRAY + ";" +
                        "-fx-text-fill: " + UI.LIGHT_TEXT + ";" +
                        "-fx-prompt-text-fill: " + UI.MUTED_TEXT + ";" +
                        "-fx-border-color: " + UI.BORDER_COLOR + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-pref-height: 34;";
        passwordField.setStyle(inputStyle);
        passwordField.setFont(new Font("Bahnschrift", 15));
        return passwordField;
    }
    private static Label label(String text, String UIColor) {
        Label label = new Label(text);
        label.setFont(new Font("Bahnschrift", 20));
        label.setStyle("-fx-text-fill: " + UIColor + ";");
        VBox.setMargin(label, new Insets(4, 10, 4, 10));
        return label;
    }
    private static Label label1(String text, String UIColor) {
        Label label = new Label(text);
        label.setFont(new Font("Bahnschrift", 10));
        label.setStyle("-fx-text-fill: " + UIColor + ";");
        VBox.setMargin(label, new Insets(4, 10, 4, 10));
        return label;
    }
    private static void labelStatus(Label label, String message, boolean isError) {
        label.setText(message);
        label.setStyle("-fx-text-fill: " + (isError ? "#ff8a80" : UI.getPrimary()) + "; -fx-font-size: 12px; -fx-font-weight: 700;");
    }
    private static ImageView imageView() {
        Image backgroundImage = new Image(UI.class.getResource("/images/backgroundImage.png").toExternalForm());
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.setFitHeight(625);
        return backgroundImageView;
    }
    private static VBox formPane(String sectionTitle, String helperText, Object... nodes) {
        VBox card = new VBox(12);
        card.setPrefWidth(380);
        card.setMaxWidth(380);
        card.setPadding(new Insets(18));
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(UI.getSurfaceStyle());

        Label title = new Label(sectionTitle);
        title.setStyle("-fx-text-fill: " + UI.getLightText() + "; -fx-font-size: 18px; -fx-font-weight: 800;");

        Label helper = new Label(helperText);
        helper.setWrapText(true);
        helper.setStyle("-fx-text-fill: " + UI.getMuted() + "; -fx-font-size: 13px;");

        card.getChildren().addAll(title, helper, createDivider());

        for (Object node : nodes) {
            if (node instanceof Region) {
                Region region = (Region) node;
                region.setMaxWidth(Double.MAX_VALUE);
                card.getChildren().add(region);
            }
        }

        return card;
    }

    private static HBox buttonRow(Button left, Button right) {
        HBox row = new HBox(12, left, right);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        left.setMaxWidth(Double.MAX_VALUE);
        right.setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    private static Region divider() {
        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setStyle("-fx-background-color: " + UI.getPanelDark() + ";");
        return divider;
    }

    private static TextField input(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(UI.getInputStyle());
        return field;
    }

    private static Label statusLabel() {
        Label label = new Label("Fill the fields and choose an item when you want to update or delete.");
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: " + UI.getMuted() + "; -fx-font-size: 12px;");
        return label;
    }
    public static String getBlack() {
        return UI.BLACK;
    }
    public static String getBorderColor() {
        return UI.BORDER_COLOR;
    }
    public static String getPrimary() {
        return UI.PRIMARY;
    }
    public static String getPrimaryHover() {
        return UI.PRIMARY_HOVER;
    }
    public static String getLightText() {
        return UI.LIGHT_TEXT;
    }
    public static String getMuted() {
        return UI.MUTED;
    }
    public static String getPanelDark() {
        return UI.PANEL_DARK;
    }
    public static String getDanger() {
        return UI.DANGER;
    }
    public static String getYellow() {return UI.YELLOW;}
    public static String getDarkGray() {return UI.DARK_GRAY;}
    public static String getPanelGray() {return UI.PANEL_GRAY;}
    public static String getMutedText() {return UI.MUTED_TEXT;}
    public static String getSuccess() {return UI.SUCCESS; }
    public static String getYellowHover() {return UI.YELLOW_HOVER; }
    public static String getDark() {return UI.DARK;}
    public static String getDarkColumn() {return UI.DARK_COLUMN;}
    public static String getLinearGradient() {return UI.LINEAR_GRADIENT; }
    public static void setHoverButtonStyle(Button button, String hoverStyle, String defaultStyle) {UI.hoverButton(button, hoverStyle, defaultStyle);}
    public static Button createLeftButton(String text) {return leftButton(text);}
    public static Button createExitButton() {return exitButton();}
    public static Label createSectionTitle(String text) {return sectionTitle(text);}
    public static void applyActionButtonStyle(Button button) {actionButtonStyle(button);}
    public static Label createTitle(String text) {return title(text);}
    public static ScrollPane createScrollPane(VBox content) {return scrollPane(content);}
    public static Button createSectionAddButton() {return sectionAddButton();}
    public static Button createCustomButton1(String text) {return customButton1(text);}
    public static Button createCustomButton2(String text) {return customButton2(text);}
    public static Button createDangerButton(String text) {return dangerButton(text);}
    public static Button createPrimaryButton(String text) {return primaryButton(text);}
    public static Button createSecondaryButton(String text) {return secondaryButton(text);}
    public static TextField createTextField() {return textField();}
    public static PasswordField createPasswordField() {return passwordField();}
    public static Label createLabel(String text, String UIColor) {return label(text, UIColor);}
    public static Label createLabel1(String text, String UIColor) {return label1(text, UIColor);}
    public static ImageView createImageView() {return imageView();}
    public static void setLabelStatus(Label label, String message, boolean isError) {labelStatus(label, message, isError);}
    public static Region createDivider() {return divider();}
    public static TextField createInput(String prompt) {return input(prompt);}
    public static Label createStatusLabel() {return statusLabel();}
    public static HBox createButtonRow(Button left, Button right) {return buttonRow(left, right);}
    public static VBox createFormPane(String sectionTitle, String helperText, Object... nodes) {return formPane(sectionTitle, helperText, nodes);}

    public static int sceneWidth() {return WIDTH;}
    public static int sceneHeight() {return HEIGHT;}




    private static String pageStyle() {
        return "-fx-background-color: " + UI.BLACK + ";";
    }

    private static String surfaceStyle() {
        return "-fx-background-color: " + UI.BLACK + "; -fx-background-radius: 22; -fx-border-radius: 22; " +
                "-fx-border-color: " + UI.BORDER_COLOR + "; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.35), 24, 0.18, 0, 8);";
    }

    private static String tableStyle() {
        return "-fx-background-color: " + UI.BLACK + "; -fx-control-inner-background: " + UI.BLACK + "; " +
                "-fx-background-radius: 16; -fx-border-color: " + UI.BORDER_COLOR + "; -fx-border-radius: 16; " +
                "-fx-table-cell-border-color: " + UI.PANEL_DARK + "; -fx-padding: 4; -fx-text-background-color: " + UI.LIGHT_TEXT + "; " +
                "-fx-selection-bar: " + UI.PRIMARY + "; -fx-selection-bar-non-focused: " + UI.PRIMARY + "; " +
                "-fx-accent: " + UI.PRIMARY + "; -fx-selection-bar-text: black;";
    }

    private static String inputStyle() {
        return "-fx-background-color: #222222; -fx-text-fill: " + UI.LIGHT_TEXT + "; -fx-prompt-text-fill: " + UI.MUTED + "; " +
                "-fx-border-color: " + UI.BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12; " +
                "-fx-padding: 12 14; -fx-font-size: 14px;";
    }

    private static String textAreaStyle() {
        return inputStyle() +
                " -fx-control-inner-background: #222222; -fx-highlight-fill: " + UI.PRIMARY + "; " +
                "-fx-highlight-text-fill: black;";
    }

    private static String comboStyle() {
        return "-fx-background-color: #222222; -fx-border-color: " + UI.BORDER_COLOR + "; -fx-border-radius: 12; " +
                "-fx-background-radius: 12; -fx-padding: 4; -fx-font-size: 14px; -fx-text-fill: " + UI.LIGHT_TEXT + ";";
    }

    public static String getPageStyle() {
        return pageStyle();
    }

    public static String getSurfaceStyle() {
        return surfaceStyle();
    }

    public static String getTableStyle() {
        return tableStyle();
    }

    public static String getInputStyle() {
        return inputStyle();
    }

    public static String getTextAreaStyle() {
        return textAreaStyle();
    }

    public static String getComboStyle() {
        return comboStyle();
    }
}
// TODO list
//-Hammasini pozitsiyasini boshqatdan qo'yib chiqish kerak \/
//-Videodagiga o'hshab ko'proq funkshinla tiqish kere \/
// AdminScreen Class
//-Places management (city, cinema, hall) \/
//-Moviesni qaytatdan qarab chiqish kere \/
//-Shopni ulash kere Baxadirdan \/
//-Coupon va diskountni jadvalini qilish kere
//-Paymentni customersga qo'yish kere
// FrontDeskOfficer Class
//-sessionslarni qo'shadi(hall, cinema, city, date, showtime, movie) \/
// LoginScreen Class \/
//-password tochka qilamiz \/
// RegisterScreen Class \/
//-password tocjka qilamiz \/
//-passwordni verify qilamiz \/
//-passwordni validitysini tekshiramiz \/
//-phoneni qo'shamiz \/
//-hammasini validitisini tekshiramiz \/
//-Person classda ham parametrlani qo'shib chiqamiz \/
//-checkbox qilib terms of serviceni tekshiramiz \/
