package com.example.movie_ticket.ui.screens.admin;

import com.example.movie_ticket.database.DatabaseConnection;
import com.example.movie_ticket.model.Cinema;
import com.example.movie_ticket.model.Movie;
import com.example.movie_ticket.ui.screens.BasketManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CinemaBookingBridge {

    private static final String BG          = "#0d0d0d";
    private static final String CARD        = "#181818";
    private static final String CARD2       = "#222222";
    private static final String YELLOW      = "#FFD700";
    private static final String YELLOW_DARK = "#e6c200";
    private static final String BOOKED      = "#2a2a2a";
    private static final String GREEN       = "#4CAF50";

    private final Movie         movie;
    private final Stage         ownerStage;
    private final Stage         dialog;
    private final List<TicketItem> basket = new ArrayList<>();

    private String   selectedCity;
    private Show     selectedShow;
    private double   selectedPrice;
    private Set<ShowSeat> confirmedSeats = new HashSet<>();

    private final BorderPane root    = new BorderPane();
    private final Button     backBtn = new Button("⬅  BACK");

    private Runnable onBasketUpdated;


    public static void open(Movie movie, Stage ownerStage) {
        new CinemaBookingBridge(movie, ownerStage).show();
    }

    public static void open(Movie movie, Stage ownerStage, Runnable onBasketUpdated) {
        CinemaBookingBridge bridge = new CinemaBookingBridge(movie, ownerStage);
        bridge.onBasketUpdated = onBasketUpdated;
        bridge.show();
    }

    private CinemaBookingBridge(Movie movie, Stage ownerStage) {
        this.movie      = movie;
        this.ownerStage = ownerStage;

        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(ownerStage);
        dialog.setTitle("Book Tickets — " + movie.getTitle());
        dialog.setMinWidth(1100);
        dialog.setMinHeight(700);
    }

    private void show() {
        root.setStyle("-fx-background-color: " + BG + ";");

        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(14, 40, 14, 40));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #000; -fx-border-color: #1a1a1a; -fx-border-width: 0 0 2 0;");

        Label logo = new Label("🎬  CINEMA RESERVE");
        logo.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        logo.setStyle("-fx-text-fill: " + YELLOW + ";");

        styleOutline(backBtn);
        backBtn.setVisible(false);

        Label basketIndicator = new Label();
        basketIndicator.setStyle("-fx-text-fill: " + YELLOW + "; -fx-font-size: 13px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(logo, spacer, basketIndicator, backBtn);
        root.setTop(topBar);

        showStep1_SelectShowtime();

        dialog.setScene(new Scene(root, 1200, 760));
        dialog.show();
    }


    private void showStep1_SelectShowtime() {
        backBtn.setVisible(true);
        backBtn.setOnAction(e -> dialog.close());

        HBox content = new HBox(0);
        content.setFillHeight(true);

        VBox leftSidebar = buildMovieSidebar();
        leftSidebar.setPrefWidth(300);
        leftSidebar.setMinWidth(300);

        VBox rightArea = new VBox(0);
        HBox.setHgrow(rightArea, Priority.ALWAYS);
        rightArea.setStyle("-fx-background-color: " + BG + ";");

        HBox stepHeader = new HBox();
        stepHeader.setPadding(new Insets(24, 40, 24, 40));
        stepHeader.setAlignment(Pos.CENTER_LEFT);
        stepHeader.setStyle("-fx-background-color: " + CARD + "; -fx-border-color: #222; -fx-border-width: 0 0 1 0;");

        Label stepTitle = new Label("SELECT YOUR SHOWTIME");
        stepTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        stepTitle.setStyle("-fx-text-fill: white;");

        Label stepSub = new Label("  Choose city → cinema → time");
        stepSub.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");
        stepHeader.getChildren().addAll(stepTitle, stepSub);

        HBox cityRow = new HBox(14);
        cityRow.setPadding(new Insets(20, 40, 20, 40));
        cityRow.setAlignment(Pos.CENTER_LEFT);
        cityRow.setStyle("-fx-background-color: " + CARD2 + ";");

        Label cityLbl = new Label("🏙  CITY:");
        cityLbl.setStyle("-fx-text-fill: " + YELLOW + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        ComboBox<String> cityBox = new ComboBox<>();
        cityBox.getItems().setAll(loadCities());
        cityBox.setPromptText("Select city...");
        cityBox.setPrefWidth(220);
        cityBox.setStyle("-fx-font-size: 13px; -fx-background-color: #1a1a1a; -fx-text-fill: white;");

        cityRow.getChildren().addAll(cityLbl, cityBox);

        VBox cinemaContainer = new VBox(16);
        cinemaContainer.setPadding(new Insets(24, 40, 24, 40));
        cinemaContainer.setFillWidth(true);

        Label pickHint = new Label("← Select a city to see available cinemas and showtimes");
        pickHint.setStyle("-fx-text-fill: #444; -fx-font-size: 14px;");
        cinemaContainer.getChildren().add(pickHint);

        cityBox.setOnAction(e -> {
            selectedCity = cityBox.getValue();
            if (selectedCity != null) populateCinemas(cinemaContainer);
        });

        ScrollPane scroll = new ScrollPane(cinemaContainer);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + "; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        rightArea.getChildren().addAll(stepHeader, cityRow, scroll);
        content.getChildren().addAll(leftSidebar, rightArea);
        root.setCenter(content);

        if (!cityBox.getItems().isEmpty()) cityBox.getSelectionModel().selectFirst();
    }

    private VBox buildMovieSidebar() {
        VBox sidebar = new VBox(16);
        sidebar.setPadding(new Insets(30, 24, 30, 24));
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-background-color: " + CARD + "; -fx-border-color: #222; -fx-border-width: 0 1 0 0;");

        ImageView poster = new ImageView();
        poster.setFitWidth(230);
        poster.setFitHeight(320);
        poster.setPreserveRatio(true);
        poster.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 20, 0, 0, 6);");

        if (movie.getImageUrl() != null && !movie.getImageUrl().isEmpty()) {
            try {
                Image img = new Image(movie.getImageUrl(), 230, 320, true, true, true);
                poster.setImage(img);
            } catch (Exception ex) {
                poster.setStyle("-fx-background-color: #222;");
            }
        }

        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(230, 320);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        poster.setClip(clip);

        Label title = new Label(movie.getTitle().toUpperCase());
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        title.setStyle("-fx-text-fill: white;");
        title.setWrapText(true);
        title.setMaxWidth(230);
        title.setAlignment(Pos.CENTER);

        Label genre = new Label((movie.getGenre() != null ? movie.getGenre() : "") + "  •  " + movie.getReleaseYear());
        genre.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
        genre.setAlignment(Pos.CENTER);

        Label rating = new Label("⭐ " + movie.getRating() + "   🌐 " + movie.getLanguage());
        rating.setStyle("-fx-text-fill: " + YELLOW + "; -fx-font-size: 12px;");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #2a2a2a;");

        Label synopsisHead = new Label("SYNOPSIS");
        synopsisHead.setStyle("-fx-text-fill: " + YELLOW + "; -fx-font-weight: bold; -fx-font-size: 11px;");

        Label synopsis = new Label(movie.getDescription() != null ? movie.getDescription() : "No description available.");
        synopsis.setWrapText(true);
        synopsis.setMaxWidth(240);
        synopsis.setStyle("-fx-text-fill: #666; -fx-font-size: 12px; -fx-line-spacing: 4;");

        sidebar.getChildren().addAll(poster, title, genre, rating, sep, synopsisHead, synopsis);
        return sidebar;
    }

    private void populateCinemas(VBox container) {
        container.getChildren().clear();
        List<Cinema> cinemas = loadCinemas(selectedCity);
        if (cinemas.isEmpty()) {
            Label none = new Label("No cinemas in " + selectedCity);
            none.setStyle("-fx-text-fill: #555;");
            container.getChildren().add(none);
            return;
        }
        boolean anyShows = false;
        for (Cinema cinema : cinemas) {
            List<Show> shows = loadShows(cinema.getName(), movie.getTitle());
            if (shows.isEmpty()) continue;
            anyShows = true;
            container.getChildren().add(buildCinemaCard(cinema, shows));
        }
        if (!anyShows) {
            Label none = new Label("No showtimes for this movie in " + selectedCity);
            none.setStyle("-fx-text-fill: #555;");
            container.getChildren().add(none);
        }
    }

    private VBox buildCinemaCard(Cinema cinema, List<Show> shows) {
        VBox card = new VBox(14);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-background-color: " + CARD + "; -fx-padding: 22; " +
                "-fx-background-radius: 14; -fx-border-color: #2a2a2a; " +
                "-fx-border-radius: 14; -fx-border-width: 1.5;");
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 22; " +
                "-fx-background-radius: 14; -fx-border-color: " + YELLOW + "; " +
                "-fx-border-radius: 14; -fx-border-width: 1.5;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + CARD + "; -fx-padding: 22; " +
                "-fx-background-radius: 14; -fx-border-color: #2a2a2a; " +
                "-fx-border-radius: 14; -fx-border-width: 1.5;"));

        Label cinemaName = new Label("🏛  " + cinema.getName());
        cinemaName.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        cinemaName.setStyle("-fx-text-fill: " + YELLOW + ";");

        VBox hallsBox = new VBox(12);
        buildHallRows(hallsBox, shows);

        card.getChildren().addAll(cinemaName, hallsBox);
        return card;
    }

    private void buildHallRows(VBox container, List<Show> shows) {
        Map<String, List<Show>> byHall = shows.stream()
                .collect(Collectors.groupingBy(s -> s.hallName, LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<String, List<Show>> entry : byHall.entrySet()) {
            String hallName = entry.getKey();
            List<Show> hallShows = entry.getValue();

            String upper = hallName.toUpperCase();
            String type  = upper.contains("VIP") ? "VIP" : upper.contains("IMAX") ? "IMAX" : "STANDARD";
            double price = upper.contains("VIP") ? 12.00 : upper.contains("IMAX") ? 10.00 : 7.00;

            VBox hallCard = new VBox(10);
            hallCard.setStyle("-fx-background-color: " + CARD2 + "; -fx-padding: 14 16; " +
                    "-fx-background-radius: 10; -fx-border-color: #2e2e2e; -fx-border-radius: 10;");

            HBox hallHeader = new HBox(12);
            hallHeader.setAlignment(Pos.CENTER_LEFT);

            Label typeBadge = new Label(type);
            typeBadge.setStyle("-fx-background-color: " +
                    (type.equals("VIP") ? "#7c3aed" : type.equals("IMAX") ? "#1d4ed8" : "#374151") +
                    "; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; " +
                    "-fx-padding: 3 8; -fx-background-radius: 4;");

            Label hallLbl  = new Label(hallName);
            hallLbl.setStyle("-fx-text-fill: #eee; -fx-font-size: 14px; -fx-font-weight: bold;");

            Label priceLbl = new Label("$" + String.format("%.2f", price) + " / seat");
            priceLbl.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");

            Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
            hallHeader.getChildren().addAll(typeBadge, hallLbl, spacer, priceLbl);

            FlowPane timeRow = new FlowPane(10, 8);
            for (Show show : hallShows) {
                Button tBtn = new Button("🕒  " + show.time);
                styleAccent(tBtn, "-fx-font-weight: bold; -fx-padding: 9 18; -fx-font-size: 13px;");
                final double fp = price;
                tBtn.setOnAction(e -> {
                    selectedShow  = show;
                    selectedPrice = fp;
                    showStep2_SeatMap();
                });
                timeRow.getChildren().add(tBtn);
            }
            hallCard.getChildren().addAll(hallHeader, timeRow);
            container.getChildren().add(hallCard);
        }
    }

    private void showStep2_SeatMap() {
        backBtn.setOnAction(e -> showStep1_SelectShowtime());

        HBox content = new HBox(0);
        content.setFillHeight(true);

        VBox leftSidebar = buildMovieSidebar();
        leftSidebar.setPrefWidth(300);
        leftSidebar.setMinWidth(300);

        VBox rightArea = new VBox(0);
        HBox.setHgrow(rightArea, Priority.ALWAYS);
        rightArea.setStyle("-fx-background-color: " + BG + ";");

        HBox stepHeader = new HBox();
        stepHeader.setPadding(new Insets(20, 40, 20, 40));
        stepHeader.setAlignment(Pos.CENTER_LEFT);
        stepHeader.setStyle("-fx-background-color: " + CARD + "; -fx-border-color: #222; -fx-border-width: 0 0 1 0;");

        Label stepTitle = new Label("CHOOSE YOUR SEATS");
        stepTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        stepTitle.setStyle("-fx-text-fill: white;");

        Label showInfo = new Label("  " + selectedShow.hallName + "  •  " + selectedShow.time);
        showInfo.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");
        stepHeader.getChildren().addAll(stepTitle, showInfo);


        VBox gridWrapper = new VBox(24);
        gridWrapper.setAlignment(Pos.CENTER);
        gridWrapper.setPadding(new Insets(30, 40, 30, 40));

        StackPane screenBanner = new StackPane();
        screenBanner.setMaxWidth(500);
        screenBanner.setPrefHeight(36);
        screenBanner.setStyle("-fx-background-color: linear-gradient(to bottom, #FFD700, #a08000); " +
                "-fx-background-radius: 6 6 0 0;");
        Label screenLbl = new Label("S  C  R  E  E  N");
        screenLbl.setStyle("-fx-text-fill: #000; -fx-font-weight: bold; -fx-font-size: 12px; -fx-letter-spacing: 4;");
        screenBanner.getChildren().add(screenLbl);

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);

        Set<ShowSeat> selectedSeats = new HashSet<>();
        ShowSeat[][] seats = loadSeats(selectedShow.id);

        Label selectedCountLbl = new Label("0 seats selected");
        selectedCountLbl.setStyle("-fx-text-fill: #aaa; -fx-font-size: 13px;");

        Label totalPriceLbl = new Label("Total: $0.00");
        totalPriceLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        totalPriceLbl.setStyle("-fx-text-fill: " + YELLOW + ";");

        for (int r = 0; r < seats.length; r++) {
            Label rowLbl = new Label(String.valueOf((char)('A' + r)));
            rowLbl.setStyle("-fx-text-fill: #555; -fx-font-size: 11px; -fx-font-weight: bold;");
            rowLbl.setPrefWidth(24);
            rowLbl.setAlignment(Pos.CENTER_RIGHT);
            grid.add(rowLbl, 0, r);

            for (int c = 0; c < seats[r].length; c++) {
                ShowSeat seat = seats[r][c];
                Button seatBtn = new Button(seat.row + seat.col);
                seatBtn.setPrefSize(46, 46);
                seatBtn.setFont(Font.font(10));
                applySeatStyle(seatBtn, seat, false);

                final ShowSeat fs = seat;
                seatBtn.setOnAction(e -> {
                    if (fs.isBooked) return;
                    if (selectedSeats.contains(fs)) {
                        selectedSeats.remove(fs);
                        applySeatStyle(seatBtn, fs, false);
                    } else {
                        selectedSeats.add(fs);
                        applySeatStyle(seatBtn, fs, true);
                    }
                    int cnt = selectedSeats.size();
                    selectedCountLbl.setText(cnt + " seat" + (cnt != 1 ? "s" : "") + " selected");
                    totalPriceLbl.setText("Total: $" + String.format("%.2f", cnt * selectedPrice));
                });

                int gridCol = (c >= 4) ? c + 2 : c + 1;
                grid.add(seatBtn, gridCol, r);
            }
        }

        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER);
        legend.getChildren().addAll(
                legendItem("#2a2a2a", "#888", "Available"),
                legendItem(YELLOW,    "black", "Selected"),
                legendItem("#1a1a1a", "#444", "Booked")
        );

        Button addToBasketBtn = new Button("🛒  ADD TO BASKET");
        addToBasketBtn.setPrefWidth(300);
        addToBasketBtn.setPrefHeight(52);
        styleAccent(addToBasketBtn, "-fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 14 40;");
        addToBasketBtn.setOnAction(e -> {
            if (selectedSeats.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "No Seats Selected",
                        "Please select at least one seat.");
                return;
            }

            boolean saved = saveSeatsToDatabase(selectedSeats);
            if (!saved) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Could not save seats. Please try again.");
                return;
            }
            TicketItem ticket = new TicketItem(
                    movie, selectedShow, new HashSet<>(selectedSeats), selectedPrice
            );

            BasketManager.getInstance().updateOrAdd(ticket);

            if (onBasketUpdated != null) onBasketUpdated.run();

            showStep3_Confirmation(new HashSet<>(selectedSeats));
        });

        gridWrapper.getChildren().addAll(screenBanner, grid, legend, selectedCountLbl, totalPriceLbl, addToBasketBtn);

        ScrollPane scroll = new ScrollPane(gridWrapper);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + "; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        rightArea.getChildren().addAll(stepHeader, scroll);
        content.getChildren().addAll(leftSidebar, rightArea);
        root.setCenter(content);
    }

    private void showStep3_Confirmation(Set<ShowSeat> bookedSeats) {
        VBox confirmScreen = new VBox(28);
        confirmScreen.setAlignment(Pos.CENTER);
        confirmScreen.setPadding(new Insets(60));
        confirmScreen.setStyle("-fx-background-color: " + BG + ";");

        ImageView poster = new ImageView();
        poster.setFitWidth(180);
        poster.setFitHeight(260);
        poster.setPreserveRatio(true);
        if (movie.getImageUrl() != null && !movie.getImageUrl().isEmpty()) {
            try {
                poster.setImage(new Image(movie.getImageUrl(), 180, 260, true, true, true));
            } catch (Exception ignored) {}
        }
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(180, 260);
        clip.setArcWidth(14); clip.setArcHeight(14);
        poster.setClip(clip);
        poster.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.4), 30, 0, 0, 0);");

        Label check = new Label("✔");
        check.setStyle("-fx-text-fill: " + GREEN + "; -fx-font-size: 52px;");

        Label added = new Label("ADDED TO BASKET!");
        added.setFont(Font.font("Verdana", FontWeight.BOLD, 26));
        added.setStyle("-fx-text-fill: white;");

        VBox ticketBox = new VBox(8);
        ticketBox.setAlignment(Pos.CENTER);
        ticketBox.setStyle("-fx-background-color: " + CARD + "; -fx-padding: 24 40; " +
                "-fx-background-radius: 14; -fx-border-color: " + YELLOW + "; " +
                "-fx-border-radius: 14; -fx-border-width: 1.2;");
        ticketBox.setMaxWidth(500);

        Label movieLbl = new Label(movie.getTitle().toUpperCase());
        movieLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        movieLbl.setStyle("-fx-text-fill: " + YELLOW + ";");

        Label showLbl = new Label("🕒  " + selectedShow.time + "   🏛  " + selectedShow.hallName);
        showLbl.setStyle("-fx-text-fill: #aaa; -fx-font-size: 13px;");

        String seatList = bookedSeats.stream()
                .sorted(Comparator.comparing((ShowSeat s) -> s.row).thenComparingInt(s -> s.col))
                .map(s -> s.row + s.col)
                .collect(Collectors.joining(", "));

        Label seatsLbl = new Label("Seats: " + seatList);
        seatsLbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        seatsLbl.setWrapText(true);
        seatsLbl.setMaxWidth(420);

        double total = bookedSeats.size() * selectedPrice;
        Label totalLbl = new Label("Total: $" + String.format("%.2f", total));
        totalLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        totalLbl.setStyle("-fx-text-fill: " + YELLOW + ";");

        ticketBox.getChildren().addAll(movieLbl, showLbl, seatsLbl, new Separator(), totalLbl);


        HBox btnRow = new HBox(20);
        btnRow.setAlignment(Pos.CENTER);

        Button bookMoreBtn = new Button("+ BOOK MORE SEATS");
        styleOutline(bookMoreBtn);
        bookMoreBtn.setOnAction(e -> showStep1_SelectShowtime());

        Button closeBtn = new Button("✔  GO TO BASKET");
        styleAccent(closeBtn, "-fx-font-weight: bold; -fx-padding: 12 30;");
        closeBtn.setOnAction(e -> dialog.close());

        btnRow.getChildren().addAll(bookMoreBtn, closeBtn);

        confirmScreen.getChildren().addAll(poster, check, added, ticketBox, btnRow);

        ScrollPane scroll = new ScrollPane(confirmScreen);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + "; -fx-border-color: transparent;");

        root.setCenter(scroll);
        backBtn.setOnAction(ev -> showStep1_SelectShowtime());
    }

    private List<String> loadCities() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM cities ORDER BY name";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString("name"));
        } catch (SQLException e) {
            System.err.println("loadCities error: " + e.getMessage());
        }
        if (list.isEmpty()) list.add("Tashkent");
        return list;
    }

    private List<Cinema> loadCinemas(String cityName) {
        List<Cinema> list = new ArrayList<>();
        String sql =
                "SELECT c.cinema_id, c.name " +
                        "FROM cinemas c " +
                        "JOIN cities ct ON c.city_id = ct.city_id " +
                        "WHERE ct.name = ? " +
                        "ORDER BY c.name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cityName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(new Cinema(rs.getInt("cinema_id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.err.println("loadCinemas error: " + e.getMessage());
        }
        return list;
    }

    private List<Show> loadShows(String cinemaName, String movieTitle) {
        List<Show> list = new ArrayList<>();
        String sql =
                "SELECT s.show_id, s.hall_name, s.show_time " +
                        "FROM shows s " +
                        "JOIN movies m ON s.movie_id = m.movie_id " +
                        "WHERE s.cinema_name = ? AND m.title = ? " +
                        "ORDER BY s.show_time";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cinemaName);
            ps.setString(2, movieTitle);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String raw  = rs.getString("show_time");
                    String time = (raw != null && raw.length() >= 16)
                            ? raw.substring(11, 16) : raw;
                    String hall = rs.getString("hall_name");
                    String type = hall.toUpperCase().contains("VIP")  ? "VIP"
                            : hall.toUpperCase().contains("IMAX") ? "IMAX"
                            : "Standard";
                    list.add(new Show(rs.getInt("show_id"), hall, type, time, raw));
                }
            }
        } catch (SQLException e) {
            System.err.println("loadShows: " + e.getMessage());
        }
        return list;
    }

    private ShowSeat[][] loadSeats(int showId) {
        ShowSeat[][] grid = new ShowSeat[5][8];
        String sql = "SELECT row_label, col_num, is_booked FROM seats WHERE show_id = ? ORDER BY row_label, col_num";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, showId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String row = rs.getString("row_label");
                    int col    = rs.getInt("col_num");
                    boolean bk = rs.getBoolean("is_booked");
                    int ri = row.toUpperCase().charAt(0) - 'A';
                    int ci = col - 1;
                    if (ri >= 0 && ri < 5 && ci >= 0 && ci < 8)
                        grid[ri][ci] = new ShowSeat(showId, row, col, bk);
                }
            }
        } catch (SQLException e) { System.err.println("loadSeats: " + e.getMessage()); }

        for (int r = 0; r < 5; r++)
            for (int cl = 0; cl < 8; cl++)
                if (grid[r][cl] == null)
                    grid[r][cl] = new ShowSeat(showId, String.valueOf((char)('A'+r)), cl+1, false);
        return grid;
    }

    private boolean saveSeatsToDatabase(Set<ShowSeat> seats) {
        String sql = "UPDATE seats SET is_booked = TRUE " +
                "WHERE show_id = ? AND row_label = ? AND col_num = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            c.setAutoCommit(false);
            for (ShowSeat s : seats) {
                ps.setInt(1, s.showId);
                ps.setString(2, s.row);
                ps.setInt(3, s.col);
                ps.addBatch();
            }
            ps.executeBatch();
            c.commit();
            System.out.println("✅ Saved " + seats.size() + " seats to DB for show " + selectedShow.id);
            return true;
        } catch (SQLException e) {
            System.err.println("saveSeats error: " + e.getMessage());
            return false;
        }
    }

    private void applySeatStyle(Button btn, ShowSeat seat, boolean selected) {
        if (seat.isBooked) {
            btn.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: #333; " +
                    "-fx-background-radius: 8; -fx-border-color: #222; -fx-border-radius: 8;");
            btn.setDisable(true);
        } else if (selected) {
            btn.setStyle("-fx-background-color: " + YELLOW + "; -fx-text-fill: black; " +
                    "-fx-font-weight: bold; -fx-background-radius: 8; " +
                    "-fx-border-color: " + YELLOW_DARK + "; -fx-border-radius: 8; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: #2a2a2a; -fx-text-fill: #ccc; " +
                    "-fx-border-color: #3a3a3a; -fx-border-radius: 8; " +
                    "-fx-background-radius: 8; -fx-cursor: hand;");
        }
    }

    private void styleAccent(Button btn, String extra) {
        String base = "-fx-background-color: " + YELLOW + "; -fx-text-fill: black; " +
                "-fx-cursor: hand; -fx-background-radius: 8; " + extra;
        String hover = "-fx-background-color: " + YELLOW_DARK + "; -fx-text-fill: black; " +
                "-fx-cursor: hand; -fx-background-radius: 8; " + extra;
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e  -> btn.setStyle(base));
    }

    private void styleOutline(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + YELLOW +
                "; -fx-border-color: " + YELLOW + "; -fx-border-radius: 6; " +
                "-fx-padding: 10 20; -fx-font-weight: bold; -fx-cursor: hand;");
    }

    private HBox legendItem(String bg, String fg, String label) {
        Button s = new Button();
        s.setPrefSize(22, 22);
        s.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 5; " +
                "-fx-border-color: " + fg + "; -fx-border-radius: 5; -fx-border-width: 1;");
        s.setDisable(true);
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
        HBox row = new HBox(8, s, l);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static class Show {
        public final int    id;
        public final String hallName, hallType, time, rawTime;

        public Show(int id, String hallName, String hallType,
                    String time, String rawTime) {
            this.id       = id;
            this.hallName = hallName;
            this.hallType = hallType;
            this.time     = time;
            this.rawTime  = rawTime;
        }
    }

    public static class ShowSeat {
        public final int showId, col; public final String row; public final boolean isBooked;
        public ShowSeat(int showId, String row, int col, boolean isBooked) {
            this.showId=showId; this.row=row; this.col=col; this.isBooked=isBooked;
        }
        @Override public boolean equals(Object o) {
            if (!(o instanceof ShowSeat)) return false;
            ShowSeat s = (ShowSeat)o;
            return showId==s.showId && col==s.col && row.equals(s.row);
        }
        @Override public int hashCode() { return Objects.hash(showId, row, col); }
    }

    public static class TicketItem {
        public final Movie movie; public final Show show;
        public final Set<ShowSeat> seats; public final double pricePerSeat;
        public TicketItem(Movie movie, Show show, Set<ShowSeat> seats, double pricePerSeat) {
            this.movie=movie; this.show=show; this.seats=seats; this.pricePerSeat=pricePerSeat;
        }
        public double total() { return seats.size() * pricePerSeat; }
    }
}


