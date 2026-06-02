package com.example.movie_ticket.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private String phone;
    private String ID;
    private String name;
    private String surname;
    private Role role;
    private Account account;
    private double balance;
    private List<Product> ownedProducts;


    public Person(String ID, String name, String surname, Role role, Account account, String phone, double balance) {
        this.ID = ID;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.account = account;
        this.phone = phone;
        this.balance = balance;
        this.ownedProducts = new ArrayList<>();
    }

    public Person(String ID, String name, String surname, Role role, Account account, String phone) {
        this.ID = ID;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.account = account;
        this.phone = phone;
        this.ownedProducts = new ArrayList<>();
    }



    public List<Product> getOwnedProducts() {
        return ownedProducts;
    }
    public String getName() {return name;}
    public String getSurname() {return surname;}
    public Role getRole() {return role;}
    public Account getAccount() {return account;}
    public String getID() {return ID;}
    public String getPhone() {return phone;}
    public double getBalance() {return balance;}

    public void setBalance(double balance) {this.balance = balance;}
    public StringProperty idProperty() {return new SimpleStringProperty(ID);}
    public StringProperty nameProperty() {return new SimpleStringProperty(name);}
    public StringProperty surnameProperty() {return new SimpleStringProperty(surname);}
    public StringProperty phoneProperty() {return new SimpleStringProperty(phone);}


    public void setOwnedProducts(List<Product> ownedProducts) {
        this.ownedProducts = ownedProducts;
    }

    public void addOwnedProduct(Product product) {
        this.ownedProducts.add(product);
    }

    public void removeOwnedProduct(Product product) {
        this.ownedProducts.remove(product);
    }
}
