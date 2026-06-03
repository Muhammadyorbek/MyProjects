package com.example.movie_ticket.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CustomerRow {
    private final StringProperty id;
    private final StringProperty registeredDate;
    private final StringProperty bookedTickets;
    private final StringProperty blocked;
    private final StringProperty email;
    private final StringProperty login;

    public CustomerRow(String id, String registeredDate, String email, String login, String bookedTickets, String blocked) {
        this.id = new SimpleStringProperty(id);
        this.registeredDate = new SimpleStringProperty(registeredDate);
        this.bookedTickets = new SimpleStringProperty(bookedTickets);
        this.blocked = new SimpleStringProperty(blocked);
        this.email = new SimpleStringProperty(email);
        this.login = new SimpleStringProperty(login);
    }
    public CustomerRow(String id, String registeredDate, String bookedTickets, String blocked) {
        this.id = new SimpleStringProperty(id);
        this.registeredDate = new SimpleStringProperty(registeredDate);
        this.bookedTickets = new SimpleStringProperty(bookedTickets);
        this.blocked = new SimpleStringProperty(blocked);
        email = new SimpleStringProperty("");
        login = new SimpleStringProperty("");
    }


    public StringProperty idProperty() { return id; }
    public StringProperty registeredDateProperty() { return registeredDate; }
    public StringProperty bookedTicketsProperty() { return bookedTickets; }
    public StringProperty blockedProperty() { return blocked; }
    public StringProperty emailProperty() { return email; }
    public StringProperty loginProperty() { return login; }

    public String getId() { return id.get(); }

    public String getBlocked() { return blocked.get(); }
    public void setBlocked(String value) { this.blocked.set(value); }

}