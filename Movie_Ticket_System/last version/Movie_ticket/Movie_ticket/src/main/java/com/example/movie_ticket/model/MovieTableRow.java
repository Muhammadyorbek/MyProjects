package com.example.movie_ticket.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MovieTableRow {
    public final StringProperty id;
    public final StringProperty name;
    public final StringProperty registeredDate;
    public final StringProperty cinema;
    public final StringProperty showtime;
    public final StringProperty status;

    public MovieTableRow(String id, String name, String registeredDate, String cinema, String showtime, String status) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.registeredDate = new SimpleStringProperty(registeredDate);
        this.cinema = new SimpleStringProperty(cinema);
        this.showtime = new SimpleStringProperty(showtime);
        this.status = new SimpleStringProperty(status);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty registeredDateProperty() {
        return registeredDate;
    }

    public StringProperty cinemaProperty() {
        return cinema;
    }

    public StringProperty showtimeProperty() {
        return showtime;
    }

    public StringProperty statusProperty() {
        return status;
    }
}