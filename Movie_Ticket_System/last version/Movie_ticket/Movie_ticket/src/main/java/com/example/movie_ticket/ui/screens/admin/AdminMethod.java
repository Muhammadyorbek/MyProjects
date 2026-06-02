package com.example.movie_ticket.ui.screens.admin;

import com.example.movie_ticket.database.*;
import com.example.movie_ticket.model.*;
import com.example.movie_ticket.ui.screens.FrontDeskOfficerScreen;
import com.example.movie_ticket.ui.screens.Table;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.example.movie_ticket.ui.screens.UI;

import java.util.List;

public class AdminMethod {
    private Stage stage;
    AdminScreen adminScreenTest;
    FrontDeskOfficerScreen frontDeskOfficerScreen;

    private final MovieDAO movieDAO = new MovieDAO();
    private final SessionDAO sessionDAO = new SessionDAO();
    private final CinemaDAO cinemaDAO = new CinemaDAO();
    private final CityDAO cityDAO = new CityDAO();

    public void show(Stage stage) {
        this.stage = stage;

        VBox menu = new VBox(24);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(48));
        menu.setStyle(UI.getPageStyle());

        Label eyebrow = new Label("Admin Panel");
        eyebrow.setStyle("-fx-text-fill: " + UI.getMuted() + "; -fx-font-size: 18px; -fx-font-weight: 600;");

        Label title = new Label("Cinema Management");
        title.setStyle("-fx-text-fill: " + UI.getPrimary() + "; -fx-font-size: 40px; -fx-font-weight: 800;");

        Label subtitle = new Label("Use the same structured editor style across every admin screen.");
        subtitle.setStyle("-fx-text-fill: " + UI.getMuted() + "; -fx-font-size: 16px;");

        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36));
        card.setMaxWidth(560);
        card.setStyle(UI.getSurfaceStyle());

        Button b1 = UI.createPrimaryButton("Manage Cities");
        Button b2 = UI.createPrimaryButton("Manage Cinemas");
        Button b3 = UI.createPrimaryButton("Manage Movies & Shows");
        Button bBack = UI.createSecondaryButton("Exit To Launcher");

        b1.setOnAction(e -> showCityManager(stage));
        b2.setOnAction(e -> showCinemaManager(stage));
        b3.setOnAction(e -> showMovieManager(stage, Role.ADMIN));
        bBack.setOnAction(e -> {
            try {
                AdminScreen adminScreen = new AdminScreen();
                adminScreen.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        card.getChildren().addAll(eyebrow, title, subtitle, b1, b2, b3, bBack);
        menu.getChildren().add(card);

        stage.setScene(new Scene(menu, UI.sceneWidth(), UI.sceneHeight()));
        stage.setTitle("Admin Panel");
        stage.show();
    }

    void showCityManager(Stage stage1) {
        ObservableList<City> cityItems = FXCollections.observableArrayList(cityDAO.getAllCities());
        TableView<City> table = new TableView<>(cityItems);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No cities available"));
        table.setStyle(UI.getTableStyle());
        Table.styleTableHeaders(table);

        TableColumn<City, String> numberCol = new TableColumn<>("No.");
        numberCol.setMaxWidth(80);
        numberCol.setReorderable(false);
        numberCol.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(String.valueOf(table.getItems().indexOf(data.getValue()) + 1)));

        TableColumn<City, String> nameCol = new TableColumn<>("City Name");
        nameCol.setReorderable(false);
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        table.getColumns().addAll(numberCol, nameCol);

        TextField input = UI.createInput("Enter city name");
        Label status = UI.createStatusLabel();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) ->
                input.setText(newValue == null ? "" : newValue.getName()));

        Button add = UI.createPrimaryButton("Add");
        Button update = UI.createSecondaryButton("Update");
        Button delete = UI.createDangerButton("Delete");
        Button backButton = UI.createSecondaryButton("Back");

        add.setOnAction(e -> {
            String value = input.getText().trim();
            if (value.isEmpty()) {
                UI.setLabelStatus(status, "Please enter a city name before adding.", true);
                return;
            }

            cityDAO.addCity(value);

            cityItems.setAll(cityDAO.getAllCities());

            input.clear();
            UI.setLabelStatus(status, "City added to Database.", false);


        });


        update.setOnAction(e -> {
            City selectedCity = table.getSelectionModel().getSelectedItem();
            String value = input.getText().trim();

            if (selectedCity == null) {
                UI.setLabelStatus(status, "Select a city first to update it.", true);
                return;
            }
            if (value.isEmpty()) {
                UI.setLabelStatus(status, "City name cannot be empty.", true);
                return;
            }

            cityDAO.updateCity(selectedCity.getId(), value);

            selectedCity.setName(value);

            cityItems.setAll(cityDAO.getAllCities());
            table.refresh();

            input.clear();
            UI.setLabelStatus(status, "City updated in Database.", false);
        });
        delete.setOnAction(e -> {
            City selectedCity = table.getSelectionModel().getSelectedItem();
            if (selectedCity == null) {
                UI.setLabelStatus(status, "Select a city first to delete it.", true);
                return;
            }

            cityDAO.deleteCity(selectedCity.getName());

            cityItems.setAll(cityDAO.getAllCities());

            input.clear();
            UI.setLabelStatus(status, "City deleted from Database.", false);
        });

        backButton.setOnAction(e -> {
            try {
                AdminScreen newAdminScreen = new AdminScreen();

                newAdminScreen.start(stage1);

            } catch (Exception ex) {
                System.err.println("Ошибка при возврате в AdminScreen: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        VBox form = UI.createFormPane(
                "City",
                "Manage the available cities in a table-first editor layout.",
                input,
                status,
                UI.createButtonRow(add, update),
                delete,
                backButton
        );

        stage1.setScene(new Scene(createManagerPage("Cities", "Clean editor layout applied to the city screen.", table, form), UI.sceneWidth(), UI.sceneHeight()));
    }


    void showCinemaManager(Stage stage1) {
        ObservableList<CinemaRow> cinemaItems = FXCollections.observableArrayList();

        TableView<CinemaRow> table = new TableView<>(cinemaItems);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No available data"));
        table.setStyle(UI.getTableStyle());
        Table.styleTableHeaders(table);

        TableColumn<CinemaRow, String> numberCol = new TableColumn<>("No.");
        numberCol.setMaxWidth(80);
        numberCol.setReorderable(false);
        numberCol.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(String.valueOf(table.getItems().indexOf(data.getValue()) + 1)));

        TableColumn<CinemaRow, String> cityCol = new TableColumn<>("City");
        cityCol.setReorderable(false);
        cityCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().cityName()));

        TableColumn<CinemaRow, String> cinemaCol = new TableColumn<>("Cinema Name");
        cinemaCol.setReorderable(false);
        cinemaCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().cinema().getName()));
        table.getColumns().addAll(numberCol, cityCol, cinemaCol);

        ComboBox<City> cityDrop = new ComboBox<>(FXCollections.observableArrayList(cityDAO.getAllCities()));        cityDrop.setPromptText("Select city");
        cityDrop.setMaxWidth(Double.MAX_VALUE);
        cityDrop.setStyle(UI.getComboStyle());
        Table.applyComboTextFix(cityDrop);

        TextField name = UI.createInput("Enter cinema name");
        Label status = UI.createStatusLabel();

        cityDrop.setOnAction(e -> refreshCinemaRows(cinemaItems, cityDrop.getValue()));
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                name.setText(newValue.cinema().getName());
                cityDrop.setValue(findCityByName(newValue.cityName()));
            }
        });

        Button add = UI.createPrimaryButton("Add");
        Button update = UI.createSecondaryButton("Update");
        Button delete = UI.createDangerButton("Delete");
        Button backButton = UI.createSecondaryButton("Back");

        add.setOnAction(e -> {
            City selectedCity = cityDrop.getValue();
            String cinemaName = name.getText().trim();
            if (selectedCity == null || cinemaName.isEmpty()) {
                UI.setLabelStatus(status, "Please choose a city and enter a cinema name.", true);
                return;
            }
            cinemaDAO.addCinema(cinemaName, selectedCity.getName());            refreshCinemaRows(cinemaItems, selectedCity);
            name.clear();
            UI.setLabelStatus(status, "Cinema added.", false);
        });

        update.setOnAction(e -> {
            CinemaRow selectedRow = table.getSelectionModel().getSelectedItem();
            String cinemaName = name.getText().trim();
            if (selectedRow == null) {
                UI.setLabelStatus(status, "Select a cinema first to update it.", true);
                return;
            }
            if (cinemaName.isEmpty()) {
                UI.setLabelStatus(status, "Cinema name cannot be empty.", true);
                return;
            }
            selectedRow.cinema().setName(cinemaName);
            refreshCinemaRows(cinemaItems, findCityByName(selectedRow.cityName()));
            table.refresh();
            UI.setLabelStatus(status, "Cinema updated.", false);
        });

        delete.setOnAction(e -> {
            CinemaRow selectedRow = table.getSelectionModel().getSelectedItem();
            City selectedCity = cityDrop.getValue();
            if (selectedRow == null || selectedCity == null) {
                UI.setLabelStatus(status, "Select a cinema from a city before deleting.", true);
                return;
            }
            selectedCity.getCinemas().remove(selectedRow.cinema());
            sessionDAO.deleteSession(selectedRow.cinema().getId());            refreshCinemaRows(cinemaItems, selectedCity);
            name.clear();
            UI.setLabelStatus(status, "Cinema deleted.", false);
        });

        backButton.setOnAction(e -> {
            adminScreenTest = new AdminScreen();
            adminScreenTest.start(stage1);
        });

        VBox form = UI.createFormPane(
                "Cinema",
                "Pick a city, then add cinemas while keeping the same editor structure.",
                cityDrop,
                name,
                status,
                UI.createButtonRow(add, update),
                delete,
                backButton
        );

        stage1.setScene(new Scene(createManagerPage("Cinemas", "Same admin style, adapted for city-linked cinema data.", table, form), UI.sceneWidth(), UI.sceneHeight()));
    }

    public void showMovieManager(Stage stage1, Role role) {
        ObservableList<AdminMovieRow> movieItems = FXCollections.observableArrayList(movieDAO.getAllAdminMovies());
        ObservableList<Cinema> allCinemas = FXCollections.observableArrayList(cinemaDAO.getAllCinemas());
        TableView<AdminMovieRow> table = new TableView<>(movieItems);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No movies available"));
        table.setStyle(UI.getTableStyle());
        Table.styleTableHeaders(table);

        TextField titleInput = new TextField();
        titleInput.setPromptText("Movie id");

        TextField languageInput = new TextField();
        languageInput.setPromptText("Language");


        TextField ratingInput = new TextField();
        ratingInput.setPromptText("Rating");

        TextArea descriptionInput = new TextArea();
        descriptionInput.setPromptText("Description");
        descriptionInput.setPrefRowCount(3);

        TextField timeInput = UI.createInput("Show time (e.g. 18:30)");


        TextField imageURLInput = new TextField();
        imageURLInput.setPromptText("Image URL");

        Label status = UI.createStatusLabel();

        TableColumn<AdminMovieRow, String> numberCol = new TableColumn<>("No.");
        numberCol.setMaxWidth(80);
        numberCol.setReorderable(false);

        numberCol.setCellValueFactory(data -> {
            int index = table.getItems().indexOf(data.getValue()) + 1;
            return new ReadOnlyStringWrapper(String.valueOf(index));
        });

        TableColumn<AdminMovieRow, String> titleCol = new TableColumn<>("Movie Title");
        titleCol.setReorderable(false);
        titleCol.setCellValueFactory(data -> data.getValue().nameProperty());

        TableColumn<AdminMovieRow, String> genreCol = new TableColumn<>("Release Year");
        genreCol.setReorderable(false);
        genreCol.setCellValueFactory(data -> data.getValue().registeredDateProperty());



        table.getColumns().addAll(numberCol, titleCol, genreCol);



        ComboBox<Cinema> cinemaDrop = new ComboBox<>(allCinemas);
        cinemaDrop.setPromptText("Select cinema");
        cinemaDrop.setMaxWidth(Double.MAX_VALUE);
        cinemaDrop.setStyle(UI.getComboStyle());
        Table.applyComboTextFix(cinemaDrop);

        ComboBox<String> hallDrop = new ComboBox<>(FXCollections.observableArrayList("Hall 1", "Hall 2", "Hall 3"));
        hallDrop.setPromptText("Select hall");
        hallDrop.setMaxWidth(Double.MAX_VALUE);
        hallDrop.setStyle(UI.getComboStyle());
        Table.applyComboTextFix(hallDrop);

        Label hallHint = new Label("Hall 1 and Hall 2 are Regular. Hall 3 is VIP.");
        hallHint.setStyle("-fx-text-fill: " + UI.getMuted() + "; -fx-font-size: 12px;");

        Label selectedMovieLabel = new Label("Selected movie: none");
        selectedMovieLabel.setStyle("-fx-text-fill: " + UI.getMuted() + "; -fx-font-size: 14px; -fx-font-weight: 600;");

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            selectedMovieLabel.setText(newValue == null
                    ? "Selected movie: none"
                    : "Selected movie: " + newValue.getTitle());
            if (newValue != null) {
                titleInput.setText(newValue.getId());
            } else {
                titleInput.clear();
            }
        });


        Button addShow = UI.createPrimaryButton("Add Show");
        Button backButton = UI.createSecondaryButton("Back");

        addShow.setOnAction(e -> {
            Cinema cinema = cinemaDrop.getValue();
            String id = titleInput.getText().trim();
            String cinemaS;
            if (cinema != null) cinemaS = cinema.getName();
            else  return;
            String hall = hallDrop.getValue();
            String showtime = timeInput.getText();
            System.out.println(id + " " + cinemaS + " " + hall + " " + showtime);

            if (cinemaS == null) {
                UI.setLabelStatus(status, "Please select cinema! ", true);
            } else if (hall == null) {
                UI.setLabelStatus(status, "Please select hall! ", true);
            } else if (id == null || !checkID(id)) {
                UI.setLabelStatus(status, "Please write proper id! ", true);
            } else if (showtime == null) {
                UI.setLabelStatus(status, "Please write showtime! ", true);
            } else {
                sessionDAO.addSession(Integer.parseInt(id), showtime, hall, cinemaS);
                timeInput.clear();
                titleInput.clear();
                cinemaDrop.getSelectionModel().clearSelection();
                hallDrop.getSelectionModel().clearSelection();
                cinemaDrop.setPromptText("Select cinema");
                hallDrop.setPromptText("Select hall");
            }
        });


        if (role == Role.ADMIN) {
            backButton.setOnAction(e -> {
                adminScreenTest = new AdminScreen();
                adminScreenTest.start(stage1);
            });
        }
        else {
            backButton.setOnAction(e -> {
                frontDeskOfficerScreen = new FrontDeskOfficerScreen();
                frontDeskOfficerScreen.start(stage1);
            });
        }

        VBox form = UI.createFormPane(
                "Movies & Shows",
                "Edit movie names/genres and add shows with fixed halls.",
                titleInput,
                selectedMovieLabel,
                cinemaDrop,
                hallDrop,
                hallHint,
                timeInput,
                status,
                addShow,
                backButton
        );

        stage1.setScene(new Scene(createManagerPage("Movies & Shows", "Structured editor screen for both movie and show management.", table, form), UI.sceneWidth(), UI.sceneHeight()));
    }

    private BorderPane createManagerPage(String titleText, String subtitleText, TableView<?> table, VBox formPane) {
        BorderPane page = new BorderPane();
        page.setPadding(new Insets(28));
        page.setStyle(UI.getPageStyle());

        Label title = new Label(titleText);
        title.setStyle("-fx-text-fill: " + UI.getPrimary() + "; -fx-font-size: 28px; -fx-font-weight: 800;");

        Label subtitle = new Label(subtitleText);
        subtitle.setStyle("-fx-text-fill: " + UI.getMuted() + "; -fx-font-size: 14px;");

        VBox header = new VBox(6, title, subtitle);
        header.setPadding(new Insets(0, 0, 18, 0));

        VBox leftCard = new VBox(16);
        leftCard.setPadding(new Insets(18));
        leftCard.setStyle(UI.getSurfaceStyle());
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPrefHeight(540);
        leftCard.getChildren().addAll(header, table);

        ScrollPane formScroll = new ScrollPane(formPane);
        formScroll.setFitToWidth(true);
        formScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        formScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        formScroll.setPrefWidth(400);
        formScroll.setPannable(true);
        formScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        HBox content = new HBox(24, leftCard, formScroll);
        content.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(leftCard, Priority.ALWAYS);

        page.setCenter(content);
        return page;
    }

    private void refreshCinemaRows(ObservableList<CinemaRow> rows, City city) {
        rows.clear();
        if (city == null) {
            return;
        }

        for (Cinema cinema : city.getCinemas()) {
            rows.add(new CinemaRow(city.getName(), cinema));
        }
    }

    private List<Cinema> getCinemasByCity() {
        return cinemaDAO.getCinemasByCity(UI.getLightText() + ";");
    }

    private City findCityByName(String cityName) {
        return cityDAO.findCityByName(cityName);
    }

    private String getHallType(String hallName) {
        return "Hall 3".equals(hallName) ? "VIP" : "REGULAR";
    }


    public void AddMovieManager(Stage stage) {
        ObservableList<Movie> movieItems = FXCollections.observableArrayList(movieDAO.getAllMovies());
        TableView<Movie> table = new TableView<>(movieItems);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No movies available"));
        table.setStyle(UI.getTableStyle());
        Table.styleTableHeaders(table);

        TableColumn<Movie, String> numberCol = new TableColumn<>("No.");
        numberCol.setMaxWidth(50);
        numberCol.setReorderable(false);
        numberCol.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(String.valueOf(table.getItems().indexOf(data.getValue()) + 1)));

        TableColumn<Movie, String> titleCol = new TableColumn<>("Title");
        titleCol.setReorderable(false);
        titleCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTitle()));

        TableColumn<Movie, String> genreCol = new TableColumn<>("Genre");
        genreCol.setReorderable(false);
        genreCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getGenre()));

        TableColumn<Movie, String> yearCol = new TableColumn<>("Year");
        yearCol.setMaxWidth(80);
        yearCol.setReorderable(false);
        yearCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getReleaseYear())));

        table.getColumns().addAll(numberCol, titleCol, genreCol, yearCol);

        TextField titleInput = UI.createInput("Title");
        TextField genreInput = UI.createInput("Genre");
        TextField languageInput = UI.createInput("Language");
        TextField durationInput = UI.createInput("Duration (min)");
        TextField yearInput = UI.createInput("Release Year");
        TextField ratingInput = UI.createInput("Rating (e.g., PG-13)");
        TextField priceInput = UI.createInput("Price");
        TextField imageUrlInput = UI.createInput("Image URL");
        TextField descInput = UI.createInput("Description");

        Label status = UI.createStatusLabel();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                titleInput.setText(newValue.getTitle());
                genreInput.setText(newValue.getGenre());
                languageInput.setText(newValue.getLanguage());
                durationInput.setText(String.valueOf(newValue.getDuration()));
                yearInput.setText(String.valueOf(newValue.getReleaseYear()));
                ratingInput.setText(newValue.getRating());
                priceInput.setText(String.valueOf(newValue.getPrice()));
                imageUrlInput.setText(newValue.getImageUrl());
                descInput.setText(newValue.getDescription());
            } else {
                titleInput.clear(); genreInput.clear(); languageInput.clear();
                durationInput.clear(); yearInput.clear(); ratingInput.clear();
                priceInput.clear(); imageUrlInput.clear(); descInput.clear();
            }
        });

        Button add = UI.createPrimaryButton("Add");
        Button update = UI.createSecondaryButton("Update");
        Button backButton = UI.createSecondaryButton("Back");

        add.setOnAction(e -> {
            String title = titleInput.getText().trim();
            String genre = genreInput.getText().trim();
            String language = languageInput.getText().trim();
            String durationStr = durationInput.getText().trim();
            String yearStr = yearInput.getText().trim();
            String rating = ratingInput.getText().trim();
            String priceStr = priceInput.getText().trim();
            String imageUrl = imageUrlInput.getText().trim();
            String description = descInput.getText().trim();

            if (title.isEmpty() || genre.isEmpty() || yearStr.isEmpty() || priceStr.isEmpty()) {
                UI.setLabelStatus(status, "Please fill in all required fields (Title, Genre, Year, Price).", true);
                return;
            }

            try {
                int duration = Integer.parseInt(durationStr);
                int releaseYear = Integer.parseInt(yearStr);
                double price = Double.parseDouble(priceStr.replace(",", "."));

                movieDAO.addMovie(title, genre, language, duration, releaseYear, rating, price, imageUrl, description);

                movieItems.setAll(movieDAO.getAllMovies());

                titleInput.clear(); genreInput.clear(); languageInput.clear();
                durationInput.clear(); yearInput.clear(); ratingInput.clear();
                priceInput.clear(); imageUrlInput.clear(); descInput.clear();

                UI.setLabelStatus(status, "Movie added to Database.", false);

            } catch (NumberFormatException ex) {
                UI.setLabelStatus(status, "Error: Duration, Year, and Price must be numbers.", true);
            }
        });

        backButton.setOnAction(e -> {
            try {
                AdminScreen adminScreen = new AdminScreen();
                adminScreen.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox formPane = new VBox(16);

        Label formTitle = new Label("Movie Details");
        formTitle.setStyle("-fx-text-fill: " + UI.getPrimary() + "; -fx-font-size: 20px; -fx-font-weight: 800;");

        VBox inputsBox = new VBox(12,
                titleInput, genreInput, languageInput,
                durationInput, yearInput, ratingInput,
                priceInput, imageUrlInput, descInput
        );

        HBox actionButtons = new HBox(10, add, update);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        formPane.getChildren().addAll(formTitle, inputsBox, actionButtons, status, backButton);

        BorderPane page = createMoviePage("Movies", "Manage cinema catalog", table, formPane);

        Scene scene = new Scene(page, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Movie Manager");
    }


    private BorderPane createMoviePage(String titleText, String subtitleText, TableView<?> table, VBox formPane) {
        BorderPane page = new BorderPane();
        page.setPadding(new Insets(28));
        page.setStyle(UI.getPageStyle());

        Label title = new Label(titleText);
        title.setStyle("-fx-text-fill: " + UI.getPrimary() + "; -fx-font-size: 28px; -fx-font-weight: 800;");

        Label subtitle = new Label(subtitleText);
        subtitle.setStyle("-fx-text-fill: " + UI.getMuted() + "; -fx-font-size: 14px;");

        VBox header = new VBox(6, title, subtitle);
        header.setPadding(new Insets(0, 0, 18, 0));

        VBox leftCard = new VBox(16);
        leftCard.setPadding(new Insets(18));
        leftCard.setStyle(UI.getSurfaceStyle());
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPrefHeight(540);
        leftCard.getChildren().addAll(header, table);

        ScrollPane formScroll = new ScrollPane(formPane);
        formScroll.setFitToWidth(true);
        formScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        formScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        formScroll.setPrefWidth(400);
        formScroll.setPannable(true);
        formScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        HBox content = new HBox(24, leftCard, formScroll);
        content.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(leftCard, Priority.ALWAYS);

        page.setCenter(content);
        return page;
    }
    public boolean checkID(String id) {
        try {
            Integer.parseInt(id);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
