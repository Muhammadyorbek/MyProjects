package com.example.movie_ticket.ui;

import com.example.movie_ticket.database.ShopDAO;
import com.example.movie_ticket.database.UserDAO;
import com.example.movie_ticket.model.Cart;
import com.example.movie_ticket.model.Person;
import com.example.movie_ticket.model.Product;
import com.example.movie_ticket.service.ShopService;
import com.example.movie_ticket.service.UserSession;
import com.example.movie_ticket.ui.screens.UI;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class ShopFx extends Application {

    private final ShopDAO shopDAO = new ShopDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ShopService shopService = new ShopService();
    private Label balanceLabel;

    @Override
    public void start(Stage stage) {

        final Person loggedInUser = createCurrentUser();
        if (loggedInUser == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Shop");
            alert.setHeaderText(null);
            alert.setContentText("Please log in before opening the shop.");
            alert.showAndWait();
            stage.close();
            return;
        }

        Cart cart = new Cart();



        TilePane grid = new TilePane();
        grid.setPrefColumns(4);
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20));

        List<Product> products = shopDAO.getAllProducts();

        System.out.println("Loaded products: " + products.size());

        for (Product p : products) {

            VBox card = new VBox(10);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-border-color: black; -fx-padding: 10;");
            card.setAlignment(Pos.CENTER);

            ImageView imageView = new ImageView();
            try {
                Image image = new Image(
                        getClass().getResource(p.getImagePath()).toExternalForm()
                );
                imageView.setImage(image);
            } catch (Exception e) {
                System.out.println("Image not found: " + p.getImagePath());
            }

            imageView.setFitWidth(120);
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(true);

            Label name = UI.createLabel1(p.getName(), UI.getLightText());
            Label price = UI.createLabel1("$" + p.getPrice(), UI.getLightText());
            Label qty = UI.createLabel1("Stock: " + p.getQuantity(), UI.getLightText());

            Button addToCart = UI.createCustomButton2("Add to cart");

            addToCart.setOnAction(e -> {

                int availableStock = shopDAO.getQuantity(p.getId());

                int alreadyInCart = 0;

                for (Product cartProduct : cart.getItems()) {
                    if (cartProduct.getId() == p.getId()) {
                        alreadyInCart++;
                    }
                }

                if (alreadyInCart >= availableStock) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Out of Stock");
                    alert.setHeaderText(null);
                    alert.setContentText(
                            "You cannot add more of this item.\n\n" +
                                    "Item: " + p.getName() + "\n" +
                                    "Available stock: " + availableStock + "\n" +
                                    "Already in cart: " + alreadyInCart
                    );
                    alert.showAndWait();
                    return;
                }

                shopService.buyProduct(cart, p);

                qty.setText("Stock: " + availableStock + " | In cart: " + (alreadyInCart + 1));
            });

            card.getChildren().addAll(imageView, name, price, qty, addToCart);

            grid.getChildren().add(card);
        }

        balanceLabel = new Label(
                "Balance: $" + String.format("%.2f", loggedInUser.getBalance())
        );

        balanceLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2E7D32;"
        );

        Button reloadButton = UI.createCustomButton2("Reload");
        reloadButton.setStyle(reloadButton.getStyle() + " -fx-border-width: 1px;");

        reloadButton.setOnAction(e -> refreshUserBalance(loggedInUser));

        HBox topBar = new HBox(20, balanceLabel, reloadButton);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: " + UI.getYellow());

        Button showCartButton = UI.createCustomButton1("Show Cart");
        Button myItemsButton = UI.createCustomButton1("My Items");

        showCartButton.setOnAction(e -> showCart(cart, loggedInUser));

        myItemsButton.setOnAction(e -> {
            loggedInUser.setOwnedProducts(userDAO.getUserProducts(loggedInUser.getID()));
            showMyItems(loggedInUser);
        });

        HBox buttonBar = new HBox(10, showCartButton, myItemsButton);
        buttonBar.setAlignment(Pos.CENTER);

        VBox page = new VBox(20);
        page.setPadding(new Insets(10));
        page.getChildren().addAll(topBar, buttonBar, grid);


        page.setBackground(
                new Background(
                        new BackgroundFill(
                                UI.gradient1,
                                CornerRadii.EMPTY,
                                Insets.EMPTY
                        )
                )
        );

        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + UI.getYellow() + ";");

        Scene scene = new Scene(scrollPane, 1200, 800);

        stage.setScene(scene);
        stage.setTitle("Shop");
        stage.show();
    }

    private Person createCurrentUser() {
        UserSession session = UserSession.getInstance();
        if (!session.isLoggedIn()) {
            return null;
        }

        String userId = String.valueOf(session.getUserId());
        double balance = userDAO.getBalance(userId);
        session.setCoinsBalance(balance);

        String firstName = session.getFirstName() == null ? "" : session.getFirstName();
        String lastName = session.getLastName() == null ? "" : session.getLastName();
        String phone = session.getPhone() == null ? "" : session.getPhone();

        Person user = new Person(userId, firstName, lastName, null, null, phone, balance);
        user.setOwnedProducts(userDAO.getUserProducts(userId));
        return user;
    }

    private void refreshUserBalance(Person user) {
        double balance = userDAO.getBalance(user.getID());
        user.setBalance(balance);
        UserSession.getInstance().setCoinsBalance(balance);

        if (balanceLabel != null) {
            balanceLabel.setText("Balance: $" + String.format("%.2f", balance));
        }
    }

    private void showCart(Cart cart, Person user) {

        Stage stage = new Stage();

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setStyle(UI.getYellow());

        Label title = new Label("Your Cart");

        ListView<String> list = new ListView<>();

        for (Product p : cart.getItems()) {
            list.getItems().add(p.getName() + " - $" + p.getPrice());
        }

        Button buy = new Button("Buy");
        Button clear = new Button("Clear Cart");

        buy.setOnAction(e -> {
            boolean ok = shopService.confirmPurchase(user, cart);
            if (!ok) return;

            list.getItems().clear();
            if (balanceLabel != null) {
                balanceLabel.setText("Balance: $" + String.format("%.2f", user.getBalance()));
            }
        });

        clear.setOnAction(e -> {
            cart.clearCart();
            list.getItems().clear();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cart");
            alert.setHeaderText(null);
            alert.setContentText("Cart cleared successfully.");
            alert.showAndWait();
        });

        HBox buttons = new HBox(10, buy, clear);
        buttons.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, list, buttons);

        stage.setScene(new Scene(root, 300, 400));
        stage.setTitle("Cart");
        stage.show();
    }

    private void showMyItems(Person user) {

        Stage stage = new Stage();

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label title = new Label("My Items");
        Label localBalance = new Label("Balance: $" + String.format("%.2f", user.getBalance()));

        ObservableList<Product> owned = FXCollections.observableArrayList(user.getOwnedProducts());
        ListView<Product> list = new ListView<>(owned);

        list.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label name = new Label(item.getName());
                Label price = new Label("$" + String.format("%.2f", item.getPrice()));

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button ret = new Button("Return");
                Product currentItem = item;
                ret.setOnAction(e -> {
                    boolean ok = shopService.returnProduct(user, currentItem);
                    if (!ok) return;

                    owned.remove(currentItem);

                    String newBalance = "Balance: $" + String.format("%.2f", user.getBalance());
                    localBalance.setText(newBalance);
                    if (balanceLabel != null) balanceLabel.setText(newBalance);
                });

                HBox row = new HBox(10, name, price, spacer, ret);
                row.setAlignment(Pos.CENTER_LEFT);

                setText(null);
                setGraphic(row);
            }
        });

        root.getChildren().addAll(title, localBalance, list);

        stage.setScene(new Scene(root, 420, 500));
        stage.setTitle("My Items");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
