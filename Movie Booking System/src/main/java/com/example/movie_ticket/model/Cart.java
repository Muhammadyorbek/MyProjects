package com.example.movie_ticket.model;



import java.util.ArrayList;
import java.util.List;

public class Cart {

    private List<Product> items = new ArrayList<>();

    public void addProduct(Product product) {
        items.add(product);
        System.out.println("Added product " + product.getName() + " to cart");
        for (Product p : items) {
            System.out.println(p.getName() + " " + p.getPrice());
        }
    }

    public void removeProduct(Product product) {
        items.remove(product);
    }

    public List<Product> getItems() {
        return items;
    }

    public double getTotalPrice() {
        double total = 0;

        for (Product product : items) {
            total += product.getPrice();
        }

        return total;
    }

    public void clearCart() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}