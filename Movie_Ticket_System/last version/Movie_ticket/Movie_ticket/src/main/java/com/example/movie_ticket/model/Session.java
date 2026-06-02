package com.example.movie_ticket.model;

import java.sql.Timestamp;

public class Session {

    private int id;
    private int movieId;
    private String movieTitle;
    private String cinemaName;
    private Timestamp showTime;
    private String hall;

    public Session(int id, int movieId, String movieTitle, String hall, String cinemaName, Timestamp showTime) {
        this.id = id;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.cinemaName = cinemaName;
        this.showTime = showTime;
        this.hall = hall;
    }
}