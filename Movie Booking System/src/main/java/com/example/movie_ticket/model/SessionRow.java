package com.example.movie_ticket.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SessionRow {
    private StringProperty show_id;
    private StringProperty movieTitle;
    private StringProperty cinema_name;
    private StringProperty hall;
    private StringProperty showtime;
    private StringProperty price;

    public SessionRow(String show_id, String movieTitle, String cinema, String hall, String showtime) {
        this.show_id = new SimpleStringProperty(show_id);
        this.movieTitle = new SimpleStringProperty(movieTitle);
        this.cinema_name = new SimpleStringProperty(cinema);
        this.hall = new SimpleStringProperty(hall);
        this.showtime = new SimpleStringProperty(showtime);
        if (hall.equals("Hall 1")) {
            price = new SimpleStringProperty(String.valueOf(15));
        } else if (hall.equals("Hall 2")) {
            price = new SimpleStringProperty(String.valueOf(15));
        } else if (hall.equals("Hall 3")) {
            price = new SimpleStringProperty(String.valueOf(20));
        } else price = new SimpleStringProperty(String.valueOf(15));
    }

    public String getShow_id() {
        return show_id.get();
    }
    public StringProperty show_idProperty() {return show_id;}
    public StringProperty movieTitleProperty() {return movieTitle;}
    public StringProperty cinemaProperty() {return cinema_name;}
    public StringProperty hallProperty() {return hall;}
    public StringProperty showtimeProperty() {return showtime;}
}
