package com.example.movie_ticket.model;


public class Product {

    private int id;
    private String name;
    private double price;
    private String imagePath;
    private int quantity;

    public Product(int id, String name, double price, String imagePath, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getQuantity() {
        return quantity;
    }
}