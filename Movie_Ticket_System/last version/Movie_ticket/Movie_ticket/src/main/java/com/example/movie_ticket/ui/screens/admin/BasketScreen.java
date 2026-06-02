package com.example.movie_ticket.ui.screens.admin;

import com.example.movie_ticket.service.CouponService;
import com.example.movie_ticket.ui.screens.BasketManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BasketScreen {

    private final MovieBookingApp app;

    private boolean proceedToCheckout = false;
    private double  finalAmountToPay  = 0.0;

    public BasketScreen(MovieBookingApp app) {
        this.app = app;
    }

    public void show() {
        proceedToCheckout = false;
        finalAmountToPay  = 0.0;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Your Booked Tickets");

        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: #1E1E1E;");
        content.setMinWidth(560);

        Label header = new Label("MY RESERVATIONS");
        header.setTextFill(Color.web("#FFD700"));
        header.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        content.getChildren().add(header);

        List<CinemaBookingBridge.TicketItem> ticketItems = BasketManager.getInstance().getItems();

        if (ticketItems.isEmpty()) {
            buildEmptyState(content, dialog);
        } else {
            buildTicketList(content, dialog, ticketItems);
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setStyle("-fx-background-color: #1E1E1E;");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();

        if (proceedToCheckout) {
            new CheckoutScreen(app).showPaymentOptions(finalAmountToPay);
        }
    }

    private void buildEmptyState(VBox content, Dialog<Void> dialog) {
        Label emptyMsg = new Label("Your basket is empty.\nBook seats from a movie to get started.");
        emptyMsg.setTextFill(Color.GRAY);
        emptyMsg.setWrapText(true);

        Button closeBtn = new Button("CLOSE");
        closeBtn.setMaxWidth(Double.MAX_VALUE);
        closeBtn.setStyle("-fx-background-color: #FFD700; -fx-font-weight: bold; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> { dialog.setResult(null); dialog.close(); });

        content.getChildren().addAll(emptyMsg, closeBtn);
    }

    private void buildTicketList(VBox content, Dialog<Void> dialog,
                                 List<CinemaBookingBridge.TicketItem> ticketItems) {
        for (CinemaBookingBridge.TicketItem ticket : ticketItems) {
            content.getChildren().add(buildTicketCard(ticket, dialog));
        }

        double subtotal = BasketManager.getInstance().getTotal();
        finalAmountToPay = subtotal;

        content.getChildren().add(new Separator());

        HBox couponRow = new HBox(10);
        couponRow.setAlignment(Pos.CENTER_LEFT);

        TextField couponInput = new TextField();
        couponInput.setPromptText("Coupon code (e.g. CINEMA20)");
        couponInput.setPrefWidth(200);
        couponInput.setStyle(
                "-fx-background-color: #252525;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: #333;" +
                        "-fx-border-radius: 4;"
        );

        Button applyBtn = new Button("APPLY");
        applyBtn.setStyle(
                "-fx-background-color: #333;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        Label couponFeedbackLbl = new Label();
        couponFeedbackLbl.setFont(Font.font(12));
        couponRow.getChildren().addAll(couponInput, applyBtn, couponFeedbackLbl);

        VBox priceSummary = new VBox(5);
        priceSummary.setAlignment(Pos.CENTER_RIGHT);

        Label subtotalLbl = new Label("Subtotal: $" + String.format("%.2f", subtotal));
        subtotalLbl.setTextFill(Color.LIGHTGRAY);
        subtotalLbl.setFont(Font.font("Verdana", 14));

        Label totalLbl = new Label("TOTAL: $" + String.format("%.2f", subtotal));
        totalLbl.setTextFill(Color.web("#FFD700"));
        totalLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 22));

        priceSummary.getChildren().addAll(subtotalLbl, totalLbl);

        applyBtn.setOnAction(e -> {
            String code = couponInput.getText().trim();
            if (code.isEmpty()) {
                couponFeedbackLbl.setText("Please enter a code.");
                couponFeedbackLbl.setTextFill(Color.web("#F44336"));
                return;
            }
            double discountPercent = CouponService.getDiscountPercentage(code);
            if (discountPercent > 0) {
                double savings = subtotal * (discountPercent / 100.0);
                finalAmountToPay = subtotal - savings;
                subtotalLbl.setStyle("-fx-strikethrough: true; -fx-text-fill: gray;");
                totalLbl.setText("SALE TOTAL: $" + String.format("%.2f", finalAmountToPay));
                totalLbl.setTextFill(Color.web("#4CAF50"));
                couponFeedbackLbl.setText("-" + discountPercent + "% Applied!");
                couponFeedbackLbl.setTextFill(Color.web("#4CAF50"));
                applyBtn.setDisable(true);
                couponInput.setDisable(true);
            } else {
                couponFeedbackLbl.setText("Invalid or expired code.");
                couponFeedbackLbl.setTextFill(Color.web("#F44336"));
            }
        });

        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        actionButtons.setPadding(new Insets(10, 0, 0, 0));

        Button cancelBtn = new Button("CANCEL");
        cancelBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #FFD700;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        cancelBtn.setOnAction(e -> { dialog.setResult(null); dialog.close(); });

        Button checkoutBtn = new Button("PROCEED TO CHECKOUT  →");
        checkoutBtn.setStyle(
                "-fx-background-color: #FFD700;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: black;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 10 20;"
        );
        checkoutBtn.setOnAction(e -> {
            proceedToCheckout = true;
            dialog.setResult(null);
            dialog.close();
        });

        actionButtons.getChildren().addAll(cancelBtn, checkoutBtn);
        content.getChildren().addAll(couponRow, priceSummary, actionButtons);
    }

    private VBox buildTicketCard(CinemaBookingBridge.TicketItem ticket, Dialog<Void> dialog) {
        VBox card = new VBox(6);
        card.setStyle(
                "-fx-background-color: #1e1e1e;" +
                        "-fx-padding: 12 16;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #2a2a2a;" +
                        "-fx-border-radius: 10;"
        );

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Button removeBtn = new Button("✖");
        removeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #F44336;" +
                        "-fx-font-size: 13px;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0;"
        );
        removeBtn.setOnAction(e -> {
            BasketManager.getInstance().remove(ticket);
            app.updateBookButtonVisual(ticket.movie);
            dialog.setResult(null);
            dialog.close();
            Platform.runLater(this::show);
        });

        Label movieTitleLbl = new Label(ticket.movie.getTitle().toUpperCase());
        movieTitleLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label ticketTotalLbl = new Label("$" + String.format("%.2f", ticket.total()));
        ticketTotalLbl.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-font-size: 14px;");

        titleRow.getChildren().addAll(removeBtn, movieTitleLbl, spacer, ticketTotalLbl);

        String seatList = ticket.seats.stream()
                .sorted(Comparator
                        .comparing((CinemaBookingBridge.ShowSeat s) -> s.row)
                        .thenComparingInt(s -> s.col))
                .map(s -> s.row + s.col)
                .collect(Collectors.joining(", "));

        Label detailsLbl = new Label(
                "🕒 " + ticket.show.time +
                        "   🏛 " + ticket.show.hallName +
                        "   💺 " + ticket.seats.size() + " seat(s): " + seatList
        );
        detailsLbl.setStyle("-fx-text-fill: #777; -fx-font-size: 11px;");
        detailsLbl.setWrapText(true);

        Label perSeatLbl = new Label(
                "$" + String.format("%.2f", ticket.pricePerSeat) +
                        " × " + ticket.seats.size() + " seats"
        );
        perSeatLbl.setStyle("-fx-text-fill: #555; -fx-font-size: 11px;");

        card.getChildren().addAll(titleRow, detailsLbl, perSeatLbl);
        return card;
    }
}
