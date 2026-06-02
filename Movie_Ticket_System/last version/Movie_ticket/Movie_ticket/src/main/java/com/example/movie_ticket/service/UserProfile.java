package com.example.movie_ticket.service;

import com.example.movie_ticket.database.UserDAO;
import com.example.movie_ticket.model.Notification;
import com.example.movie_ticket.ui.screens.admin.MovieBookingApp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.example.movie_ticket.ui.screens.UI.MUTED;

public class UserProfile {

    private static final String BLACK      = "#121212";
    private static final String DARK_CARD  = "#1E1E1E";
    private static final String CARD2      = "#252525";
    private static final String YELLOW     = "#FFD700";
    private static final String BORDER     = "#333333";
    private static final String LIGHT_TEXT = "#CCCCCC";
    private static final String RED        = "#F44336";
    private static final String GREEN      = "#4CAF50";

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public void display(Stage mainStage, ImageView navAvatar) {
        Stage profileStage = new Stage();
        profileStage.setTitle("Cinema Reserve | Profile");
        mainStage.hide();

        HBox root = new HBox(0);
        root.setStyle("-fx-background-color: " + BLACK + ";");

        VBox ticketsList = new VBox(12);

        root.getChildren().addAll(
                buildSidebar(profileStage, mainStage, navAvatar),
                buildTicketsPanel(ticketsList),
                buildNotificationsPanel()
        );

        profileStage.setScene(new Scene(root, 1350, 900));
        profileStage.setOnCloseRequest(e -> mainStage.show());
        profileStage.show();
    }

    private VBox buildSidebar(Stage profileStage, Stage mainStage, ImageView navAvatar) {
        VBox sidebar = new VBox(18);
        sidebar.setPadding(new Insets(24, 32, 24, 32));
        sidebar.setMinWidth(340);
        sidebar.setMaxWidth(340);
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle(
                "-fx-background-color: " + DARK_CARD + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-width: 0 1 0 0;"
        );

        Button backBtn = outlineBtn("← BACK");
        backBtn.setOnAction(e -> { profileStage.close(); mainStage.show(); });
        HBox backRow = new HBox(backBtn);
        backRow.setAlignment(Pos.TOP_LEFT);

        ImageView avatarView = new ImageView(navAvatar.getImage());
        avatarView.setFitWidth(110);
        avatarView.setFitHeight(110);
        avatarView.setClip(new Circle(55, 55, 55));

        Button changePhotoBtn = new Button("CHANGE PHOTO");
        changePhotoBtn.setMaxWidth(Double.MAX_VALUE);
        changePhotoBtn.setStyle(
                "-fx-background-color: " + BORDER + "; -fx-text-fill: white;" +
                        "-fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8; -fx-background-radius: 6;"
        );
        changePhotoBtn.setOnAction(e -> pickAvatar(profileStage, avatarView, navAvatar));
        VBox avatarBox = new VBox(10, avatarView, changePhotoBtn);
        avatarBox.setAlignment(Pos.CENTER);

        VBox coinsBox = new VBox(4);
        coinsBox.setAlignment(Pos.CENTER);
        coinsBox.setStyle(
                "-fx-background-color:" + CARD2 + "; -fx-border-color:" + BORDER + ";" +
                        "-fx-border-radius:10; -fx-background-radius:10; -fx-padding:14;"
        );
        Label coinsHead = new Label("COLLECTED COINS");
        coinsHead.setStyle("-fx-text-fill:" + LIGHT_TEXT + "; -fx-font-size:10px; -fx-font-weight:bold;");
        Label coinsAmt = new Label("🪙 " + UserSession.getInstance().getCoinsBalance());
        coinsAmt.setStyle("-fx-text-fill:" + YELLOW + "; -fx-font-size:22px; -fx-font-weight:bold;");
        coinsBox.getChildren().addAll(coinsHead, coinsAmt);

        UserDAO.UserProfileData profile = UserDAO.loadProfile();

        String locked = lockedStyle();
        TextField fnFld    = formField(profile.name, false);
        TextField lnFld    = formField(profile.surname,  false);
        TextField emailFld = formField(profile.email,     false);
        TextField phoneFld = formField(profile.phone,     false);

        GridPane grid = new GridPane();
        grid.setVgap(8); grid.setHgap(8);
        grid.addRow(0, fieldLabel("FIRST NAME")); grid.addRow(1, fnFld);
        grid.addRow(2, fieldLabel("LAST NAME"));  grid.addRow(3, lnFld);
        grid.addRow(4, fieldLabel("EMAIL"));      grid.addRow(5, emailFld);
        grid.addRow(6, fieldLabel("PHONE"));      grid.addRow(7, phoneFld);
        for (var f : new TextField[]{fnFld, lnFld, emailFld, phoneFld})
            GridPane.setHgrow(f, Priority.ALWAYS);

        Button editSaveBtn = yellowBtn("EDIT PROFILE");
        editSaveBtn.setOnAction(e -> {
            if ("EDIT PROFILE".equals(editSaveBtn.getText())) {
                fnFld.setEditable(true);    fnFld.setStyle(activeStyle());
                lnFld.setEditable(true);    lnFld.setStyle(activeStyle());
                emailFld.setEditable(true); emailFld.setStyle(activeStyle());
                phoneFld.setEditable(true); phoneFld.setStyle(activeStyle());
                editSaveBtn.setText("SAVE PROFILE");
            } else {
                String fn = fnFld.getText().trim();
                String ln = lnFld.getText().trim();
                String em = emailFld.getText().trim();
                String ph = phoneFld.getText().trim();

                if (fn.isEmpty() || ln.isEmpty() || em.isEmpty()) {
                    new Alert(Alert.AlertType.WARNING,
                            "First name, last name and email are required.").show();
                    return;
                }
                if (!em.contains("@") || !em.contains(".")) {
                    new Alert(Alert.AlertType.WARNING,
                            "Please enter a valid email.").show();
                    return;
                }
                if (UserDAO.saveProfile(fn, ln, em, ph)) {
                    UserSession.getInstance().setFirstName(fn);
                    UserSession.getInstance().setLastName(ln);
                    UserSession.getInstance().setEmail(em);
                    UserSession.getInstance().setPhone(ph);

                    fnFld.setEditable(false);    fnFld.setStyle(locked);
                    lnFld.setEditable(false);    lnFld.setStyle(locked);
                    emailFld.setEditable(false); emailFld.setStyle(locked);
                    phoneFld.setEditable(false); phoneFld.setStyle(locked);
                    editSaveBtn.setText("EDIT PROFILE");
                    new Alert(Alert.AlertType.INFORMATION,
                            "Profile updated!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR,
                            "Failed to save. Try again.").show();
                }
            }
        });

        Button logoutBtn = dangerBtn("LOG OUT");
        logoutBtn.setOnAction(e -> {
            UserSession.getInstance().cleanUserSession();
            profileStage.close();
            mainStage.close();
            try { new MovieBookingApp().start(new Stage()); }
            catch (Exception ex) { ex.printStackTrace(); }
        });

        sidebar.getChildren().addAll(
                backRow, avatarBox, coinsBox, grid, editSaveBtn, logoutBtn
        );
        return sidebar;
    }

    private VBox buildTicketsPanel(VBox ticketsList) {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(24));
        HBox.setHgrow(panel, Priority.ALWAYS);
        panel.setStyle("-fx-background-color:" + BLACK + ";");

        Label title = sectionTitle("MY TICKETS");

        ticketsList.setStyle("-fx-background-color:" + BLACK + ";");
        fillTicketsList(ticketsList);

        ScrollPane scroll = styledScroll(ticketsList);
        panel.getChildren().addAll(title, scroll);
        return panel;
    }

    private void fillTicketsList(VBox list) {
        list.getChildren().clear();
        List<UserDAO.TicketData> tickets = UserDAO.loadTickets();

        if (tickets.isEmpty()) {
            Label empty = mutedLabel("No tickets booked yet. Go grab some! 🎬");
            list.getChildren().add(empty);
            return;
        }
        for (UserDAO.TicketData t : tickets)
            list.getChildren().add(buildTicketCard(t, list));
    }

    private VBox buildTicketCard(UserDAO.TicketData ticket, VBox parentList) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle(cardStyle());

        Label titleLbl = new Label("🎬 " + ticket.movieTitle);
        titleLbl.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;"
        );
        titleLbl.setWrapText(true);

        HBox chips = new HBox(20);
        chips.setAlignment(Pos.CENTER_LEFT);
        chips.getChildren().addAll(
                chip("📅 Booked",  ticket.bookedAt),
                chip("💳 Payment", ticket.payMethod),
                chip("💰 Paid",    "$" + String.format("%.2f", ticket.price))
        );

        HBox showTimeRow = new HBox(8);
        showTimeRow.setAlignment(Pos.CENTER_LEFT);

        if (ticket.showTime != null) {
            boolean isFuture = ticket.showTime.isAfter(LocalDateTime.now());
            String showStr   = ticket.showTime.format(DISPLAY_FMT);

            Label clockIcon = new Label("🕒");
            Label showLbl   = new Label("Show: " + showStr);
            showLbl.setStyle(
                    "-fx-text-fill: " + (isFuture ? LIGHT_TEXT : MUTED) + ";" +
                            "-fx-font-size: 12px;"
            );

            Label badge = new Label(isFuture ? "UPCOMING" : "PAST");
            badge.setStyle(
                    "-fx-background-color: " + (isFuture ? GREEN : "#555") + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 9px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 2 6;" +
                            "-fx-background-radius: 4;"
            );

            showTimeRow.getChildren().addAll(clockIcon, showLbl, badge);
        } else {
            Label showLbl = new Label("🕒 " + extractTimeFromTitle(ticket.movieTitle));
            showLbl.setStyle("-fx-text-fill: " + LIGHT_TEXT + "; -fx-font-size: 12px;");
            showTimeRow.getChildren().add(showLbl);
        }

        Label bookingLbl = new Label("🔖 " + ticket.bookingId);
        bookingLbl.setStyle("-fx-text-fill: #555; -fx-font-size: 11px;");

        card.getChildren().addAll(titleLbl, chips, showTimeRow, bookingLbl);

        if (ticket.showTime != null) {
            if (ticket.isRefundEligible()) {
                VBox refundBox = new VBox(6);
                refundBox.setStyle(
                        "-fx-background-color: #0d1f0d;" +
                                "-fx-padding: 10 14;" +
                                "-fx-background-radius: 8;" +
                                "-fx-border-color: " + GREEN + ";" +
                                "-fx-border-radius: 8;" +
                                "-fx-border-width: 0 0 0 3;"
                );

                long hoursLeft = java.time.Duration
                        .between(LocalDateTime.now(), ticket.showTime).toHours();
                long minsLeft  = java.time.Duration
                        .between(LocalDateTime.now(), ticket.showTime).toMinutes() % 60;

                Label refundInfo = new Label(
                        "✅ Free cancellation available  •  " +
                                hoursLeft + "h " + minsLeft + "m until show"
                );
                refundInfo.setStyle(
                        "-fx-text-fill: " + GREEN + ";" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;"
                );

                Label refundPolicy = new Label(
                        "Full refund of $" + String.format("%.2f", ticket.price)
                );
                refundPolicy.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11px;");

                Button cancelBtn = dangerBtn("✖  CANCEL & GET REFUND");
                cancelBtn.setMaxWidth(220);
                cancelBtn.setPrefHeight(36);
                cancelBtn.setOnAction(e ->
                        handleCancellation(ticket, card, parentList)
                );

                refundBox.getChildren().addAll(refundInfo, refundPolicy, cancelBtn);
                card.getChildren().add(refundBox);

            } else {
                boolean alreadyPast = ticket.showTime.isBefore(LocalDateTime.now());

                VBox noRefundBox = new VBox(4);
                noRefundBox.setStyle(
                        "-fx-background-color: #1a1010;" +
                                "-fx-padding: 10 14;" +
                                "-fx-background-radius: 8;" +
                                "-fx-border-color: #8B0000;" +
                                "-fx-border-radius: 8;" +
                                "-fx-border-width: 0 0 0 3;"
                );
                Label noRefundLbl = new Label(
                        alreadyPast
                                ? "⛔  Show has already passed — no refund available."
                                : "⚠️  Less than 2 hours to show — cancellation window closed."
                );
                noRefundLbl.setStyle(
                        "-fx-text-fill: #cc4444;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;"
                );
                noRefundBox.getChildren().add(noRefundLbl);
                card.getChildren().add(noRefundBox);
            }
        } else {
            Label unknownLbl = new Label("ℹ  Show time unavailable — contact support for cancellation.");
            unknownLbl.setStyle("-fx-text-fill: #777; -fx-font-size: 11px; -fx-font-style: italic;");
            card.getChildren().add(unknownLbl);
        }

        return card;
    }

    private String extractTimeFromTitle(String movieTitle) {
        try {
            int atIdx    = movieTitle.indexOf("@ ");
            int closeIdx = movieTitle.indexOf("]", atIdx);
            if (atIdx >= 0 && closeIdx > atIdx)
                return movieTitle.substring(atIdx + 2, closeIdx).trim();
        } catch (Exception ignored) {}
        return movieTitle;
    }

    private void handleCancellation(UserDAO.TicketData ticket,
                                    VBox card, VBox parentList) {
        if (!ticket.isRefundEligible()) {
            new Alert(Alert.AlertType.WARNING,
                    "Cancellation window has closed.\n" +
                            "Tickets can only be cancelled more than 2 hours before the show."
            ).show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Ticket");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Cancel this ticket and get a refund?\n\n" +
                        "💰 Refund: $" + String.format("%.2f", ticket.price) + "\n" +
                        "Show: " + (ticket.showTime != null
                        ? ticket.showTime.format(DISPLAY_FMT) : "N/A") + "\n\n" +
                        "This cannot be undone."
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response != ButtonType.OK) return;

            boolean deleted = UserDAO.deleteTicket(ticket.ticketId);
            if (!deleted) {
                new Alert(Alert.AlertType.ERROR,
                        "Failed to cancel. Please try again.").show();
                return;
            }

            parentList.getChildren().remove(card);

            if (parentList.getChildren().isEmpty()) {
                parentList.getChildren().add(
                        mutedLabel("No tickets booked yet. Go grab some! 🎬")
                );
            }


            int userId = UserSession.getInstance().getUserId();
            Notification.send(userId,
                    "❌ Ticket Cancelled — Refund Issued",
                    "Booking " + ticket.bookingId + " was cancelled.\n" +
                            "Refund of $" + String.format("%.2f", ticket.price) +
                            " has been processed.\n" +
                            "Show was: " + (ticket.showTime != null
                            ? ticket.showTime.format(DISPLAY_FMT) : "N/A")
            );

            new Alert(Alert.AlertType.INFORMATION,
                    "✅ Cancelled successfully!\n" +
                            "Refund of $" + String.format("%.2f", ticket.price) +
                            " has been processed."
            ).show();
        });
    }

    private VBox buildNotificationsPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(24));
        HBox.setHgrow(panel, Priority.ALWAYS);
        panel.setStyle("-fx-background-color:" + BLACK + ";");

        HBox headerRow = new HBox(10);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.getChildren().add(sectionTitle("NOTIFICATIONS"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button markAllBtn = outlineBtn("✔ Mark All Read");
        markAllBtn.setStyle(markAllBtn.getStyle() + "-fx-font-size:11px;");
        headerRow.getChildren().addAll(spacer, markAllBtn);

        VBox notifList = new VBox(12);
        notifList.setStyle("-fx-background-color:" + BLACK + ";");
        fillNotificationsList(notifList);

        markAllBtn.setOnAction(e -> {
            Notification.markAllRead(UserSession.getInstance().getUserId());
            fillNotificationsList(notifList);
        });

        ScrollPane scroll = styledScroll(notifList);
        panel.getChildren().addAll(headerRow, scroll);
        return panel;
    }

    private void fillNotificationsList(VBox list) {
        list.getChildren().clear();
        int userId = UserSession.getInstance().getUserId();

        if (userId <= 0) {
            list.getChildren().add(mutedLabel("Sign in to see notifications."));
            return;
        }

        List<com.example.movie_ticket.model.Notification> notifications =
                Notification.loadForCurrentUser();

        if (notifications.isEmpty()) {
            list.getChildren().add(mutedLabel("No notifications yet."));
            return;
        }

        for (com.example.movie_ticket.model.Notification n : notifications)
            list.getChildren().add(buildNotifCard(n));
    }

    private VBox buildNotifCard(com.example.movie_ticket.model.Notification n) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        applyNotifStyle(card, n.isRead());

        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        if (!n.isRead()) {
            Label dot = new Label("●");
            dot.setStyle("-fx-text-fill:" + YELLOW + "; -fx-font-size:10px;");
            topRow.getChildren().add(dot);
        }

        Label titleLbl = new Label("🔔 " + n.getTitle());
        titleLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        titleLbl.setStyle("-fx-text-fill:" + YELLOW + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label dateLbl = new Label(n.getCreatedAt().format(DISPLAY_FMT));
        dateLbl.setStyle("-fx-text-fill:#555; -fx-font-size:10px;");
        topRow.getChildren().addAll(titleLbl, sp, dateLbl);

        Label descLbl = new Label(n.getDescription());
        descLbl.setStyle("-fx-text-fill:" + LIGHT_TEXT + "; -fx-font-size:12px;");
        descLbl.setWrapText(true);
        card.getChildren().addAll(topRow, descLbl);

        if (!n.isRead()) {
            card.setStyle(card.getStyle() + "-fx-cursor:hand;");
            card.setOnMouseClicked(e -> {
                Notification.markRead(n.getId());
                applyNotifStyle(card, true);
                topRow.getChildren().removeIf(
                        node -> node instanceof Label && "●".equals(((Label) node).getText())
                );
                card.setOnMouseClicked(null);
            });
        }
        return card;
    }

    private Button yellowBtn(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        String base = "-fx-background-color:" + YELLOW + "; -fx-text-fill:black;" +
                "-fx-font-weight:bold; -fx-cursor:hand; -fx-padding:10; -fx-background-radius:6;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(base.replace(YELLOW, "#e6c200")));
        btn.setOnMouseExited(e  -> btn.setStyle(base));
        return btn;
    }

    private Button dangerBtn(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        String base = "-fx-background-color:transparent; -fx-text-fill:" + RED +
                "; -fx-font-weight:bold; -fx-border-color:" + RED +
                "; -fx-border-radius:5; -fx-cursor:hand; -fx-padding:10;";
        String hover = "-fx-background-color:" + RED + "; -fx-text-fill:white;" +
                "-fx-font-weight:bold; -fx-border-color:" + RED +
                "; -fx-border-radius:5; -fx-cursor:hand; -fx-padding:10;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e  -> btn.setStyle(base));
        return btn;
    }

    private Button outlineBtn(String text) {
        Button btn = new Button(text);
        String base = "-fx-background-color:transparent; -fx-text-fill:" + YELLOW +
                "; -fx-border-color:" + YELLOW +
                "; -fx-border-radius:5; -fx-cursor:hand; -fx-padding:5 12; -fx-font-weight:bold;";
        String hover = "-fx-background-color:" + YELLOW + "; -fx-text-fill:black;" +
                "-fx-border-color:" + YELLOW +
                "; -fx-border-radius:5; -fx-cursor:hand; -fx-padding:5 12; -fx-font-weight:bold;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e  -> btn.setStyle(base));
        return btn;
    }

    private TextField formField(String value, boolean editable) {
        TextField f = new TextField(value != null ? value : "");
        f.setEditable(editable);
        f.setMaxWidth(Double.MAX_VALUE);
        f.setStyle(editable ? activeStyle() : lockedStyle());
        return f;
    }

    private Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:" + LIGHT_TEXT + "; -fx-font-weight:bold; -fx-font-size:11px;");
        return l;
    }

    private Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        l.setStyle("-fx-text-fill:" + YELLOW + ";");
        return l;
    }

    private Label mutedLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:gray; -fx-font-style:italic;");
        return l;
    }

    private VBox chip(String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:#666; -fx-font-size:10px;");
        Label val = new Label(value != null ? value : "N/A");
        val.setStyle("-fx-text-fill:" + LIGHT_TEXT + "; -fx-font-size:12px; -fx-font-weight:bold;");
        return new VBox(2, lbl, val);
    }

    private ScrollPane styledScroll(VBox content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background:" + BLACK + "; -fx-background-color:" + BLACK + "; -fx-border-width:0;");
        VBox.setVgrow(sp, Priority.ALWAYS);
        return sp;
    }

    private String lockedStyle() {
        return "-fx-background-color:transparent; -fx-text-fill:white;" +
                "-fx-border-color:" + BORDER + "; -fx-border-radius:4; -fx-padding:6 8;";
    }

    private String activeStyle() {
        return "-fx-background-color:" + CARD2 + "; -fx-text-fill:white;" +
                "-fx-border-color:" + YELLOW + "; -fx-border-radius:4; -fx-padding:6 8;";
    }

    private String cardStyle() {
        return "-fx-background-color:" + DARK_CARD + "; -fx-background-radius:12;" +
                "-fx-border-color:" + BORDER + "; -fx-border-radius:12;";
    }

    private void applyNotifStyle(VBox card, boolean read) {
        card.setStyle(read
                ? "-fx-background-color:" + DARK_CARD + "; -fx-background-radius:10;" +
                "-fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-border-width:1;"
                : "-fx-background-color:#1c1a00; -fx-background-radius:10;" +
                "-fx-border-color:" + YELLOW + "; -fx-border-radius:10; -fx-border-width:0 0 0 3;"
        );
    }

    private void pickAvatar(Stage stage, ImageView profile, ImageView nav) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Profile Photo");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fc.showOpenDialog(stage);
        if (file != null) {
            Image img = new Image(file.toURI().toString());
            profile.setImage(img);
            nav.setImage(img);
        }
    }
}

