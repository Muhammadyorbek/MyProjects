package com.example.movie_ticket.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AdminMovieRow {
    private final StringProperty id;
    private final StringProperty title;
    private final StringProperty releaseDate;
    private final StringProperty status;
    private final StringProperty genre;

    public AdminMovieRow(String id, String title, String releaseDate, String status, String genre) {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.releaseDate = new SimpleStringProperty(releaseDate);
        this.status = new SimpleStringProperty(status);
        this.genre = new SimpleStringProperty(genre);
    }

    public String getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getReleaseDate() { return releaseDate.get(); }
    public String getGenre() {return genre.get();}
    public String getStatus() { return status.get(); }

    public void setId(String id) { this.id.set(id); }
    public void setTitle(String title) { this.title.set(title); }

    public void setReleaseDate(String releaseDate) { this.releaseDate.set(releaseDate); }
    public void setStatus(String status) { this.status.set(status); }



    public StringProperty idProperty() { return id; }
    public StringProperty nameProperty() { return title; }
    public StringProperty genreProperty() {return genre;}
    public StringProperty registeredDateProperty() { return releaseDate; }
    public StringProperty statusProperty() { return status; }
}