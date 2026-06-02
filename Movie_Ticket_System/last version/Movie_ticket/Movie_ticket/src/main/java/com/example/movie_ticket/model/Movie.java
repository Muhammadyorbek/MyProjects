package com.example.movie_ticket.model;

public class Movie {
    private int id;
    private String title;
    private String genre;
    private String language;
    private int duration;
    private int releaseYear;
    private String rating;
    private double price;
    private String imageUrl;
    private String description;

    public static final String STATUS_EXISTS = "Exists";
    public static final String STATUS_CANCELED = "Canceled";
    public static final String STATUS_DELETED = "Deleted";

    public Movie() {}

    public Movie(int id, String title, String genre, String language, int duration,
                 int releaseYear, String rating, double price, String imageUrl, String description) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.language = language;
        this.duration = duration;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getLanguage() { return language; }
    public int getDuration() { return duration; }
    public int getReleaseYear() { return releaseYear; }
    public String getRating() { return rating; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }

    public void setTitle(String title) { this.title = title; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setLanguage(String language) { this.language = language; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
    public void setRating(String rating) { this.rating = rating; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setDescription(String description) { this.description = description; }

    public void setYear(int releaseYear) { this.releaseYear = releaseYear; }



    public String getTimes() {
        return "10:00, 14:00, 18:00";
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId(){return id;}

}