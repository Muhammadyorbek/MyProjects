package com.example.movie_ticket.ui.screens.admin;

import com.example.movie_ticket.database.DatabaseConnection;
import com.example.movie_ticket.model.Notification;
import com.example.movie_ticket.service.UserSession;
import com.example.movie_ticket.ui.screens.BasketManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Collectors;

public class CheckoutScreen {

    private final MovieBookingApp app;

    public CheckoutScreen(MovieBookingApp app) {
        this.app = app;
    }

    public void showPaymentOptions(double amountToPay) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Secure Checkout");

        VBox content = new VBox(20);
        content.setPadding(new Insets(35));
        content.setStyle(
                "-fx-background-color: #1E1E1E;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 2;"
        );
        content.setAlignment(Pos.CENTER);

        Label titleLbl = new Label("SELECT PAYMENT METHOD");
        titleLbl.setTextFill(Color.WHITE);
        titleLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 18));

        Label amountLbl = new Label("Amount Due: $" + String.format("%.2f", amountToPay));
        amountLbl.setTextFill(Color.web("#FFD700"));
        amountLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 24));

        Button creditCardBtn = buildCheckoutOptionButton("💳   PAY WITH CREDIT CARD");
        creditCardBtn.setOnAction(e -> {
            dialog.close();
            Platform.runLater(() -> showCreditCardInput(amountToPay));
        });

        Button cashBtn = buildCheckoutOptionButton("💵   PAY AT CINEMA (CASH)");
        cashBtn.setOnAction(e -> {
            dialog.close();
            Platform.runLater(() -> showNotificationOptions("Cash", amountToPay));
        });

        Button backBtn = new Button("← Back to Basket");
        backBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #888888;" +
                        "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> {
            dialog.close();
            Platform.runLater(() -> new BasketScreen(app).show());
        });

        content.getChildren().addAll(titleLbl, amountLbl, creditCardBtn, cashBtn, backBtn);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }
    private void showCreditCardInput(double amountToPay) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Credit Card Details");

        VBox content = new VBox(20);
        content.setPadding(new Insets(35));
        content.setStyle("-fx-background-color: #1E1E1E; -fx-border-color: #FFD700; -fx-border-width: 2;");
        content.setAlignment(Pos.CENTER);

        Label titleLbl = new Label("ENTER CARD NUMBER (DIGITS ONLY)");
        titleLbl.setTextFill(Color.WHITE);
        titleLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

        TextField cardInput = new TextField();
        cardInput.setPromptText("1234567890123456");
        cardInput.setPrefWidth(280);
        cardInput.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 16px;");

        cardInput.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        }));

        Button submitBtn = buildCheckoutOptionButton("CONFIRM PAYMENT");
        submitBtn.setOnAction(e -> {
            String input = cardInput.getText().trim();
            if (input.length() >= 13 && input.length() <= 19) {
                dialog.close();
                Platform.runLater(() -> showNotificationOptions("Credit Card", amountToPay));
            } else {
                titleLbl.setText("INVALID CARD LENGTH");
                titleLbl.setTextFill(Color.RED);
            }
        });

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888888; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            dialog.close();
            Platform.runLater(() -> showPaymentOptions(amountToPay));
        });

        content.getChildren().addAll(titleLbl, cardInput, submitBtn, backBtn);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }

    private void showNotificationOptions(String paymentMethod, double amountToPay) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Receipt Preference");

        VBox content = new VBox(20);
        content.setPadding(new Insets(35));
        content.setStyle(
                "-fx-background-color: #1E1E1E;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 2;"
        );
        content.setAlignment(Pos.CENTER);

        Label titleLbl = new Label("HOW SHOULD WE SEND YOUR RECEIPT?");
        titleLbl.setTextFill(Color.WHITE);
        titleLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

        Button emailBtn = buildCheckoutOptionButton("📧   SEND VIA EMAIL");
        emailBtn.setOnAction(e -> {
            dialog.close();
            Platform.runLater(() -> saveBookingToDatabase(paymentMethod, amountToPay, "Email"));
        });

        Button smsBtn = buildCheckoutOptionButton("📱   SEND VIA SMS");
        smsBtn.setOnAction(e -> {
            dialog.close();
            Platform.runLater(() -> saveBookingToDatabase(paymentMethod, amountToPay, "SMS"));
        });

        content.getChildren().addAll(titleLbl, emailBtn, smsBtn);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }

    private void saveBookingToDatabase(String paymentMethod, double amountToPay, String receiptMethod) {
        String bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                String insertBooking =
                        "INSERT INTO bookings (booking_id, user_id, payment_method, total_paid, notification_pref) " +
                                "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertBooking)) {
                    ps.setString(1, bookingId);
                    ps.setInt(2, UserSession.getInstance().getUserId());
                    ps.setString(3, paymentMethod);
                    ps.setDouble(4, amountToPay);
                    ps.setString(5, receiptMethod);
                    ps.executeUpdate();
                }

                String insertTicket =
                        "INSERT INTO booked_tickets " +
                                "(booking_id, movie_title, price, show_time) " +
                                "VALUES (?, ?, ?, ?)";

                try (PreparedStatement ps = conn.prepareStatement(insertTicket)) {
                    for (CinemaBookingBridge.TicketItem ticket :
                            BasketManager.getInstance().getItems()) {

                        String seats = ticket.seats.stream()
                                .map(s -> s.row + "" + s.col)
                                .collect(Collectors.joining(","));

                        String description =
                                ticket.movie.getTitle() +
                                        " [" + ticket.show.hallName +
                                        " @ " + ticket.show.rawTime + "]" +
                                        " Seats: " + seats;

                        ps.setString(1, bookingId);
                        ps.setString(2, description);
                        ps.setDouble(3, ticket.total());
                        ps.setString(4, ticket.show.rawTime);

                        System.out.println("Saving show_time: '" + ticket.show.rawTime + "'");

                        ps.executeUpdate();
                    }
                }

                conn.commit();
                //
                //
                int userId = UserSession.getInstance().getUserId();
                if (userId > 0) {
                    String ticketSummary = BasketManager.getInstance().getItems().stream()
                            .map(t -> "• " + t.movie.getTitle() +
                                    " | " + t.show.hallName +
                                    " @ " + t.show.time +
                                    " | Seats: " + t.seats.stream()
                                    .map(s -> s.row + "" + s.col)
                                    .collect(Collectors.joining(",")) +
                                    " | $" + String.format("%.2f", t.total()))
                            .collect(Collectors.joining("\n"));

                    Notification.send(userId,
                            "✅ Booking Confirmed — " + bookingId,
                            "Payment: " + paymentMethod + " | Receipt: " + receiptMethod +
                                    "\nTotal: $" + String.format("%.2f", amountToPay) +
                                    "\n\nTickets:\n" + ticketSummary
                    );
                }

                BasketManager.getInstance().getItems()
                        .forEach(ticket -> app.updateBookButtonVisual(ticket.movie));

                BasketManager.getInstance().clear();

                app.getPrimaryStage();
                new MovieGridScreen(app).loadMoviesAsync(
                        app.getSearchKeyword(),
                        app.getCityFilter(),
                        app.getGenreFilter(),
                        app.getYearFilter(),
                        app.getLanguageFilter()
                );

                new Alert(
                        Alert.AlertType.INFORMATION,
                        "✅ Booking Confirmed!\n\n" +
                                "Booking ID: " + bookingId + "\n" +
                                "Total Paid: $" + String.format("%.2f", amountToPay) + "\n" +
                                "Receipt sent via: " + receiptMethod
                ).showAndWait();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database error:\n" + e.getMessage()).showAndWait();
        }
    }

    private Button buildCheckoutOptionButton(String label) {
        Button btn = new Button(label);
        btn.setPrefWidth(280);
        btn.setPrefHeight(50);
        btn.setStyle(
                "-fx-background-color: #2A2A2A;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #555555;" +
                        "-fx-border-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #3A3A3A;" +
                        "-fx-text-fill: #FFD700;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-radius: 5;" +
                        "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: #2A2A2A;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #555555;" +
                        "-fx-border-radius: 5;" +
                        "-fx-cursor: hand;"
        ));
        return btn;
    }
}


