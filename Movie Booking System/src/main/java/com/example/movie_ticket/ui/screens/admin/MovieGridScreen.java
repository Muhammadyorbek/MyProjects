package com.example.movie_ticket.ui.screens.admin;

import com.example.movie_ticket.database.MovieDAO;
import com.example.movie_ticket.model.Movie;
import com.example.movie_ticket.service.UserSession;
import com.example.movie_ticket.ui.screens.LoginScreen;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MovieGridScreen {

    private final MovieBookingApp app;
    private final MovieDAO        movieDAO = new MovieDAO();

    private final FlowPane movieGrid = new FlowPane();

    private static final String PLACEHOLDER_IMAGE_URL =
            "https://via.placeholder.com/220x310/1E1E1E/FFD700?text=Loading...";

    public MovieGridScreen(MovieBookingApp app) {
        this.app = app;
    }

    public ScrollPane buildScrollPane() {
        movieGrid.setHgap(30);
        movieGrid.setVgap(30);
        movieGrid.setPadding(new Insets(40));
        movieGrid.setStyle("-fx-background-color: #121212;");

        HBox filterBar   = buildFilterBar();
        VBox gridWrapper = new VBox(filterBar, movieGrid);
        gridWrapper.setStyle("-fx-background-color: #121212;");

        ScrollPane scrollPane = new ScrollPane(gridWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
                "-fx-background: #121212;" +
                        "-fx-background-color: #121212;" +
                        "-fx-border-color: #121212;"
        );
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        movieGrid.prefWrapLengthProperty().bind(scrollPane.widthProperty().subtract(80));
        return scrollPane;
    }

    private HBox buildFilterBar() {
        HBox filterBar = new HBox(15);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(10, 40, 10, 40));
        filterBar.setStyle(
                "-fx-background-color: #1A1A1A;" +
                        "-fx-border-color: #2A2A2A;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        Label filterLabel = new Label("FILTER:");
        filterLabel.setTextFill(Color.web("#FFD700"));
        filterLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));

        ComboBox<String> cityCombo = buildFilterCombo("City",
                "All Cities", "Tashkent", "Samarkand", "Bukhara");

        ComboBox<String> genreCombo = buildFilterCombo("Genre",
                "All Genres", "Action", "Comedy", "Drama", "Horror",
                "Sci-Fi", "Romance", "Thriller", "Animation", "Documentary");

        List<String> yearOptions = new ArrayList<>();
        yearOptions.add("All Years");
        for (int year = 2025; year >= 2000; year--) yearOptions.add(String.valueOf(year));
        ComboBox<String> yearCombo = buildFilterCombo("Year", yearOptions.toArray(new String[0]));

        ComboBox<String> languageCombo = buildFilterCombo("Language",
                "All Languages", "English", "Uzbek", "Russian", "Spanish", "French", "Korean", "Japanese");

        cityCombo.setOnAction(e -> {
            app.setCityFilter(cityCombo.getValue());
            app.getFilterDebounce().playFromStart();
        });
        genreCombo.setOnAction(e -> {
            app.setGenreFilter(genreCombo.getValue());
            app.getFilterDebounce().playFromStart();
        });
        yearCombo.setOnAction(e -> {
            app.setYearFilter(yearCombo.getValue());
            app.getFilterDebounce().playFromStart();
        });
        languageCombo.setOnAction(e -> {
            app.setLanguageFilter(languageCombo.getValue());
            app.getFilterDebounce().playFromStart();
        });

        Button resetBtn = new Button("✕ RESET");
        resetBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #888888;" +
                        "-fx-border-color: #555555;" +
                        "-fx-border-radius: 4;" +
                        "-fx-font-size: 11px;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 4 10;"
        );
        resetBtn.setOnMouseEntered(e -> resetBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #F44336; " +
                        "-fx-border-color: #F44336; -fx-border-radius: 4; -fx-font-size: 11px; " +
                        "-fx-cursor: hand; -fx-padding: 4 10;"));
        resetBtn.setOnMouseExited(e -> resetBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #888888; " +
                        "-fx-border-color: #555555; -fx-border-radius: 4; -fx-font-size: 11px; " +
                        "-fx-cursor: hand; -fx-padding: 4 10;"));
        resetBtn.setOnAction(e -> {
            cityCombo.setValue("All Cities");
            genreCombo.setValue("All Genres");
            yearCombo.setValue("All Years");
            languageCombo.setValue("All Languages");
            app.setCityFilter("All Cities");
            app.setGenreFilter("All Genres");
            app.setYearFilter("All Years");
            app.setLanguageFilter("All Languages");
            app.getFilterDebounce().playFromStart();
        });

        filterBar.getChildren().addAll(filterLabel, cityCombo, genreCombo, yearCombo, languageCombo, resetBtn);
        return filterBar;
    }

    private ComboBox<String> buildFilterCombo(String prompt, String... options) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().setAll(options);
        combo.setValue(options[0]);
        combo.setPromptText(prompt);
        combo.setPrefWidth(140);
        combo.setStyle(
                "-fx-background-color: #252525;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: #444444;" +
                        "-fx-border-radius: 4;"
        );
        return combo;
    }

    public void showSpinner() {
        movieGrid.getChildren().clear();
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setStyle("-fx-progress-color: #FFD700;");
        movieGrid.getChildren().add(spinner);
    }

    public void displayMovies(List<Movie> movies) {
        movieGrid.getChildren().clear();

        if (movies == null || movies.isEmpty()) {
            Label empty = new Label("No movies found.");
            empty.setTextFill(Color.GRAY);
            movieGrid.getChildren().add(empty);
            return;
        }

        for (Movie movie : movies)
            movieGrid.getChildren().add(buildMovieCard(movie));
    }

    public void loadMoviesAsync(String keyword, String city,
                                String genre, String year, String language) {
        app.setSearchKeyword(keyword);
        app.setCityFilter(city);
        app.setGenreFilter(genre);
        app.setYearFilter(year);
        app.setLanguageFilter(language);

        showSpinner();

        Task<List<Movie>> filterTask = new Task<>() {
            @Override protected List<Movie> call() {
                List<Movie> movies = movieDAO.getAllMovies();
                if (movies == null) return new ArrayList<>();

                int parsedYear = "All Years".equals(year) ? 0 : Integer.parseInt(year);

                movies.removeIf(movie -> {
                    boolean matchesKeyword = keyword.isEmpty() ||
                            movie.getTitle().toLowerCase().contains(keyword.toLowerCase());
                    boolean matchesGenre   = genre.equals("All Genres") ||
                            movie.getGenre().equalsIgnoreCase(genre);
                    boolean matchesYear    = parsedYear == 0 ||
                            movie.getReleaseYear() == parsedYear;
                    boolean matchesLang    = language.equals("All Languages") ||
                            movie.getLanguage().equalsIgnoreCase(language);

                    return !(matchesKeyword && matchesGenre && matchesYear && matchesLang);
                });

                return movies;
            }
        };

        filterTask.setOnSucceeded(e -> {
            movieGrid.getChildren().clear();
            List<Movie> filteredMovies = filterTask.getValue();

            if (filteredMovies.isEmpty()) {
                Label noResults = new Label("No movies found. Try adjusting your filters.");
                noResults.setTextFill(Color.GRAY);
                noResults.setFont(Font.font(20));
                movieGrid.getChildren().add(noResults);
            } else {
                for (Movie movie : filteredMovies)
                    movieGrid.getChildren().add(buildMovieCard(movie));
            }
        });

        filterTask.setOnFailed(e -> {
            filterTask.getException().printStackTrace();
            Platform.runLater(() -> {
                movieGrid.getChildren().clear();
                Label errorLbl = new Label("Error loading movies: " + filterTask.getException().getMessage());
                errorLbl.setTextFill(Color.RED);
                movieGrid.getChildren().add(errorLbl);
            });
        });

        Thread thread = new Thread(filterTask, "movie-filter-thread");
        thread.setDaemon(true);
        thread.start();
    }

    private VBox buildMovieCard(Movie movie) {
        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: #1E1E1E;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #333333;" +
                        "-fx-border-radius: 15;"
        );
        card.setPadding(new Insets(15));
        card.setPrefWidth(250);
        card.setAlignment(Pos.TOP_LEFT);

        ImageView poster = new ImageView();
        poster.setFitWidth(220);
        poster.setFitHeight(310);
        poster.setPreserveRatio(true);
        poster.setImage(app.getCachedImage(PLACEHOLDER_IMAGE_URL, 220, 310));
        app.getImageLoader().submit(() -> {
            Image realPoster = app.getCachedImage(movie.getImageUrl(), 220, 310);
            Platform.runLater(() -> poster.setImage(realPoster));
        });

        Label titleLbl = new Label(movie.getTitle().toUpperCase());
        titleLbl.setTextFill(Color.WHITE);
        titleLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        titleLbl.setWrapText(true);
        titleLbl.setMinHeight(50);

        Label ratingGenreLbl = new Label("⭐ " + movie.getRating() + " | " + movie.getGenre());
        ratingGenreLbl.setTextFill(Color.web("#FFD700"));

        Label yearLangLbl = new Label(movie.getReleaseYear() + "  •  " + movie.getLanguage());
        yearLangLbl.setTextFill(Color.web("#AAAAAA"));
        yearLangLbl.setFont(Font.font(11));

        Label priceLbl = new Label("$" + String.format("%.2f", movie.getPrice()));
        priceLbl.setTextFill(Color.WHITE);
        priceLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        Button bookBtn = new Button();
        bookBtn.setMaxWidth(Double.MAX_VALUE);
        app.getCardButtons().put(movie, bookBtn);
        app.updateBookButtonVisual(movie);

        bookBtn.setOnAction(e -> {
            if (UserSession.getInstance().getUserId() <= 0) {
                Alert loginAlert = new Alert(Alert.AlertType.WARNING);
                loginAlert.setTitle("Sign In Required");
                loginAlert.setHeaderText(null);
                loginAlert.setContentText(
                        "You need to sign in to book tickets.\n\n" +
                                "Please sign in or create an account."
                );

                ButtonType signInBtn  = new ButtonType("SIGN IN",  ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelBtn  = new ButtonType("CANCEL",   ButtonBar.ButtonData.CANCEL_CLOSE);
                loginAlert.getButtonTypes().setAll(signInBtn, cancelBtn);

                loginAlert.showAndWait().ifPresent(response -> {
                    if (response == signInBtn) {
                        try {
                            new LoginScreen().start(app.getPrimaryStage());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                return;
            }

            Stage owner = (Stage) bookBtn.getScene().getWindow();
            CinemaBookingBridge.open(movie, owner, () ->
                    Platform.runLater(() ->
                            app.updateBookButtonVisual(movie)
                    )
            );
        });

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #252525;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-radius: 15;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: #1E1E1E;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #333333;" +
                        "-fx-border-radius: 15;"
        ));

        card.getChildren().addAll(poster, titleLbl, ratingGenreLbl, yearLangLbl, priceLbl, bookBtn);
        return card;
    }


}
