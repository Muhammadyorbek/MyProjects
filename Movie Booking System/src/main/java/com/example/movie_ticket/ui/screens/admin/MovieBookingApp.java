package com.example.movie_ticket.ui.screens.admin;

import com.example.movie_ticket.database.MovieDAO;
import com.example.movie_ticket.model.Movie;
import com.example.movie_ticket.service.UserProfile;
import com.example.movie_ticket.service.UserSession;
import com.example.movie_ticket.ui.screens.BasketManager;
import com.example.movie_ticket.ui.ShopFx;
import com.example.movie_ticket.ui.screens.LoginScreen;
import com.example.movie_ticket.ui.screens.UI;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.*;

public class MovieBookingApp extends Application {

    private final StackPane rootStack = new StackPane();
    private final Scene     mainScene = new Scene(rootStack, UI.sceneWidth(), UI.sceneHeight());

    private final MovieDAO        movieDAO    = new MovieDAO();
    private final Map<Movie, Button> cardButtons = new HashMap<>();


    private final Map<String, Image> imageCache  = new ConcurrentHashMap<>();
    private final ExecutorService    imageLoader = Executors.newFixedThreadPool(4);

    private String searchKeyword  = "";
    private String cityFilter     = "All Cities";
    private String genreFilter    = "All Genres";
    private String yearFilter     = "All Years";
    private String languageFilter = "All Languages";


    private int          heroIndex      = 0;
    private List<Movie>  allMovies      = new ArrayList<>();
    private Timeline     heroTimeline;
    private final ImageView heroImageView = new ImageView();
    private final Label     heroTitleLbl  = new Label();

    private MovieGridScreen  gridScreen;
    private BasketScreen     basketScreen;


    private UserProfile userProfile;
    private final Image     defaultAvatar = new Image(
            "https://cdn-icons-png.flaticon.com/512/149/149071.png", 150, 150, true, true);
    private final ImageView navAvatar     = new ImageView(defaultAvatar);

    private PauseTransition filterDebounce;

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        buildScene(stage);

        stage.setTitle("Cinema Reserve | Premium Online Booking");
        stage.setScene(mainScene);
        stage.show();

        stage.setOnCloseRequest(e -> {
            imageLoader.shutdownNow();
            Platform.exit();
            System.exit(0);
        });
    }

    public void buildScene(Stage stage) {
        gridScreen   = new MovieGridScreen(this);
        basketScreen = new BasketScreen(this);

        HBox      navBar      = buildNavBar(stage);
        StackPane heroSlider  = buildHeroSlider();
        ScrollPane movieScroll = gridScreen.buildScrollPane();
        Button    basketBtn   = buildFloatingBasketButton();

        VBox pageLayout = new VBox(navBar, heroSlider, movieScroll);
        VBox.setVgrow(movieScroll, Priority.ALWAYS);

        rootStack.getChildren().addAll(pageLayout, basketBtn);
        StackPane.setAlignment(basketBtn, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(basketBtn, new Insets(40));

        loadAllMoviesAsync();
    }

    private HBox buildNavBar(Stage stage) {
        HBox navBar = new HBox(20);
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(15, 40, 15, 40));
        navBar.setStyle(
                "-fx-background-color: #121212;" +
                        "-fx-border-color: #333333;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        Label logo = new Label("CINEMA RESERVE");
        logo.setFont(Font.font("Verdana", FontWeight.BOLD, 26));
        logo.setTextFill(Color.web("#FFD700"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField searchBox = new TextField();
        searchBox.setPromptText("Search movies...");
        searchBox.setPrefWidth(220);
        searchBox.setStyle(
                "-fx-background-color: #252525;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #888888;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;"
        );

        PauseTransition searchDebounce = new PauseTransition(Duration.millis(400));
        searchDebounce.setOnFinished(e ->
                gridScreen.loadMoviesAsync(searchBox.getText(), cityFilter, genreFilter, yearFilter, languageFilter)
        );
        searchBox.textProperty().addListener((obs, oldVal, newVal) -> searchDebounce.playFromStart());
        this.filterDebounce = searchDebounce;

        Button shopBtn = new Button("🛍  SHOP");
        shopBtn.setStyle(
                "-fx-background-color: #FFD700;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 6 18;" +
                        "-fx-cursor: hand;"
        );
        UI.setHoverButtonStyle(shopBtn,
                "-fx-background-color: #e6c200; -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 6 18; -fx-cursor: hand;",
                "-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 6 18; -fx-cursor: hand;"
        );
        shopBtn.setOnAction(e -> openShop(stage));

        StackPane authArea = buildAuthArea(stage);

        navBar.getChildren().addAll(logo, spacer, searchBox, shopBtn, authArea);
        return navBar;
    }

    private StackPane buildAuthArea(Stage stage) {
        StackPane authArea = new StackPane();
        boolean loggedIn   = UserSession.getInstance().getUserId() > 0;

        if (loggedIn) {
            navAvatar.setFitWidth(40);
            navAvatar.setFitHeight(40);
            navAvatar.setClip(new javafx.scene.shape.Circle(20, 20, 20));

            userProfile = new UserProfile();
            Button profileBtn = new Button();
            profileBtn.setGraphic(navAvatar);
            profileBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
            profileBtn.setOnAction(e -> userProfile.display(stage, navAvatar));
            authArea.getChildren().add(profileBtn);
        } else {
            Button signInBtn = new Button("SIGN IN");
            signInBtn.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #FFD700;" +
                            "-fx-border-color: #FFD700;" +
                            "-fx-border-radius: 5;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 5 15;" +
                            "-fx-cursor: hand;"
            );
            signInBtn.setOnAction(e -> {
                try { new LoginScreen().start(stage); }
                catch (Exception ex) { ex.printStackTrace(); }
            });
            authArea.getChildren().add(signInBtn);
        }
        return authArea;
    }

    private void openShop(Stage stage) {
        try {
            Stage shopStage = new Stage();
            ShopFx shopApp  = new ShopFx();
            shopApp.start(shopStage);
            shopStage.setOnCloseRequest(ev -> stage.show());
            stage.hide();
        } catch (Exception ex) {
            System.err.println("Could not open Shop: " + ex.getMessage());
        }
    }

    private StackPane buildHeroSlider() {
        StackPane heroSlider = new StackPane();
        heroSlider.setPrefHeight(400);
        heroSlider.setStyle("-fx-background-color: black;");

        heroImageView.setFitHeight(400);
        heroImageView.setPreserveRatio(true);

        VBox overlay = new VBox(10);
        overlay.setAlignment(Pos.BOTTOM_LEFT);
        overlay.setPadding(new Insets(0, 0, 40, 60));
        overlay.setStyle("-fx-background-color: linear-gradient(to top, rgba(18,18,18,1), rgba(18,18,18,0));");

        heroTitleLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 48));
        heroTitleLbl.setTextFill(Color.WHITE);

        Button nextBtn = new Button("NEXT FEATURED ❯");
        nextBtn.setStyle("-fx-background-color: #FFD700; -fx-font-weight: bold; -fx-padding: 10 20;");
        nextBtn.setOnAction(e -> rotateHeroImage());

        overlay.getChildren().addAll(heroTitleLbl, nextBtn);
        heroSlider.getChildren().addAll(heroImageView, overlay);
        return heroSlider;
    }

    private void rotateHeroImage() {
        if (allMovies == null || allMovies.isEmpty()) return;
        if (heroTimeline != null) heroTimeline.stop();

        heroTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            heroIndex = (heroIndex + 1) % allMovies.size();
            Movie movie = allMovies.get(heroIndex);

            imageLoader.submit(() -> {
                Image img = getCachedImage(movie.getImageUrl(), 1200, 400);
                Platform.runLater(() -> {
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(400), heroImageView);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.3);
                    fadeOut.setOnFinished(e -> {
                        heroImageView.setImage(img);
                        heroTitleLbl.setText(movie.getTitle().toUpperCase());
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), heroImageView);
                        fadeIn.setFromValue(0.3);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();
                    });
                    fadeOut.play();
                });
            });
        }));
        heroTimeline.setCycleCount(Timeline.INDEFINITE);
        heroTimeline.play();
    }

    private Button buildFloatingBasketButton() {
        Button basketBtn = new Button("🛒");
        basketBtn.setStyle(
                "-fx-background-color: #FFD700;" +
                        "-fx-background-radius: 50;" +
                        "-fx-font-size: 28;" +
                        "-fx-pref-width: 75;" +
                        "-fx-pref-height: 75;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 15, 0, 0, 0);"
        );
        basketBtn.setOnAction(e -> basketScreen.show());
        return basketBtn;
    }

    private void loadAllMoviesAsync() {
        gridScreen.showSpinner();

        Task<List<Movie>> loadTask = new Task<>() {
            @Override protected List<Movie> call() {
                List<Movie> movies = movieDAO.getAllMovies();
                return (movies == null) ? new ArrayList<>() : movies;
            }
        };

        loadTask.setOnSucceeded(e -> {
            allMovies = loadTask.getValue();
            if (!allMovies.isEmpty()) rotateHeroImage();
            gridScreen.displayMovies(allMovies);
        });

        loadTask.setOnFailed(e -> {
            if (loadTask.getException() != null)
                loadTask.getException().printStackTrace();
        });

        Thread thread = new Thread(loadTask, "initial-data-loader");
        thread.setDaemon(true);
        thread.start();
    }

    public Image getCachedImage(String url, double width, double height) {
        return imageCache.computeIfAbsent(url, key ->
                new Image(key, width, height, true, true, true)
        );
    }

    public void updateBookButtonVisual(Movie movie) {
        Button btn = cardButtons.get(movie);
        if (btn == null) return;

        boolean alreadyBooked = BasketManager.getInstance().containsMovie(movie.getMovieId());
        if (alreadyBooked) {
            btn.setText("ADDED ✓");
            btn.setStyle(
                    "-fx-background-color: #4CAF50;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 12;"
            );
            btn.setDisable(false);
        } else {
            btn.setText("BOOK NOW");
            btn.setStyle(
                    "-fx-background-color: #FFD700;" +
                            "-fx-text-fill: black;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 12;" +
                            "-fx-cursor: hand;"
            );
            btn.setDisable(false);
        }
    }

    public Map<Movie, Button> getCardButtons()   { return cardButtons; }
    public ExecutorService    getImageLoader()   { return imageLoader; }
    public PauseTransition    getFilterDebounce(){ return filterDebounce; }
    public Stage              getPrimaryStage()  { return primaryStage; }

    public String getSearchKeyword()  { return searchKeyword; }
    public String getCityFilter()     { return cityFilter; }
    public String getGenreFilter()    { return genreFilter; }
    public String getYearFilter()     { return yearFilter; }
    public String getLanguageFilter() { return languageFilter; }

    public void setSearchKeyword (String v) { searchKeyword  = v; }
    public void setCityFilter    (String v) { cityFilter     = v; }
    public void setGenreFilter   (String v) { genreFilter    = v; }
    public void setYearFilter    (String v) { yearFilter     = v; }
    public void setLanguageFilter(String v) { languageFilter = v; }

    @Override
    public void stop() {
        if (imageLoader  != null) imageLoader.shutdownNow();
        if (heroTimeline != null) heroTimeline.stop();
        System.exit(0);
    }
}