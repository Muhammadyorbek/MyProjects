package com.example.movie_ticket.ui.screens.admin;

import com.example.movie_ticket.database.MovieDAO;
import com.example.movie_ticket.database.SessionDAO;
import com.example.movie_ticket.database.UserDAO;
import com.example.movie_ticket.model.*;
import com.example.movie_ticket.service.CouponService;
import com.example.movie_ticket.ui.CouponManagementWindow;
import com.example.movie_ticket.ui.ItemManagementWindow;
import com.example.movie_ticket.ui.screens.LoginScreen;
import com.example.movie_ticket.ui.screens.Table;
import com.example.movie_ticket.ui.screens.UI;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class AdminScreen extends Application {
    Stage adminStage;
     Stage shopStage;
     final BorderPane root = new BorderPane();
     final Scene scene = new Scene(root, UI.sceneWidth(), UI.sceneHeight());
    LoginScreen loginScreen;
    AdminMethod adminPanel;

    final MovieDAO movieDAO = new MovieDAO();

    final SessionDAO sessionDAO = new SessionDAO();

    final UserDAO userDAO = new UserDAO();

    final ObservableList<AdminMovieRow> movieData = FXCollections.observableArrayList();

    final ObservableList<CouponRow> couponData = FXCollections.observableArrayList();

     final ObservableList<CustomerRow> customerData = FXCollections.observableArrayList();


    final ObservableList<SessionRow> sessionData = FXCollections.observableArrayList();



    @Override
    public void start(Stage stage) {
        createScene(stage);
        stage.setTitle("Admin Panel");
        stage.setScene(scene);
        stage.show();

        List<AdminMovieRow> listFromDb = movieDAO.getAllAdminMovies();
        movieData.addAll(listFromDb);

        List<SessionRow> sessionfromdb = sessionDAO.GetAllSessions();
        sessionData.addAll(sessionfromdb);

        List<CustomerRow> customerfromdb = userDAO.getAllAccounts();
        customerData.addAll(customerfromdb);
    }



    public void createScene(Stage stage) {
        this.adminStage = stage;

        Label adminPanelLabel = UI.createTitle("Admin Panel");

        Button dashboardButton = UI.createLeftButton("Dashboard");
        Button customersButton = UI.createLeftButton("Customers");
        Button moviesButton = UI.createLeftButton("Movies");
        Button shopButton = UI.createLeftButton("Add Shop Item");
        Button placeButton = UI.createLeftButton("Place");
        Button addSessionButton = UI.createLeftButton("Add Session");
        Button sessionButton = UI.createLeftButton("Session");
        Button exitButton = UI.createExitButton();

        dashboardButton.setOnAction(actionEvent -> dashboardScene(root));
        customersButton.setOnAction(actionEvent -> customersScene(root));
        moviesButton.setOnAction(actionEvent -> moviesScene(root));
        shopButton.setOnAction(e -> {
            if (shopStage == null || !shopStage.isShowing()) {
                shopStage = shopScene();
                shopStage.setOnHidden(event -> shopStage = null);
            } else {
                shopStage.toFront();
                shopStage.requestFocus();
            }
        });
        placeButton.setOnAction(e -> placeScene(root));
        addSessionButton.setOnAction(e -> {
            adminPanel = new AdminMethod();
            adminPanel.showMovieManager(adminStage, Role.ADMIN);
        });
        sessionButton.setOnAction(e -> sessionScene(root));
        exitButton.setOnAction(event -> {
            loginScreen = new LoginScreen();
            loginScreen.start(adminStage);
        });

        BorderPane topBar = new BorderPane();
        topBar.setRight(exitButton);
        topBar.setCenter(adminPanelLabel);
        topBar.setPadding(new Insets(14, 22, 14, 28));
        topBar.setStyle("-fx-background-color: " + UI.getBlack() + "; -fx-border-color:" + UI.getBorderColor() + "; -fx-border-width: 0 0 1 0;");

        VBox leftBar = new VBox(12, dashboardButton, customersButton, moviesButton, shopButton, placeButton, addSessionButton, sessionButton);
        leftBar.setPadding(new Insets(22));
        leftBar.setPrefWidth(220);
        leftBar.setBackground(new Background(new BackgroundFill(Color.web(UI.getPanelDark()), CornerRadii.EMPTY, Insets.EMPTY)));
        leftBar.setStyle("-fx-border-color: " + UI.getBorderColor() + "; -fx-border-width: 0 1 0 0;");

        root.setStyle(UI.getLinearGradient());
        root.setTop(topBar);
        root.setLeft(leftBar);
        dashboardScene(root);
    }

    public void dashboardScene(BorderPane root) {
        Label statisticsLabel = UI.createSectionTitle("Statistics");
        HBox statisticsCards = new HBox(20, createStatCard("Amount of customers", String.valueOf(userDAO.getNumberOfCustomers())),
                createStatCard("Amount of sessions", String.valueOf(sessionDAO.GetAmountOfSessions())), createStatCard("Amount of Movies", String.valueOf(movieDAO.GetAmountOfMovies())));

        loadCoupons(couponData);

        Label couponLabel = createSubTitle("Coupons");
        Button addCouponButton = UI.createSectionAddButton();
        addCouponButton.setOnAction(event -> {
            new CouponManagementWindow().display(couponData);
            loadCoupons(couponData);
        });

        TableView<CouponRow> couponTable = Table.createStyledTable(couponData, 300, 930);
        couponTable.getColumns().addAll(
                Table.createTextColumn("Code", 2.2, CouponRow::coupon_codeProperty),
                Table.createTextColumn("Discount Percentage", 2.1, CouponRow::discount_percentageProperty),
                Table.createStatusColumn("Status", 2.1, CouponRow::statusProperty)
        );
        couponTable.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            Table.applyTableHeaderStyle(couponTable);
        });

        VBox centerBar = new VBox(18, statisticsLabel, statisticsCards, couponLabel, addCouponButton, couponTable);
        centerBar.setPadding(new Insets(30, 40, 30, 50));

        ScrollPane dashboardScroll = UI.createScrollPane(centerBar);
        Table.applyTableHeaderStyle(couponTable);
        root.setCenter(dashboardScroll);
    }
    private void loadCoupons(ObservableList<CouponRow> couponData) {
        couponData.clear();
        couponData.addAll(new CouponService().getAllCoupons());
    }

    public void customersScene(BorderPane root) {
        Label customersLabel = UI.createSectionTitle("Customers");

        TableView<CustomerRow> customersTable = Table.createStyledTable(customerData, 320, 900);
        customersTable.getColumns().addAll(
                Table.createTextColumn("ID", 1.0, CustomerRow::idProperty),
                Table.createTextColumn("Login ", 1.2, CustomerRow::loginProperty),
                Table.createTextColumn("Email", 1.1, CustomerRow::emailProperty),
                Table.createTextColumn("Booked Tickets", 1.2, CustomerRow::bookedTicketsProperty),
                Table.createCustomerStatusColumn("Status", 1.2, CustomerRow::blockedProperty),
                Table.createCustomerActionColumn(this.userDAO)
        );

        VBox centerBar = new VBox(16, customersLabel, customersTable);
        centerBar.setPadding(new Insets(30, 40, 30, 50));

        ScrollPane customerScrollPane = UI.createScrollPane(centerBar);
        root.setCenter(customerScrollPane);
    }

    public void moviesScene(BorderPane root) {
        Label moviesLabel = UI.createSectionTitle("Movies");

        TableView<AdminMovieRow> moviesTable = Table.createStyledTable(movieData, 320, 920);
        moviesTable.getColumns().addAll(
                Table.createTextColumn("ID", 0.9, AdminMovieRow::idProperty),
                Table.createTextColumn("Title", 1.5, AdminMovieRow::nameProperty),
                Table.createTextColumn("Genre", 1.6, AdminMovieRow::genreProperty),
                Table.createTextColumn("Registered Date", 1.4, AdminMovieRow::registeredDateProperty),
                Table.createMovieStatusColumn("Status", 1.0, AdminMovieRow::statusProperty),
                Table.createMovieActionColumn(this.movieDAO)
        );

        Button addMovieButton = UI.createSectionAddButton();

        addMovieButton.setOnAction(e -> {
            adminPanel = new AdminMethod();
            adminPanel.AddMovieManager(adminStage);
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


    public void placeScene(BorderPane root) {
        Label placeLabel = UI.createSectionTitle("Place Management");

        Button addCityButton = UI.createSectionAddButton();
        addCityButton.setText("Add City");
        addCityButton.setPrefWidth(150);
        Button addCinemaButton = UI.createSectionAddButton();
        addCinemaButton.setText("Add Cinema");
        addCinemaButton.setPrefWidth(150);


        HBox buttonRow = new HBox(30, addCityButton, addCinemaButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        addCityButton.setOnAction(e -> {
            adminPanel = new AdminMethod();
            adminPanel.showCityManager(adminStage);
        });
        addCinemaButton.setOnAction(e -> {
            adminPanel = new AdminMethod();
            adminPanel.showCinemaManager(adminStage);
        });

        VBox infoCard = createPanelBox(new Label("Use these buttons to manage cities and cinemas."));
        ((Label) infoCard.getChildren().getFirst()).setTextFill(Color.web(UI.getLightText()));

        VBox centerBar = new VBox(18, placeLabel, buttonRow, infoCard);
        centerBar.setPadding(new Insets(30, 40, 30, 50));
        root.setCenter(centerBar);
    }

    private void sessionScene(BorderPane root) {
        TableView<SessionRow> sessionTable = Table.createStyledTable(sessionData, 320, 920);
        sessionTable.getColumns().addAll(
                Table.createTextColumn("show_id", 1.0, SessionRow::show_idProperty),
                Table.createTextColumn("movie_id", 1.0, SessionRow::movieTitleProperty),
                Table.createTextColumn("Cinema", 1.5, SessionRow::cinemaProperty),
                Table.createTextColumn("Hall", 1.2, SessionRow::hallProperty),
                Table.createTextColumn("Showtime", 1.2, SessionRow::showtimeProperty),
                Table.createSessionActionColumn(this.sessionDAO, "Delete")
        );

        VBox centerBar = new  VBox(16, sessionTable);
        centerBar.setPadding(new Insets(30, 40, 30, 50));

        ScrollPane sessionScrollPane = UI.createScrollPane(centerBar);
        root.setCenter(sessionScrollPane);
    }

     public Label createSubTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Bahnschrift", FontWeight.BOLD, 30));
        label.setTextFill(Color.web(UI.getLightText()));
        return label;
    }

     public VBox createStatCard(String title, String value) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Bahnschrift", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web(UI.getMutedText()));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Bahnschrift", FontWeight.BOLD, 32));
        valueLabel.setTextFill(Color.web(UI.getYellow()));

        VBox card = new VBox(10, titleLabel, valueLabel);
        card.setPrefWidth(260);
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color: " + UI.getPanelGray() + ";" +
                        "-fx-border-color: " + UI.getBorderColor() + ";" +
                        "-fx-border-radius: 18;" +
                        "-fx-background-radius: 18;"
        );
        return card;
    }

     public VBox createPanelBox(Label first) {
        VBox box = new VBox(10, first);
        box.setPadding(new Insets(20));
        box.setMaxWidth(720);
        box.setStyle(
                "-fx-background-color: " + UI.getPanelGray() + ";" +
                        "-fx-border-color: " + UI.getBorderColor() + ";" +
                        "-fx-border-radius: 18;" +
                        "-fx-background-radius: 18;"
        );
        return box;
    }
}
