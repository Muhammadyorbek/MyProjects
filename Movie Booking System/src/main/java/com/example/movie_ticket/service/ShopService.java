package com.example.movie_ticket.service;

import com.example.movie_ticket.database.ShopDAO;
import com.example.movie_ticket.database.UserDAO;
import com.example.movie_ticket.model.Cart;
import com.example.movie_ticket.model.Person;
import com.example.movie_ticket.model.Product;
import javafx.scene.control.Alert;


public class ShopService {

    private ShopDAO shopDAO = new ShopDAO();
    private UserDAO userDAO = new UserDAO();

    public boolean buyProduct(Cart cart, Product product) {

        int stock = shopDAO.getQuantity(product.getId());

        if (stock <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Purchase Error");
            alert.setContentText("Product is out of stock.");
            alert.showAndWait();
            return false;
        }

        cart.addProduct(product);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Added to cart");
        alert.setContentText(product.getName() + " added to cart");
        alert.showAndWait();

        return true;
    }

    public boolean confirmPurchase(Person user, Cart cart) {

        double total = cart.getTotalPrice();

        if (user.getBalance() < total) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Not enough balance");
            alert.showAndWait();
            return false;
        }

        double newBalance = user.getBalance() - total;
        user.setBalance(newBalance);
        userDAO.updateBalance(user.getID(), newBalance);
        UserSession.getInstance().setCoinsBalance(newBalance);

        for (Product p : cart.getItems()) {
            user.getOwnedProducts().add(p);
            userDAO.savePurchase(user.getID(), p.getId());

            shopDAO.reduceQuantity(p.getId());
        }

        cart.clearCart();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Purchase successful!");
        alert.showAndWait();

        return true;
    }

    public boolean returnProduct(Person user, Product product) {

        if (!user.getOwnedProducts().contains(product)) {
            return false;
        }

        user.getOwnedProducts().remove(product);
        double newBalance = user.getBalance() + product.getPrice();
        user.setBalance(newBalance);
        userDAO.updateBalance(user.getID(), newBalance);
        userDAO.deletePurchase(user.getID(), product.getId());
        UserSession.getInstance().setCoinsBalance(newBalance);

        shopDAO.increaseQuantity(product.getId());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Returned successfully");
        alert.showAndWait();

        return true;
    }
}
